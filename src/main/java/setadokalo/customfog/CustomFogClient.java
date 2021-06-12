package setadokalo.customfog;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.apache.logging.log4j.Level;
import setadokalo.customfog.config.CustomFogConfig;
import setadokalo.customfog.config.ServerConfig;

import java.util.Objects;

@Environment(EnvType.CLIENT)
public class CustomFogClient implements ClientModInitializer {
	public static CustomFogConfig config;
	public static ServerConfig serverConfig = null;
	@Override
	public void onInitializeClient() {
		config = Objects.requireNonNull(CustomFogConfig.getConfig());
		ClientPlayNetworking.registerGlobalReceiver(CustomFog.SERVER_CONFIG_PACKET_ID, (client, netHandler, buf, sender) -> {
			serverConfig = ServerConfig.deserialize(buf.readString());
//			CustomFog.log(Level.INFO, "config packet received: " + buf.readString());
		});
	}
}
