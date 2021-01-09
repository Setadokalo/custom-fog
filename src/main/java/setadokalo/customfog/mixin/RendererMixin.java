package setadokalo.customfog.mixin;

import java.util.Objects;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import setadokalo.customfog.CustomFog;
import setadokalo.customfog.CustomFogConfig;
import setadokalo.customfog.DimensionConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.fluid.FluidState;
import net.minecraft.tag.FluidTags;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;


@Mixin(BackgroundRenderer.class)
// This mod shouldn't even be installed on a server but w/e
@Environment(EnvType.CLIENT)
public class RendererMixin {
	@NotNull
	private static <T> T requireNonNullElse(@Nullable T nullable, @NotNull T defaultT) {
		if (nullable != null) {
			return nullable;
		} else if (defaultT != null) {
			return defaultT;
		} else {
			throw new NullPointerException("defaultT should always be non-null");
		}
	}


	@Inject(method = "applyFog", at=@At(value = "INVOKE", target = "com/mojang/blaze3d/systems/RenderSystem.setupNvFogDistance()V"), locals = LocalCapture.CAPTURE_FAILSOFT)
	private static void setFogFalloff(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo ci, FluidState fluidState, Entity entity) {
		if (! (fluidState.isIn(FluidTags.LAVA)) || (entity instanceof LivingEntity && ((LivingEntity)entity).hasStatusEffect(StatusEffects.BLINDNESS))) {
			// If the dimensions list contains a special config for this dimension, use it; otherwise use the default
			DimensionConfig config = requireNonNullElse(
				CustomFog.config.overrideConfig, 
				requireNonNullElse(
					CustomFog.config.dimensions.get(entity.getEntityWorld().getRegistryKey().getValue().toString()), 
					CustomFog.config.defaultConfig
				)
			);
			changeFalloff(viewDistance, config);
		}
	}

	private static void changeFalloff(float viewDistance, DimensionConfig config) {
		if (config.getEnabled()) {
			if (config.getType() == CustomFogConfig.FogType.LINEAR) {
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
