package customfog;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.fabricmc.api.ModInitializer;

import net.minecraft.text.Text;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CustomFog implements ModInitializer {

    public static Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "custom-fog";
    public static final String MOD_NAME = "Custom Fog";
    public static CustomFogConfig config;
    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing");

        AutoConfig.register(CustomFogConfig.class, JanksonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(CustomFogConfig.class).getConfig();
    }

    public static void log(Level level, String message){
        LOGGER.log(level, "["+MOD_NAME+"] " + message);
    }

}