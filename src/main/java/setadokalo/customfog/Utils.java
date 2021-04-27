package setadokalo.customfog;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import setadokalo.customfog.config.DimensionConfig;
import setadokalo.customfog.config.ServerConfig;

public class Utils {
	@NotNull
	private static <T> T requireNonNullElse(@Nullable T nullable, @NotNull T defaultT) {
		if (nullable != null) {
			return nullable;
		} else if (defaultT != null) {
			return defaultT;
		} else {
			throw new NullPointerException("defaultT should always be non-null");
		}
	}

	public static DimensionConfig getDimensionConfigFor(@Nullable Identifier value) {
		ServerConfig serverConfig = CustomFogClient.serverConfig;
		if (CustomFogClient.config.overrideConfig != null)
			return CustomFogClient.config.overrideConfig;
		if (serverConfig != null) {
			if (serverConfig.overrides.get(value) != null)
				return serverConfig.overrides.get(value);
			if (serverConfig.universalOverride != null)
				return serverConfig.universalOverride;
		}

		return requireNonNullElse(
			CustomFogClient.config.dimensions.get(value),
			requireNonNullElse(serverConfig == null ? null : serverConfig.defaultOverride,
				CustomFogClient.config.defaultConfig
			)
		);
	}

	public static boolean universalOverride() {
		return CustomFogClient.serverConfig != null && CustomFogClient.serverConfig.universalOverride != null;
	}
}
