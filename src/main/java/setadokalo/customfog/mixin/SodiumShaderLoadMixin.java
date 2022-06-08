package setadokalo.customfog.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import me.jellysquid.mods.sodium.client.gl.shader.ShaderLoader;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import setadokalo.customfog.CustomFog;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Mixin(ShaderLoader.class)
public class SodiumShaderLoadMixin {

	@Inject(method = "getShaderSource(Lnet/minecraft/util/Identifier;)Ljava/lang/String;", at = @At(value = "HEAD"), cancellable = true)
	private static void loadCustomFogShader(Identifier name, CallbackInfoReturnable<String> cir) {
		if (name.toString().equals("sodium:include/fog.glsl")) {
			CustomFog.log(Level.INFO, "Overwriting sodium fog shader");

			String path = "sodium-include/fog.glsl";
			try (InputStream in = MinecraftClient.getInstance().getResourceManager()
							.getResource(new Identifier("custom-fog", path))
							.get().getInputStream()) {
				if (in == null) {
					throw new RuntimeException("Shader not found: " + path);
				}

				cir.setReturnValue(IOUtils.toString(in, StandardCharsets.UTF_8));
			} catch (IOException e) {
				throw new RuntimeException("Failed to read shader source for " + path, e);
			}
		}
	}
}
