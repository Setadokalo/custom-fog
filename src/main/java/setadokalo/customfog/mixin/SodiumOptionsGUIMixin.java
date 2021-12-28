package setadokalo.customfog.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.jellysquid.mods.sodium.client.gui.SodiumOptionsGUI;
import me.jellysquid.mods.sodium.client.gui.widgets.FlatButtonWidget;
import me.jellysquid.mods.sodium.client.util.Dim2i;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import setadokalo.customfog.CustomFogClient;
import setadokalo.customfog.config.gui.CustomFogConfigScreen;

@Mixin(SodiumOptionsGUI.class)
public class SodiumOptionsGUIMixin extends Screen {
	protected SodiumOptionsGUIMixin(Text title) {
		super(title);
	}

	@Inject(method = "rebuildGUI()V", at = @At("RETURN"), remap = false)
	private void customFogGuiHook(CallbackInfo ci) {
		if (!CustomFogClient.config.videoOptionsButton)
			return;
		var customFogBtn = new FlatButtonWidget(
				new Dim2i(6, this.height - 26, 100, 20),
				new TranslatableText("button.customfog.menu"),
				() -> {
					if (this.client != null) {
						this.client.setScreen(new CustomFogConfigScreen(this));
					}
				}
		);
		this.addDrawableChild(customFogBtn);
	}
}
