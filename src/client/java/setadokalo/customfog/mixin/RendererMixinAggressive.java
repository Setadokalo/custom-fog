package setadokalo.customfog.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Fog;

import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import setadokalo.customfog.CustomFogImpl;


@Mixin(value = BackgroundRenderer.class, priority = 500)
// This mod shouldn't even be installed on a server but w/e
@Environment(EnvType.CLIENT)
public class RendererMixinAggressive {

	// I prefer to use the nicer inject-at-tail, but every other mod that messes with fog
	// injects at head and cancels early unconditionally so HERE WE ARE I GUESS
	@Inject(method = "applyFog", at=@At(value = "HEAD"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
	private static void setFogFalloff(Camera camera, BackgroundRenderer.FogType fogType, Vector4f color, float viewDistance, boolean thickFog, float tickDelta, CallbackInfoReturnable<Fog> ci) {
		Fog newFog = CustomFogImpl.setFogFalloff(null, camera, fogType, color, viewDistance);
		if (newFog != null) {
			ci.setReturnValue(newFog);
		}
	}

}
