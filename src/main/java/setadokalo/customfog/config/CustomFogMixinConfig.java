package setadokalo.customfog.config;

import org.jetbrains.annotations.NotNull;
import setadokalo.customfog.CustomFogLogger;

import java.io.File;

public class CustomFogMixinConfig extends BaseConfig {
	public CustomFogMixinConfig(File file) {
		super(file);
	}
	private static transient CustomFogMixinConfig config;

	@NotNull
	public static CustomFogMixinConfig getConfig() {
		if (config == null)
			config = ConfigLoader.getConfig(CustomFogMixinConfig.class, CustomFogLogger.MOD_ID + "-mixin");
		return config;
	}
}
