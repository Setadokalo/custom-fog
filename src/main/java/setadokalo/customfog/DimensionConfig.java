package setadokalo.customfog;

import setadokalo.customfog.CustomFogConfig.FogType;

public class DimensionConfig {
	protected boolean enabled;
	protected FogType type;
	protected float linearFogStartMult;
	protected float linearFogEndMult;
	protected float expFogMult;
	protected float exp2FogMult;
	

	public DimensionConfig(boolean enabled, FogType type, float linearStart, float linearEnd, float exp, float exp2) {
		this.enabled = enabled;
		this.type = type;
		linearFogStartMult = linearStart;
		linearFogEndMult = linearEnd;
		expFogMult = exp;
		exp2FogMult = exp2;
	}
	
	public DimensionConfig copy() {
		return new DimensionConfig(this.enabled, this.type, this.linearFogStartMult, this.linearFogEndMult, this.expFogMult, this.exp2FogMult);
	}

	public boolean getEnabled() {
		return enabled;
	}
	public FogType getType() {
		return type;
	}
	public float getLinearStart() {
		return linearFogStartMult;
	}
	public float getLinearEnd() {
		return linearFogEndMult;
	}
	public float getExp() {
		return expFogMult;
	}
	public float getExp2() {
		return exp2FogMult;
	}
}
