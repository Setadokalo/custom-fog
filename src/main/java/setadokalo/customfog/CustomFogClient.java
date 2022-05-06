package setadokalo.customfog;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import setadokalo.customfog.config.ConfigLoader;
import setadokalo.customfog.config.CustomFogConfig;
import setadokalo.customfog.config.ServerConfig;

@Environment(EnvType.CLIENT)
public class CustomFogClient implements ClientModInitializer {
	public static CustomFogConfig config = CustomFogConfig.getConfig();
	public static ServerConfig serverConfig = null;
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(CustomFog.SERVER_CONFIG_PACKET_ID, (client, net, buf, sdr) ->
				serverConfig = ConfigLoader.deserialize(ServerConfig.class, buf.readString()));

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> serverConfig = null);
		if (FabricLoader.getInstance().isModLoaded("canvas")) {
			CustomFogLogger.log(Level.INFO, "Canvas Renderer detected, adding canvas fog shader resource pack");
			FabricLoader.getInstance().getModContainer(CustomFog.MOD_ID).ifPresent(modContainer ->
					ResourceManagerHelper.registerBuiltinResourcePack(
							new Identifier("customfog:canvasfog"),
							modContainer,
							ResourcePackActivationType.DEFAULT_ENABLED
					)
			);
		}
	}
}
