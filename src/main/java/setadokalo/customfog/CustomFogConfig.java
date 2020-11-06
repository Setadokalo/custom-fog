package setadokalo.customfog;


import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CustomFogConfig {
	public CustomFogConfig() {
		CustomFog.file = new File(FabricLoader.getInstance().getConfigDir().toString(), CustomFog.MOD_ID + ".toml");
		if (CustomFog.file.exists()) {
			Toml config = new Toml().read(CustomFog.file);
			Double temp = config.getDouble("linearFogStartMultiplier");
			if (temp != null) {
				linearFogStartMultiplier = temp.floatValue();
			}
			temp = config.getDouble("linearFogEndMultiplier");
			if (temp != null) {
				linearFogEndMultiplier = temp.floatValue();
			}
			temp = config.getDouble("expFogMultiplier");
			if (temp != null) {
				expFogMultiplier = temp.floatValue();
			}
			temp = config.getDouble("exp2FogMultiplier");
			if (temp != null) {
				exp2FogMultiplier = temp.floatValue();
			}
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
				String[] _temp = new String[tempA.size()];
				dimensionsList = tempA.toArray(_temp);
			}
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
	public float linearFogStartMultiplier = 0.25F;
	public float linearFogEndMultiplier = 1.00F;
	public float expFogMultiplier = 3.00F;
	public float exp2FogMultiplier = 1.75F;
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
