package setadokalo.customfog;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

import com.google.gson.GsonBuilder;

import setadokalo.customfog.config.ConfigLoader;

public class CustomFog implements ModInitializer {
	public static final String MOD_ID = CustomFogLogger.MOD_ID;

	public static final Identifier SERVER_CONFIG_PACKET_ID = new Identifier(CustomFog.MOD_ID, "server_config");
	public static final Identifier OP_UPDATE_CONFIG_PACKET_ID = new Identifier(CustomFog.MOD_ID, "op_update_config");

	public static final String WATER_CONFIG = "_customfog_internal:__/water/__";
	public static final String POWDER_SNOW_CONFIG = "_customfog_internal:__/snow/__";
	public static final String DEFAULT_CONFIG = "_customfog_internal:__/default/__";
	public static final String UNIVERSAL_CONFIG = "_customfog_internal:__/universal/__";

	@Override
	public void onInitialize() {
		CustomFogLogger.info( "Initializing Custom Fog");
		ConfigLoader.GSON = new GsonBuilder()
		.registerTypeAdapter(Identifier.class, new Identifier.Serializer())
		.enableComplexMapKeySerialization()
		.serializeNulls()
		.setPrettyPrinting()
		.create();
	}

}

