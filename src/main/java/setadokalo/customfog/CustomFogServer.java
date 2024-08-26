package setadokalo.customfog;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.S2CPlayChannelEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import setadokalo.customfog.CustomFog.CFServerConfigPayload;
import setadokalo.customfog.config.ConfigLoader;
import setadokalo.customfog.config.DimensionConfig;
import setadokalo.customfog.config.ServerConfig;

public class CustomFogServer implements DedicatedServerModInitializer {
	public static ServerConfig config = ServerConfig.getConfig();

	@Override
	public void onInitializeServer() {
		CustomFogLogger.info( "Initializing packet sender");
		S2CPlayChannelEvents.REGISTER.register((handler, sender, server, channels) -> {
			if (channels.contains(CustomFog.SERVER_CONFIG_PACKET_ID)) {
				ServerPlayNetworking.send(
					handler.player,
					new CustomFog.CFServerConfigPayload(ConfigLoader.serialize(config))
				);
				CustomFogLogger.info( "Sending packet");
			} else {
				CustomFogLogger.info("Client does not support customfog");
			}
		});
		ServerPlayNetworking.registerGlobalReceiver(CustomFog.UpdateServerConfigPayload.ID,
			(payload, context) -> {
				ServerPlayerEntity player = context.player();
				if (player.hasPermissionLevel(3)) {
					Identifier dimId = payload.dimId();
					DimensionConfig dimConf = payload.config();
					switch (dimId.toString()) {
						case CustomFog.WATER_CONFIG ->
							config.waterOverride = dimConf;
						case CustomFog.POWDER_SNOW_CONFIG ->
							config.snowOverride = dimConf;
						case CustomFog.DEFAULT_CONFIG ->
							config.defaultOverride = dimConf;
						case CustomFog.UNIVERSAL_CONFIG ->
							config.universalOverride = dimConf;
						default ->
							config.overrides.put(dimId, dimConf);
					};
					config.saveConfig();
					sendUpdatedConfig(player.server);
				}
			}
		);
		CommandRegistrationCallback.EVENT.register(
			(dis, ded, __) -> dis.register(CommandManager.literal("customfog")
				.requires(source -> source.hasPermissionLevel(4))
				.then(CommandManager.literal("reload").executes(CustomFogServer::customFogReload))
			)
		);
	}
	private static int customFogReload(CommandContext<ServerCommandSource> ctx) {
		config = ServerConfig.getConfig();
		sendUpdatedConfig(ctx.getSource().getServer());
		ctx.getSource().sendFeedback(
				() -> Text.translatable("modid.customfog").formatted(Formatting.GOLD)
				.append(Text.translatable("chat.customfog.reloaded").formatted(Formatting.YELLOW)),
			true);
		return 0;
	}

	private static void sendUpdatedConfig(MinecraftServer server) {
		String serialized = ConfigLoader.serialize(config);
		for (ServerPlayerEntity entity : PlayerLookup.all(server)) {
			if (ServerPlayNetworking.canSend(entity, CustomFog.SERVER_CONFIG_PACKET_ID))
				ServerPlayNetworking.send(entity, new CFServerConfigPayload(serialized));
		}
	}
}
