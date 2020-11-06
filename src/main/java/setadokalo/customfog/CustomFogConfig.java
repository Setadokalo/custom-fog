package setadokalo.customfog;


import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class CustomFogConfig {
	public float linearFogMultiplier = 0.25F;
	public float expFogMultiplier = 3.00F;
	public float exp2FogMultiplier = 1.75F;
	public enum FogType {
		LINEAR,
		EXPONENTIAL,
		EXPONENTIAL_TWO,
	}
	public FogType fogType = FogType.LINEAR;
}
