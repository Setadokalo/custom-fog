package setadokalo.customfog;

import org.jetbrains.annotations.Nullable;

import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import setadokalo.customfog.config.DimensionConfig;
import setadokalo.customfog.config.ServerConfig;
import setadokalo.customfog.config.CustomFogConfig;

public class CustomFogImpl {
	public static @Nullable Fog setFogFalloff(Fog fog, Camera camera, BackgroundRenderer.FogType fogType, float viewDistance) {
		CameraSubmersionType cameraSubmersionType = camera.getSubmersionType();
		Entity entity = camera.getFocusedEntity();
	//	if (true) return;
		ServerConfig serverConfig = CustomFogClient.serverConfig;
		if (serverConfig != null && !serverConfig.baseModAllowed) {
			return fog;
		}

		// Try applying fog for sky, otherwise apply custom terrain fog
		if (cameraSubmersionType == CameraSubmersionType.NONE && fogType == BackgroundRenderer.FogType.FOG_SKY) {
			return new Fog(
				0.0f,
				viewDistance,
				fog.shape(),
				fog.red(),
				fog.green(),
				fog.blue(),
				fog.alpha()
			);
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
			fog = changeFalloff(fog, viewDistance, config);
			return fog;
		}
		return fog;
	}

	private static Fog changeFalloff(Fog fog, float viewDistance, DimensionConfig config) {
		if (config.getEnabled()) {
			if (config.getType() == CustomFogConfig.FogType.LINEAR) {
				return new Fog(
					viewDistance * config.getLinearStart(),
					viewDistance * config.getLinearEnd(),
					fog.shape(),
					fog.red(),
					fog.green(),
					fog.blue(),
					fog.alpha()
				);
//				RenderSystem.fogMode(GlStateManager.FogMode.LINEAR);
			} else if (config.getType() == CustomFogConfig.FogType.EXPONENTIAL) {
				return new Fog(
					-512.0F,
					config.getExp() / (0.3F * viewDistance),
					fog.shape(),
					fog.red(),
					fog.green(),
					fog.blue(),
					fog.alpha()
				);
			} else if (config.getType() == CustomFogConfig.FogType.EXPONENTIAL_TWO) {
				return new Fog(
					-1024.0F,
					config.getExp2() / (50.0F * viewDistance),
					fog.shape(),
					fog.red(),
					fog.green(),
					fog.blue(),
					fog.alpha()
				);
			} else {
				return new Fog(
					990000.0F,
					1000000.0F,
					fog.shape(),
					fog.red(),
					fog.green(),
					fog.blue(),
					fog.alpha()
				);
			}
		}
		return fog;
	}
}
