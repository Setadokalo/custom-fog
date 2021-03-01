package setadokalo.customfog;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomFogConfig {
	private CustomFogConfig() {
	}

	public static CustomFogConfig getConfig() {
		CustomFog.log(Level.INFO, "Loading config file");
		File file = new File(FabricLoader.getInstance().getConfigDir().toString(), CustomFog.MOD_ID + ".toml");
		if (file.exists()) {
			Toml configToml = new Toml().read(file);
			CustomFogConfig config = configToml.to(CustomFogConfig.class);
			config.file = file;
			List<String> copy = new ArrayList<>();
			config.dimensions.keySet().stream().forEach(copy::add);
			for (String key : copy) {
				String strippedKey = key.substring(1, key.length() - 1);
				changeKey(config, key, strippedKey);
			}
			return config;
		} else {
			CustomFogConfig config = new CustomFogConfig();
			config.file = file;
			config.dimensions.put("minecraft:the_nether",
					new DimensionConfig(false, FogType.LINEAR, LINEAR_START, LINEAR_END, EXP, EXP2));
			config.saveConfig();
			return config;
		}
	}

	public static void changeKey(CustomFogConfig config, String originalKey, String newKey) {
		DimensionConfig dConfig = config.dimensions.get(originalKey);
		if (dConfig == null) {
			throw new NullPointerException("Key invalid");
		}
		config.dimensions.remove(originalKey);
		config.dimensions.put(newKey, dConfig);
	}

	public static void add(CustomFogConfig config, String key, DimensionConfig dimCfg) {
		config.dimensions.put(key, dimCfg);
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
	@NotNull
	public DimensionConfig defaultConfig = new DimensionConfig(true, FogType.LINEAR, LINEAR_START, LINEAR_END, EXP, EXP2);
	@Nullable
	public DimensionConfig overrideConfig = null;
	public Map<String, DimensionConfig> dimensions = new HashMap<>();
	public enum FogType {
		LINEAR,
		EXPONENTIAL,
		EXPONENTIAL_TWO,
		NONE
		;

		/**
		 * Gets the next FogType in the enum, allowing for easily cycling between them.
		 * @return The next FogType.
		 */
		public FogType next() {
			if (this.equals(FogType.LINEAR))
				return FogType.EXPONENTIAL;
			else if (this.equals(FogType.EXPONENTIAL))
				return FogType.EXPONENTIAL_TWO;
			else if (this.equals(FogType.EXPONENTIAL_TWO))
				return FogType.NONE;
			else if (this.equals(FogType.NONE))
				return FogType.LINEAR;
			return null;
		}
	}
}
