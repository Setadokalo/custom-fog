package setadokalo.customfog;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.CommandException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.Level;
import setadokalo.customfog.config.ServerConfig;

public class CustomFogServer implements DedicatedServerModInitializer {
	public static ServerConfig config;

	@Override
	public void onInitializeServer() {
		config = ServerConfig.getConfig();
		CustomFog.log(Level.INFO, "Initializing packet sender");
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
			if (ServerPlayNetworking.canSend(handler, CustomFog.SERVER_CONFIG_PACKET_ID))
				ServerPlayNetworking.send(
					handler.player,
					CustomFog.SERVER_CONFIG_PACKET_ID,
					PacketByteBufs.create().writeString(config.serialize())
				);
			CustomFog.log(Level.INFO, "Sending packet");
		});
		CommandRegistrationCallback.EVENT.register((dis, ded) -> {
			dis.register(CommandManager.literal("customfog")
				.requires(source -> source.hasPermissionLevel(4))
				.then(CommandManager.literal("reload").executes(CustomFogServer::customFogReload)));
		});
	}
	private static int customFogReload(CommandContext<ServerCommandSource> ctx) {
		config = ServerConfig.getConfig();
		if (config == null)
			throw new CommandException(new LiteralText("Invalid Config File"));
		String serialized =	config.serialize();
		for (ServerPlayerEntity entity : ctx.getSource().getServer().getPlayerManager().getPlayerList()) {
			if (ServerPlayNetworking.canSend(entity, CustomFog.SERVER_CONFIG_PACKET_ID))
				ServerPlayNetworking.send(entity, CustomFog.SERVER_CONFIG_PACKET_ID, PacketByteBufs.create().writeString(serialized));
		}
		ctx.getSource().sendFeedback(
			new TranslatableText("modid.customfog").formatted(Formatting.GOLD)
				.append(new TranslatableText("chat.customfog.reloaded").formatted(Formatting.YELLOW)),
			true);
		return 0;
	}
}
