package setadokalo.customfog.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.fabricmc.loader.api.FabricLoader;
import setadokalo.customfog.CustomFog;

import java.io.*;
import java.util.*;

public class CustomFogConfig {
	private static final Gson GSON = new GsonBuilder()
		.registerTypeAdapter(Identifier.class, new Identifier.Serializer())
		.enableComplexMapKeySerialization()
		.setPrettyPrinting()
		.create();
	private static class OldTomlConfig {
		public static final float LINEAR_START = 0.25F;
		public static final float LINEAR_END = 1.00F;
		public static final float EXP = 3.00F;
		public static final float EXP2 = 1.75F;
		@NotNull
		public DimensionConfig defaultConfig = new DimensionConfig(true, FogType.LINEAR, LINEAR_START, LINEAR_END, EXP, EXP2);
		public Map<String, DimensionConfig> dimensions = new HashMap<>();
	}

	public CustomFogConfig() {}
	public CustomFogConfig(File file) {
		this.file = file;
	}

	@Nullable
	public static CustomFogConfig getConfig() {
		CustomFog.log(Level.INFO, "Loading config file");
		File file = new File(FabricLoader.getInstance().getConfigDir().toString(), CustomFog.MOD_ID + ".json");
		if (file.exists()) {
			try {
				CustomFogConfig c = GSON.fromJson(new FileReader(file), CustomFogConfig.class);
				if (c == null) {
					c = new CustomFogConfig(file);
					c.saveConfig();
				} else {
					c.file = file;
				}
				return c;
			} catch (FileNotFoundException | JsonSyntaxException e) {
				return null;
			}
		} else {
			File tomlFile = new File(FabricLoader.getInstance().getConfigDir().toString(), CustomFog.MOD_ID + ".toml");
			if (tomlFile.exists()) {
				try {
					Toml configToml = new Toml().read(tomlFile);
					OldTomlConfig oldConfig = configToml.to(OldTomlConfig.class);
					CustomFogConfig config = new CustomFogConfig(file);
					config.defaultConfig = oldConfig.defaultConfig;
					// convert the string keys from the toml config to the new json config
					Map<String, DimensionConfig> newDimensions = new HashMap<>();
					for (Map.Entry<String, DimensionConfig> row : oldConfig.dimensions.entrySet()) {
						String strippedKey = row.getKey().substring(1, row.getKey().length() - 1);
						config.dimensions.put(new Identifier(strippedKey), row.getValue());
						newDimensions.put(strippedKey, row.getValue());
					}
					oldConfig.dimensions = newDimensions;
					// prepend a deprecation warning to the outdated config file
					try (FileWriter writer = new FileWriter(tomlFile)) {
						writer.write("" +
							"# WARNING: This config file is now deprecated, in a future version support for converting from it will be disabled.\n" +
							"# This config file will no longer be read from. To configure Custom Fog, use the new `custom-fog.json` file instead.\n"
						);
						new TomlWriter().write(oldConfig, writer);
					} catch (IOException e) {
						e.printStackTrace();
					}
					config.saveConfig();
					return config;
				} catch (IllegalStateException e) {
					return null;
				}
			} else {
				CustomFogConfig config = new CustomFogConfig(file);
				config.dimensions.put(new Identifier("minecraft:the_nether"),
					new DimensionConfig(false, FogType.LINEAR, LINEAR_START, LINEAR_END, EXP, EXP2));
				config.saveConfig();
				return config;
			}
		}
	}

	public static void add(CustomFogConfig config, Identifier key, DimensionConfig dimCfg) {
		config.dimensions.put(key, dimCfg);
	}

	public static void changeKey(CustomFogConfig config, Identifier originalKey, Identifier newKey) {
			DimensionConfig dConfig = config.dimensions.get(originalKey);
			if (dConfig == null) {
				throw new NullPointerException("Key invalid");
			}
			config.dimensions.remove(originalKey);
			config.dimensions.put(newKey, dConfig);
		}
	public void saveConfig() {
		if (file == null) {
			CustomFog.log(Level.WARN, "File for config was null: this should not happen with the normal mod");
			return;
		}
		String serialized = GSON.toJson(this);
		try (FileWriter fR = new FileWriter(file)) {
			fR.write(serialized);
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

	// should not be exposed in the config files, I think it was previously though oops
	@Nullable
	public transient DimensionConfig overrideConfig = null;
	public Map<Identifier, DimensionConfig> dimensions = new HashMap<>();
	public boolean videoOptionsButton = true;


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
