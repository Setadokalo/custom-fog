package setadokalo.customfog;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

@Environment(EnvType.CLIENT)
public class CustomFog implements ClientModInitializer {
    static Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "custom-fog";
    public static final String MOD_NAME = "Custom Fog";
    public static CustomFogConfig config;
    static File file;

    @Override
    public void onInitializeClient() {
        log(Level.INFO, "Initializing");

//        AutoConfig.register(CustomFogConfig.class, JanksonConfigSerializer::new);
//        config = AutoConfig.getConfigHolder(CustomFogConfig.class).getConfig();
        config = new CustomFogConfig();
    }

    public static void log(Level level, String message){
        LOGGER.log(level, "["+MOD_NAME+"] " + message);
    }
}
