package setadokalo.customfog;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import setadokalo.customfog.config.gui.CustomFogConfigScreen;

public class CustomFog implements ModInitializer {
	public static final String MOD_ID = "custom-fog";
	public static final String MOD_NAME = "Custom Fog";

	public static final Identifier SERVER_CONFIG_PACKET_ID = new Identifier(CustomFog.MOD_ID, "server_config");
	public static final Identifier OP_UPDATE_CONFIG_PACKET_ID = new Identifier(CustomFog.MOD_ID, "op_update_config");

	protected static final String LOG_STRING = "[" + MOD_NAME + "] {}";
	static final Logger LOGGER = LogManager.getLogger();

	public static void log(Level level, String message) {
		LOGGER.log(level, LOG_STRING, message);
	}

	@Override
	public void onInitialize() {
		log(Level.INFO, "Initializing Custom Fog");
	}

	@Environment(EnvType.CLIENT)
	public static Screen getConfigScreen(Screen parent) {
		return new CustomFogConfigScreen(parent);
	}
}
