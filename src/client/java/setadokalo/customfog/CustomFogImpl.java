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
	public static boolean setFogFalloff(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance) {
		CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
		Entity entity = camera.getFocusedEntity();
	//	if (true) return;
		ServerConfig serverConfig = CustomFogClient.serverConfig;
		if (serverConfig != null && !serverConfig.baseModAllowed) {
			return false;
		}

		// Try applying fog for sky, otherwise apply custom terrain fog
		if (fogType == BackgroundRenderer.FogType.FOG_SKY) {
			RenderSystem.setShaderFogStart(0.0f);
			RenderSystem.setShaderFogEnd(viewDistance);
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
			changeFalloff(viewDistance, config);
			return true;
		}
		return false;
	}

	private static void changeFalloff(float viewDistance, DimensionConfig config) {
		if (config.getEnabled()) {
			if (config.getType() == CustomFogConfig.FogType.LINEAR) {
				RenderSystem.setShaderFogStart(viewDistance * config.getLinearStart());
				RenderSystem.setShaderFogEnd(viewDistance * config.getLinearEnd());
//				RenderSystem.fogMode(GlStateManager.FogMode.LINEAR);
			} else if (config.getType() == CustomFogConfig.FogType.EXPONENTIAL) {
				RenderSystem.setShaderFogStart(-512.0F);
				RenderSystem.setShaderFogEnd(config.getExp() / (0.3F * viewDistance));
			} else if (config.getType() == CustomFogConfig.FogType.EXPONENTIAL_TWO) {
				RenderSystem.setShaderFogStart(-1024.0F);
				RenderSystem.setShaderFogEnd(config.getExp2() / (50.0F * viewDistance));
			} else {
				RenderSystem.setShaderFogStart(990000.0F);
				RenderSystem.setShaderFogEnd( 1000000.0F);
			}
		}
	}
}
