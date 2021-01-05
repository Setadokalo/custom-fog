package setadokalo.customfog;


import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CustomFogConfig {
	public CustomFogConfig() {
		CustomFog.file = new File(FabricLoader.getInstance().getConfigDir().toString(), CustomFog.MOD_ID + ".toml");
		if (CustomFog.file.exists()) {
			Toml config = new Toml().read(CustomFog.file);
			linearFogStartMultiplier = config.getDouble("linearFogStartMultiplier", (double)linearFogStartMultiplier).floatValue();
			linearFogEndMultiplier = config.getDouble("linearFogEndMultiplier", (double)linearFogEndMultiplier).floatValue();
			expFogMultiplier = config.getDouble("expFogMultiplier", (double)expFogMultiplier).floatValue();
			exp2FogMultiplier = config.getDouble("exp2FogMultiplier", (double)exp2FogMultiplier).floatValue();
			String tempS = config.getString("fogType");
			if (tempS != null) {
				fogType = FogType.valueOf(config.getString("fogType"));
			}
			tempS = config.getString("listMode");
			if (tempS != null) {
				listMode = ListMode.valueOf(config.getString("listMode"));
			}
			List<String> tempA = config.getList("dimensionsList");
			if (tempA != null) {
				String[] tempSA = new String[tempA.size()];
				dimensionsList = tempA.toArray(tempSA);
			}
		} else {
			saveConfig();
		}
	}
	
	public void saveConfig() {
		TomlWriter tWr = new TomlWriter();
		try {
			tWr.write(this, CustomFog.file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static final float DEFAULT_LINEAR_START_MULT = 0.25F;
	public float linearFogStartMultiplier = DEFAULT_LINEAR_START_MULT;
	public static final float DEFAULT_LINEAR_END_MULT = 1.00F;
	public float linearFogEndMultiplier = DEFAULT_LINEAR_END_MULT;
	public static final float DEFAULT_EXP_MULT = 3.00F;
	public float expFogMultiplier = DEFAULT_EXP_MULT;
	public static final float DEFAULT_EXP2_MULT = 1.75F;
	public float exp2FogMultiplier = DEFAULT_EXP2_MULT;
	public enum ListMode {
		WHITELIST,
		BLACKLIST
	};
	public ListMode listMode = ListMode.BLACKLIST;
	public String[] dimensionsList = new String[] {"minecraft:the_end"};
	public enum FogType {
		LINEAR,
		EXPONENTIAL,
		EXPONENTIAL_TWO,
	}
	public FogType fogType = FogType.LINEAR;
}
