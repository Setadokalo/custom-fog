package setadokalo.customfog;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CustomFogLogger {
	public static final String MOD_ID = "custom-fog";
	public static final String MOD_NAME = "Custom Fog";
	protected static final String LOG_STRING = "[" + MOD_NAME + "] {}";
	static final Logger LOGGER = LogManager.getLogger();

	public static void log(Level level, String message) {
		LOGGER.log(level, LOG_STRING, message);
	}
}
