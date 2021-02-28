package setadokalo.customfog.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.VideoOptionsScreen;
import net.minecraft.client.gui.screen.options.GameOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.options.GameOptions;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import setadokalo.customfog.CustomFog;
import setadokalo.customfog.gui.CustomFogConfigScreen;

@Mixin(VideoOptionsScreen.class)
public abstract class VideoOptionsMixin extends GameOptionsScreen {

	private VideoOptionsMixin(Screen parent, GameOptions gameOptions, Text title) {
		super(parent, gameOptions, title);
	}

	@Inject(method = "init", at = @At(value = "RETURN"))
	protected void addCustomFogButton(CallbackInfo ci) {
		// if (!CustomFog.modMenuPresent)
			this.addButton(
				new ButtonWidget(
					this.width - 108, 
					this.height - 27, 
					100, 
					20, 
					new TranslatableText("button.customfog.menu"), 
					btn -> client.openScreen(CustomFog.configScreen(this))
				)
			);
	} 

	@ModifyConstant(method = "init", constant = @Constant(intValue = 100))
	protected int fixDoneButtonPos(int curVal) {
		int menuBPos = width - 108;
		int doneBEnd = (width / 2 - curVal) + 208;
		return curVal + Math.max(0, doneBEnd - menuBPos);
	}
}
