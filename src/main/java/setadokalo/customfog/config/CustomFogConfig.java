package setadokalo.customfog.config;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import setadokalo.customfog.CustomFogLogger;

import java.io.*;
import java.util.*;

public class CustomFogConfig extends BaseConfig {

	public CustomFogConfig(File file) {
		super(file);
		dimensions.put(Identifier.of("minecraft:the_nether"),
			new DimensionConfig(false, FogType.LINEAR, LINEAR_START, LINEAR_END, EXP, EXP2));
		saveConfig();
	}

	@NotNull
	public static CustomFogConfig getConfig() {
		return ConfigLoader.getConfig(CustomFogConfig.class, CustomFogLogger.MOD_ID);
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
		ConfigLoader.saveConfig(this);
	}

	public static final float LINEAR_START = 0.25F;
	public static final float LINEAR_END = 1.00F;
	public static final float EXP = 3.00F;
	public static final float EXP2 = 1.75F;
	public static final float SNOW_LINEAR_START = 0.0F;
	public static final float SNOW_LINEAR_END = 1.40F;
	@NotNull
	public final DimensionConfig defaultConfig = new DimensionConfig(true, FogType.LINEAR, LINEAR_START, LINEAR_END, EXP, EXP2);

	@NotNull
	public DimensionConfig waterConfig = new DimensionConfig(true, FogType.EXPONENTIAL, LINEAR_START, LINEAR_END, 2.0F, 0.05F);

	// these exp and exp2 values are made to be close to linear, couldn't think of a better default value.
	@NotNull
	public DimensionConfig snowConfig = new DimensionConfig(true, FogType.LINEAR, SNOW_LINEAR_START, SNOW_LINEAR_END, 85.0F, 9800.05F);

	// should not be exposed in the config files, I think it was previously though oops
	// used by the client to show the config being edited in the config gui regardless of the dimension/state
	@Nullable
	public transient DimensionConfig overrideConfig = null;
	public final Map<Identifier, DimensionConfig> dimensions = new HashMap<>();

	public boolean videoOptionsButton = true;
	public boolean hasClosedToast = false;
	public boolean hasAcknowledgedSodium = false;

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
