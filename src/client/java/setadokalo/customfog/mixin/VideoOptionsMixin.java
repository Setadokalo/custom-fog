package setadokalo.customfog.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.widget.ButtonWidget;
import setadokalo.customfog.config.gui.CustomFogConfigScreen;
import setadokalo.customfog.CustomFogClient;

// priority 1100 so we can fail-soft with BSVBP
@Mixin(value = VideoOptionsScreen.class, priority = 900)
public abstract class VideoOptionsMixin extends GameOptionsScreen {

	private VideoOptionsMixin() {
		super(null, null, null);
	}

	@Inject(method = "init", at = @At(value = "RETURN"))
	protected void addCustomFogButton(CallbackInfo ci) {
		if (!FabricLoader.getInstance().isModLoaded("bettersodiumvideosettingsbutton") &&
				CustomFogClient.config.videoOptionsButton && client != null) {
			this.addDrawableChild(
				ButtonWidget.builder(Text.translatable("button.customfog.menu"), btn -> client.setScreen(new CustomFogConfigScreen(parent)))
				.dimensions(this.width - 108, this.height - 27, 100, 20)
				.build()
			);
		}
	} 

	@ModifyConstant(method = "init", constant = @Constant(intValue = 100), require = 0)
	protected int fixDoneButtonPos(int curVal) {
		int maxBStart = width - 316;
		int curBStart = (width / 2) - curVal;
		return curVal + Math.max(0, curBStart - maxBStart);
	}
}
