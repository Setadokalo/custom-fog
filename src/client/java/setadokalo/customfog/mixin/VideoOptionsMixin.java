package setadokalo.customfog.mixin;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.Positioner;
import net.minecraft.client.gui.widget.ThreePartsLayoutWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.screen.ScreenTexts;
import setadokalo.customfog.config.gui.CustomFogConfigScreen;
import setadokalo.customfog.CustomFogClient;

// priority 1100 so we can fail-soft with BSVBP
@Mixin(value = GameOptionsScreen.class, priority = 900)
public abstract class VideoOptionsMixin extends Screen {

	private VideoOptionsMixin() {
		super(null);
	}

	@Inject(method = "initFooter", at = @At(value = "RETURN"))
	protected void addCustomFogButton(CallbackInfo ci) {
		if ((Object)this.getClass() != VideoOptionsScreen.class) {
			return;
		}
		if (!FabricLoader.getInstance().isModLoaded("bettersodiumvideosettingsbutton") &&
				CustomFogClient.config.videoOptionsButton && client != null) {
			
			((GameOptionsScreen)(Object)this).layout.addFooter(
				ButtonWidget.builder(Text.translatable("button.customfog.menu"), btn -> client.setScreen(new CustomFogConfigScreen(this)))
				.dimensions(this.width - 108, this.height - 27, 100, 20)
				.build(),
				positioner -> {
					positioner.alignRight();
					positioner.marginRight(8);
				}
			);
		}
	} 

	@Redirect(
		method = "initFooter", 
		at = @At(
			value = "INVOKE", 
			target="Lnet/minecraft/client/gui/widget/ThreePartsLayoutWidget;addFooter(Lnet/minecraft/client/gui/widget/Widget;)Lnet/minecraft/client/gui/widget/Widget;"
		)
	)
	protected Widget fixDoneButtonPos(ThreePartsLayoutWidget layout, Widget doneBtn) {
		ButtonWidget doneButton = (ButtonWidget) doneBtn;
		if (
			// Do nothing to any screen other than Video Options
			(Object)this.getClass() != VideoOptionsScreen.class
			// Don't inject if BSVSB is loaded, maybe unnecessary now?
			|| FabricLoader.getInstance().isModLoaded("bettersodiumvideosettingsbutton")
			// Don't offset the done button if we're not adding the custom fog button
			|| !CustomFogClient.config.videoOptionsButton
		) {
			return layout.addFooter(doneButton);
		}
		return layout.addFooter(doneButton, positioner -> {
			positioner.marginRight(116);
		});
	}
}
