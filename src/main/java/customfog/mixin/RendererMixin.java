package customfog.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import customfog.CustomFog;
import customfog.CustomFogConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(BackgroundRenderer.class)
// This mod shouldn't even be installed on a server but w/e
@Environment(EnvType.CLIENT)
public class RendererMixin {
	@ModifyVariable(method = "applyFog", index = 7, at = @At(value = "CONSTANT", args = "floatValue=0.75", shift = At.Shift.BY, by = 3))
	private static float controlFog(float s, Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog) {
//		s = viewDistance * 0.25F;
		return viewDistance * CustomFog.config.linearFogMultiplier;
	}

	@Inject(method = "applyFog", at=@At(value = "INVOKE", target = "com/mojang/blaze3d/systems/RenderSystem.setupNvFogDistance()V"), locals = LocalCapture.CAPTURE_FAILSOFT)
	private static void setFogFalloff(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo ci, FluidState fluidState, Entity entity, float u) {
		if (! (fluidState.isIn(FluidTags.LAVA)) || (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.BLINDNESS))) {

			if (CustomFog.config.fogType == CustomFogConfig.FogType.LINEAR)
				RenderSystem.fogMode(GlStateManager.FogMode.LINEAR);
			else if (CustomFog.config.fogType == CustomFogConfig.FogType.EXPONENTIAL) {
				RenderSystem.fogDensity(CustomFog.config.expFogMultiplier / viewDistance);
				RenderSystem.fogMode(GlStateManager.FogMode.EXP);
			} else if (CustomFog.config.fogType == CustomFogConfig.FogType.EXPONENTIAL_TWO) {
				RenderSystem.fogDensity(CustomFog.config.exp2FogMultiplier / viewDistance);
				RenderSystem.fogMode(GlStateManager.FogMode.EXP2);
			}
		}
	}
}
