package setadokalo.customfog.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;

import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import setadokalo.customfog.CustomFogImpl;


@Mixin(value = BackgroundRenderer.class, priority = 1500)
// This mod shouldn't even be installed on a server but w/e
@Environment(EnvType.CLIENT)
public class RendererMixin {

	@ModifyReturnValue(method = "applyFog", at = @At("RETURN"))
	private static Fog setFogFalloff(Fog fog, Camera camera, BackgroundRenderer.FogType fogType, Vector4f color, float viewDistance, boolean thickFog, float tickDelta) {
		return CustomFogImpl.setFogFalloff(fog, camera, fogType, color, viewDistance);
	}
}
