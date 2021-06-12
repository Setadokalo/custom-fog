package setadokalo.customfog.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import setadokalo.customfog.CustomFog;
import setadokalo.customfog.CustomFogClient;

@Mixin(MinecraftClient.class)
public class ClientMixin {
	@Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At(value = "HEAD"))
	private void clearServerConfig(Screen screen, CallbackInfo ci) {
		CustomFogClient.serverConfig = null;
	}
}
