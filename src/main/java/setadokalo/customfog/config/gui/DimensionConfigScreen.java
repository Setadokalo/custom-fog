package setadokalo.customfog.config.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import setadokalo.customfog.CustomFogClient;
import setadokalo.customfog.config.gui.widgets.ResizingRangeSlider;
import setadokalo.customfog.config.gui.widgets.DimensionConfigEntry;

public class DimensionConfigScreen extends Screen {
	private final Screen parent;
	public static final int DONE_WIDTH = 100;
	protected DimensionConfigEntry entry;
	protected ClickableWidget slider1;
	@Nullable
	protected ClickableWidget slider2 = null;

	public DimensionConfigScreen(@Nullable Screen parent, DimensionConfigEntry entry) {
		super(new TranslatableText("screen.customfog.config"));
		this.parent = parent;
		this.entry = entry;
		CustomFogClient.config.overrideConfig = entry.config;
	}

	@Override
	protected void init() {
		super.init();
		MinecraftClient minecraftClient = MinecraftClient.getInstance();
		TextRenderer textRenderer = minecraftClient.textRenderer;
		Text saveAndQuitText = new TranslatableText("button.customfog.saveandquit");
		Text enabledText = new TranslatableText(getKeyForEnabled());
//		int combinedWidth = textRenderer.getWidth(saveAndQuitText.asOrderedText()) + 64 +
//			textRenderer.getWidth(fogModeText.asOrderedText()) + 60 +
//			textRenderer.getWidth(enabledText.asOrderedText()) + 64;
		int modeRowHeight = addSliders() ? this.height - 57 : this.height - 86;

		this.addDrawableChild(new ButtonWidget(this.width - DONE_WIDTH - 9, this.height - 29, DONE_WIDTH, 20,
				saveAndQuitText, btn -> {
					CustomFogClient.config.overrideConfig = null;
					if (this.client != null)
						this.client.openScreen(this.parent);
				}));
		this.addDrawableChild(new ButtonWidget(18 + 150, modeRowHeight, 75, 20,
			enabledText, btn -> {
				this.entry.config.setEnabled(!this.entry.config.getEnabled());
				btn.setMessage(new TranslatableText(getKeyForEnabled()));
			}
		));
	}

	private String getKeyForEnabled() {

		return "button.customfog." + (this.entry.config.getEnabled() ? "enabled" : "disabled");
	}

	protected void removeSliders() {
		if (slider1 != null) {
			this.remove(slider1);
		}
		if (slider2 != null) {
			this.remove(slider2);
		}
		slider1 = null;
		slider2 = null;
	}

	private double truncateVal(double value) {
		return ((double)(int)(value * 100.0)) / 100.0;
	}

	protected boolean addSliders() {
		int sliderWidth = 150;
		int sliderHeight = this.height - 29;
		if (this.width > 260 + DONE_WIDTH)
			sliderWidth = Math.min(sliderWidth, (this.width - (DONE_WIDTH + 26)) / 2);
		else {
			sliderWidth = Math.min(sliderWidth, (this.width - 16) / 2);
			sliderHeight -= 29;
		}
		slider1 = new ResizingRangeSlider(9, sliderHeight, sliderWidth, 20, this.entry.config.getLinearStart(), 1.0,
			nVal -> this.entry.config.setLinearStart(nVal.floatValue()),
			s -> new TranslatableText(
					"option.customfog.linearslider",
					truncateVal(s)
			), s -> new TranslatableText(
					"option.customfog.linearslider",
					truncateVal(s)
			)
		);
		this.addDrawableChild(slider1);
		slider2 = new ResizingRangeSlider(13 + sliderWidth, sliderHeight, sliderWidth, 20, this.entry.config.getLinearEnd(), 1.0,
			nVal -> this.entry.config.setLinearEnd(nVal.floatValue()),
			s -> new TranslatableText(
					"option.customfog.linearendslider",
					truncateVal(s)
			), s -> new TranslatableText(
					"option.customfog.linearendslider",
					truncateVal(s)
			)
		);
		this.addDrawableChild(slider2);
		return sliderHeight == this.height - 29;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (this.client.world == null) {
			this.renderBackground(matrices);
		}
		super.render(matrices, mouseX, mouseY, delta);
		// Draw the title text.
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
	}

}
