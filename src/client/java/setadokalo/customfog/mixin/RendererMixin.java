package setadokalo.customfog.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import setadokalo.customfog.CustomFogImpl;


@Mixin(value = BackgroundRenderer.class, priority = 1500)
// This mod shouldn't even be installed on a server but w/e
@Environment(EnvType.CLIENT)
public class RendererMixin {



	@Inject(method = "applyFog", at=@At("RETURN"))
	private static void setFogFalloff(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
		CustomFogImpl.setFogFalloff(camera, fogType, viewDistance);
	}
}
