package setadokalo.customfog;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import setadokalo.customfog.gui.CustomFogConfigScreen;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public class CustomFog implements ClientModInitializer {
	static Logger LOGGER = LogManager.getLogger();

	public static final String MOD_ID = "custom-fog";
	public static final String MOD_NAME = "Custom Fog";
	protected static final String LOG_STRING = "[" + MOD_NAME + "] {}";
	public static CustomFogConfig config;

	public static boolean modMenuPresent = false;

	@Override
	public void onInitializeClient() {
		log(Level.INFO, "Initializing");
		// AutoConfig.register(CustomFogConfig.class, JanksonConfigSerializer::new);
		// config = AutoConfig.getConfigHolder(CustomFogConfig.class).getConfig();
		config = CustomFogConfig.getConfig();
	}

	public static void log(Level level, String message) {
		LOGGER.log(level, LOG_STRING, message);
	}
	public static Screen configScreen(Screen parent) {
		return new CustomFogConfigScreen(parent);
	}
}
