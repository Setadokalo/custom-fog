package setadokalo.customfog.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import setadokalo.customfog.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.FluidState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import setadokalo.customfog.config.CustomFogConfig;
import setadokalo.customfog.config.DimensionConfig;
import setadokalo.customfog.config.ServerConfig;


@Mixin(BackgroundRenderer.class)
// This mod shouldn't even be installed on a server but w/e
@Environment(EnvType.CLIENT)
public class RendererMixin {


	@Inject(method = "applyFog", at=@At(value = "INVOKE", target = "com/mojang/blaze3d/systems/RenderSystem.setupNvFogDistance()V"), locals = LocalCapture.CAPTURE_FAILSOFT)
	private static void setFogFalloff(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo ci, FluidState fluidState, Entity entity) {
		ServerConfig serverConfig = CustomFogClient.serverConfig;
		if (serverConfig != null && !serverConfig.baseModAllowed) {
			return;
		}
		// Try applying fog for sky, otherwise apply custom terrain fog
		if (fogType == BackgroundRenderer.FogType.FOG_SKY) {
			RenderSystem.fogStart(0.0f);
			RenderSystem.fogEnd(viewDistance);
			RenderSystem.fogMode(GlStateManager.FogMode.LINEAR);
		} else if (!(entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.BLINDNESS))) {
			// If the dimensions list contains a special config for this dimension, use it; otherwise use the default
			DimensionConfig config = Utils.getDimensionConfigFor(entity.getEntityWorld().getRegistryKey().getValue());

			if (config != null) {
				changeFalloff(viewDistance, config);
			}
		}
	}

	private static void changeFalloff(float viewDistance, DimensionConfig config) {
		if (config.getEnabled()) {
			if (config.getType() == CustomFogConfig.FogType.NONE) {
				RenderSystem.fogStart(40000000000.0F);
				RenderSystem.fogEnd(  80000000000.0F);
				RenderSystem.fogMode(GlStateManager.FogMode.LINEAR);
			} else if (config.getType() == CustomFogConfig.FogType.LINEAR) {
				RenderSystem.fogStart(viewDistance * config.getLinearStart());
				RenderSystem.fogEnd(viewDistance * config.getLinearEnd());
				RenderSystem.fogMode(GlStateManager.FogMode.LINEAR);
			}
			else if (config.getType() == CustomFogConfig.FogType.EXPONENTIAL) {
				RenderSystem.fogDensity(config.getExp() / viewDistance);
				RenderSystem.fogMode(GlStateManager.FogMode.EXP);
			} else if (config.getType() == CustomFogConfig.FogType.EXPONENTIAL_TWO) {
				RenderSystem.fogDensity(config.getExp2() / viewDistance);
				RenderSystem.fogMode(GlStateManager.FogMode.EXP2);
			}
		}
	}
}
