package setadokalo.customfog;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import setadokalo.customfog.config.DimensionConfig;
import setadokalo.customfog.config.ServerConfig;
import setadokalo.customfog.config.CustomFogConfig;

public class CustomFogImpl {
	public static boolean setFogFalloff(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, BackgroundRenderer.FogData fogData) {
		CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
		Entity entity = camera.getFocusedEntity();
	//	if (true) return;
		ServerConfig serverConfig = CustomFogClient.serverConfig;
		if (serverConfig != null && !serverConfig.baseModAllowed) {
			return false;
		}

		// Try applying fog for sky, otherwise apply custom terrain fog
		if (fogType == BackgroundRenderer.FogType.FOG_SKY) {
			fogData.fogStart = 0.0f;
			fogData.fogEnd = viewDistance;
			return true;
//			RenderSystem.setShaderFogMode(GlStateManager.FogMode.LINEAR);
		} else if (cameraSubmersionType != CameraSubmersionType.LAVA && !((entity instanceof LivingEntity) && ((LivingEntity)entity).hasStatusEffect(StatusEffects.BLINDNESS))) {
			// If the dimensions list contains a special config for this dimension, use it; otherwise use the default
			DimensionConfig config;
			if (cameraSubmersionType == CameraSubmersionType.WATER) {
				config = Utils.getDimensionConfigFor(Identifier.of(CustomFog.WATER_CONFIG));
			} else if (cameraSubmersionType == CameraSubmersionType.POWDER_SNOW) {
				config = Utils.getDimensionConfigFor(Identifier.of(CustomFog.POWDER_SNOW_CONFIG));
			} else {
				config = Utils.getDimensionConfigFor(entity.getEntityWorld().getRegistryKey().getValue());
			}
			changeFalloff(viewDistance, config, fogData);
			return true;
		}
		return false;
	}

	private static void changeFalloff(float viewDistance, DimensionConfig config, BackgroundRenderer.FogData fogData) {
		if (config.getEnabled()) {
			if (config.getType() == CustomFogConfig.FogType.LINEAR) {
				fogData.fogStart = (viewDistance * config.getLinearStart());
				fogData.fogEnd = (viewDistance * config.getLinearEnd());
			} else if (config.getType() == CustomFogConfig.FogType.EXPONENTIAL) {
				fogData.fogStart = (-512.0F);
				fogData.fogEnd = (config.getExp() / (0.3F * viewDistance));
			} else if (config.getType() == CustomFogConfig.FogType.EXPONENTIAL_TWO) {
				fogData.fogStart = (-1024.0F);
				fogData.fogEnd = (config.getExp2() / (50.0F * viewDistance));
			} else {
				fogData.fogStart = (990000.0F);
				fogData.fogEnd = ( 1000000.0F);
			}
		}
	}
}
