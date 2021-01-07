package setadokalo.customfog.gui;

import javax.annotation.Nullable;

import me.lambdaurora.spruceui.SpruceButtonWidget;
import me.lambdaurora.spruceui.Tooltip;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class DimensionConfigScreen extends Screen {
	private final Screen parent;
	public static final int DONE_WIDTH = 200;

	public DimensionConfigScreen(@Nullable Screen parent) {
		super(new TranslatableText("screen.customfog.config"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		super.init();
		this.addButton(new SpruceButtonWidget((this.width - DONE_WIDTH) / 2, this.height - 29, DONE_WIDTH, 20,
				new TranslatableText("button.customfog.saveandquit"), btn -> this.client.openScreen(this.parent)));
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (this.client.world == null) {
			this.renderBackground(matrices);
		}
		super.render(matrices, mouseX, mouseY, delta);
		// Draw the title text.
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
		// Render all the tooltips.
		Tooltip.renderAll(matrices);
	}

}
