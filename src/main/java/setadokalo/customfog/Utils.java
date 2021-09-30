package setadokalo.customfog;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import setadokalo.customfog.config.DimensionConfig;
import setadokalo.customfog.config.ServerConfig;

import java.util.Objects;

public class Utils {

	public static final String WATER_CONFIG = "_customfog_internal:__/water/__";

	@NotNull
	public static DimensionConfig getDimensionConfigFor(@Nullable Identifier value) {
		ServerConfig serverConfig = CustomFogClient.serverConfig;
		if (CustomFogClient.config.overrideConfig != null)
			return CustomFogClient.config.overrideConfig;
		if (value != null && value.toString().equals(Utils.WATER_CONFIG)) {
			return Objects.requireNonNullElse(serverConfig != null ? serverConfig.waterOverride : null, CustomFogClient.config.waterConfig);
		}
		if (serverConfig != null) {
			if (serverConfig.overrides.get(value) != null)
				return serverConfig.overrides.get(value);
			if (serverConfig.universalOverride != null)
				return serverConfig.universalOverride;
		}

		return Objects.requireNonNullElse(
			CustomFogClient.config.dimensions.get(value),
			Objects.requireNonNullElse(serverConfig == null ? null : serverConfig.defaultOverride,
				CustomFogClient.config.defaultConfig
			)
		);
	}

	public static boolean universalOverride() {
		return CustomFogClient.serverConfig != null && CustomFogClient.serverConfig.universalOverride != null;
	}
}
