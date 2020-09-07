package customfog;

import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;

@Config(name = CustomFog.MOD_ID)
public class CustomFogConfig implements ConfigData {
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
