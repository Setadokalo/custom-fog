package setadokalo.customfog.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import setadokalo.customfog.CustomFogLogger;

import java.io.*;
import java.lang.reflect.InvocationTargetException;

public class ConfigLoader {
	private static final Gson GSON = new GsonBuilder()
		.registerTypeAdapter(Identifier.class, new Identifier.Serializer())
		.enableComplexMapKeySerialization()
		.serializeNulls()
		.setPrettyPrinting()
		.create();

	@NotNull
	public static <T extends BaseConfig> T getConfig(Class<T> tClass, String configName) {
		return getConfig(tClass, configName, GSON);
	}
	@NotNull
	public static <T extends BaseConfig> T getConfig(Class<T> tClass, String configName, Gson gson) {
		File tomlFile = new File(FabricLoader.getInstance().getConfigDir().toString(), configName + ".toml");
		if (tomlFile.exists()) {
			CustomFogLogger.log(Level.WARN, "Old config file detected; mod no longer supports loading this format");
		}
		CustomFogLogger.log(Level.DEBUG, "Loading config file");

		File file = new File(FabricLoader.getInstance().getConfigDir().toString(), configName + ".json");
		if (file.exists()) {
			try {
				T c = gson.fromJson(new FileReader(file), tClass);
				if (c == null) {
					c = makeConfig(tClass, file);
					saveConfig(c);
				} else {
					c.file = file;
				}
				return c;
			} catch (FileNotFoundException | JsonSyntaxException e) {
				return makeConfig(tClass, file);
			}
		} else {
			T config = makeConfig(tClass, file);
			saveConfig(config);
			return config;
		}
	}

	private static <T extends BaseConfig> T makeConfig(Class<T> tClass, File file) {
		try {
			return tClass.getConstructor(File.class).newInstance(file);
		} catch (InvocationTargetException ex) {
			throw new RuntimeException(ex.getCause());
		} catch (IllegalAccessException | NoSuchMethodException | InstantiationException ex) {
			throw new RuntimeException("Somehow class " + tClass.toString() + " extending BaseConfig did not have valid makeConfig: " + ex);
		}
	}

	public static <T extends BaseConfig> void saveConfig(T config) {
		if (config.file == null) {
			CustomFogLogger.log(Level.WARN, "File for config was null: this should not happen with the normal mod");
			return;
		}
		String serialized = GSON.toJson(config);
		try (FileWriter fR = new FileWriter(config.file)) {
			fR.write(serialized);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static String serialize(Object o) {
		return GSON.toJson(o);
	}
	public static <T extends BaseConfig> T deserialize(Class<T> tClass, String s) {
		return GSON.fromJson(s, tClass);
	}
}
