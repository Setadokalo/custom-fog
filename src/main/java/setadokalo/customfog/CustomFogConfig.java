package setadokalo.customfog;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomFogConfig {
	private CustomFogConfig() {}
	public static CustomFogConfig getConfig() {
		File file = new File(FabricLoader.getInstance().getConfigDir().toString(), CustomFog.MOD_ID + ".toml");
		if (file.exists()) {
			Toml config = new Toml().read(file);
			return config.to(CustomFogConfig.class);
		} else {
			CustomFogConfig config = new CustomFogConfig();
			config.file = file;
			config.dimensions.put("minecraft:the_nether", new DimensionConfig(false, FogType.LINEAR, LINEAR_START, LINEAR_END, EXP, EXP2));
			config.saveConfig();
			return config;
		}
	}
	
	public void saveConfig() {
		TomlWriter tWr = new TomlWriter();
		try {
			tWr.write(this, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private transient File file;
	public static final float LINEAR_START = 0.25F;
	public static final float LINEAR_END = 1.00F;
	public static final float EXP = 3.00F;
	public static final float EXP2 = 1.75F;
	public DimensionConfig defaultConfig = new DimensionConfig(true, FogType.LINEAR, LINEAR_START, LINEAR_END, EXP, EXP2);
	public Map<String, DimensionConfig> dimensions = new HashMap<String, DimensionConfig>();
	public enum FogType {
		LINEAR,
		EXPONENTIAL,
		EXPONENTIAL_TWO,
	}
}
