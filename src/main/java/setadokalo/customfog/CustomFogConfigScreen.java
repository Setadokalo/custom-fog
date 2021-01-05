package setadokalo.customfog;

import javax.annotation.Nullable;

import org.apache.logging.log4j.Level;

import me.lambdaurora.spruceui.SpruceButtonWidget;
import me.lambdaurora.spruceui.SpruceOptionSliderWidget;
import me.lambdaurora.spruceui.SpruceTexts;
import me.lambdaurora.spruceui.Tooltip;
import me.lambdaurora.spruceui.option.SpruceDoubleOption;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;

public class CustomFogConfigScreen extends Screen {
	private final Screen parent;

	protected double value = 0.0;

	public CustomFogConfigScreen(@Nullable Screen parent) {
		super(new TranslatableText("screen.customfog.config"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		super.init();

		int startY = this.height / 4 + 48;
		this.addButton(new SpruceButtonWidget(this.width / 2 - 100, startY, 200, 20, new TranslatableText("button.customfog.test"),
		btn -> {
			CustomFog.log(Level.INFO, "button 1 pressed");
			btn.setMessage(new TranslatableText("button.customfog.test.pressed"));
		}));
		this.addButton(new SpruceButtonWidget(this.width / 2 - 100, startY + 25, 200, 20, new TranslatableText("button.customfog.test2"),
		btn -> CustomFog.log(Level.INFO, "button 2 pressed")));
		SpruceDoubleOption opt = new SpruceDoubleOption(
			"option.customfog.dopttest",
			0.0,
			100.0,
			0.01F,
			() -> value,
			newValue -> value = newValue,
			option -> new LiteralText(String.format("%.2f%% Stinky", value)),
			new LiteralText("Uh oh, stinky~!"));

		this.addButton(new SpruceOptionSliderWidget(this.width / 2 - 100, startY + 50, 200, 20, opt));
		
		// Add done button.
		this.addButton(new SpruceButtonWidget(this.width / 2 - 100, this.height - 29, 200, 20, SpruceTexts.GUI_DONE,
				  btn -> this.client.openScreen(this.parent)));
  }
	
  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
  {
		this.renderBackground(matrices);
		super.render(matrices, mouseX, mouseY, delta);
		// Draw the title text.
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 16777215);
		// Render all the tooltips.
		Tooltip.renderAll(matrices);
  }
}
