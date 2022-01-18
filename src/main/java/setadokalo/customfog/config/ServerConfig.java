package setadokalo.customfog.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import setadokalo.customfog.CustomFog;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ServerConfig {
	private static final Gson GSON = new GsonBuilder()
		.registerTypeAdapter(Identifier.class, new Identifier.Serializer())
		.enableComplexMapKeySerialization()
		.serializeNulls()
		.setPrettyPrinting()
		.create();

	public ServerConfig() {}

	public ServerConfig(File file) {
		this.file = file;
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

	public String serialize() {
		return GSON.toJson(this);
	}
	public static ServerConfig deserialize(String s) {
		return GSON.fromJson(s, ServerConfig.class);
	}

	private transient File file;

	public boolean baseModAllowed = true;
	//TODO: Implement lava configuration

	@Nullable
	public DimensionConfig defaultOverride = null;
	@Nullable
	public DimensionConfig waterOverride = null;
	@Nullable
	public DimensionConfig snowOverride = null;
	@Nullable
	public DimensionConfig universalOverride = null;

	public Map<Identifier, DimensionConfig> overrides = new HashMap<>();

	@NotNull
	public static ServerConfig getConfig() {
		CustomFog.log(Level.INFO, "Loading server config file");
		File file = new File(FabricLoader.getInstance().getConfigDir().toString(), CustomFog.MOD_ID + "-server.json");
		if (file.exists()) {
			try {
				ServerConfig c = GSON.fromJson(new FileReader(file), ServerConfig.class);
				if (c == null) {
					c = new ServerConfig(file);
					c.saveConfig();
				} else {
					c.file = file;
				}
				return c;
			} catch (FileNotFoundException | JsonSyntaxException e) {
				return new ServerConfig(file);
			}
		} else {
			ServerConfig config = new ServerConfig(file);
			config.saveConfig();
			return config;
		}
	}

//	public enum TriState {
//		ALLOWED,
//		NEUTRAL,
//		DENIED;
//		public boolean asOptIn() {
//			return this == ALLOWED;
//		}
//		public boolean asOptOut() {
//			return this != DENIED;
//		}
//	}
}
