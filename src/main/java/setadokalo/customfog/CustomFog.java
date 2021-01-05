package setadokalo.customfog;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Environment(EnvType.CLIENT)
public class CustomFog implements ClientModInitializer {
	static Logger LOGGER = LogManager.getLogger();

	public static final String MOD_ID = "custom-fog";
	public static final String MOD_NAME = "Custom Fog";
	protected static final String LOG_STRING = "[" + MOD_NAME + "] {}"; 
	public static CustomFogConfig config;

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
}
