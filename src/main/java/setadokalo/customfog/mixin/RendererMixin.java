package setadokalo.customfog.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import setadokalo.customfog.CustomFogClient;
import setadokalo.customfog.Utils;
import setadokalo.customfog.config.CustomFogConfig;
import setadokalo.customfog.config.DimensionConfig;
import setadokalo.customfog.config.ServerConfig;


@Mixin(BackgroundRenderer.class)
// This mod shouldn't even be installed on a server but w/e
@Environment(EnvType.CLIENT)
public class RendererMixin {


	@Inject(method = "applyFog", at=@At(value = "INVOKE", target = "com/mojang/blaze3d/systems/RenderSystem.setShaderFogEnd(F)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT)
	private static void setFogFalloff(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo ci, CameraSubmersionType cameraSubmersionType, Entity entity) {
	//	if (true) return;
		ServerConfig serverConfig = CustomFogClient.serverConfig;
		if (serverConfig != null && !serverConfig.baseModAllowed) {
			return;
		}

		// Try applying fog for sky, otherwise apply custom terrain fog
		if (fogType == BackgroundRenderer.FogType.FOG_SKY) {
			RenderSystem.setShaderFogStart(0.0f);
			RenderSystem.setShaderFogEnd(viewDistance);
//			RenderSystem.setShaderFogMode(GlStateManager.FogMode.LINEAR);
		} else if (
				cameraSubmersionType != CameraSubmersionType.LAVA &&
				!((entity instanceof LivingEntity)
					&& ((LivingEntity)entity).hasStatusEffect(StatusEffects.BLINDNESS))
		) {
			// If the dimensions list contains a special config for this dimension, use it; otherwise use the default
			DimensionConfig config = cameraSubmersionType == CameraSubmersionType.WATER ?
					Utils.getDimensionConfigFor(new Identifier(Utils.WATER_CONFIG)) :
					Utils.getDimensionConfigFor(entity.getEntityWorld().getRegistryKey().getValue());

			changeFalloff(viewDistance, config);
		}
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
