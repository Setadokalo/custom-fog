package setadokalo.customfog.config.gui;

import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import setadokalo.customfog.CustomFogClient;
import setadokalo.customfog.config.CustomFogConfig;
import setadokalo.customfog.config.gui.widgets.ResizingRangeSlider;
import setadokalo.customfog.config.gui.widgets.DimensionConfigEntry;
import setadokalo.customfog.config.gui.widgets.WarningWidget;

public class DimensionConfigScreen extends Screen {
	private final Screen parent;
	public static final int DONE_WIDTH = 100;
	protected DimensionConfigEntry entry;
	protected ResizingRangeSlider slider1;
	@Nullable
	protected ResizingRangeSlider slider2 = null;

	public DimensionConfigScreen(@Nullable Screen parent, DimensionConfigEntry entry) {
		super(new TranslatableText("screen.customfog.config"));
		this.parent = parent;
		this.entry = entry;
		CustomFogClient.config.overrideConfig = entry.config;
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}


	@Override
	protected void init() {
		super.init();
		this.clearChildren();
		Text saveAndQuitText = new TranslatableText("button.customfog.saveandquit");
		Text enabledText = new TranslatableText(getKeyForEnabled());
		int modeRowHeight = addSliders() ? this.height - 57 : this.height - 86;

		if (!CustomFogClient.config.hasClosedToast) {
			WarningWidget w = this.addDrawable(new WarningWidget(
					WarningWidget.Type.WARNING,
					this.width / 2 - 150, 20, 300,
					new TranslatableText("notice.customfog.slidervalue1").formatted(Formatting.YELLOW, Formatting.BOLD),
					new TranslatableText("notice.customfog.slidervalue2").formatted(Formatting.WHITE),
					new TranslatableText("notice.customfog.slidervalue3").formatted(Formatting.WHITE)));
			this.addDrawableChild(new TexturedButtonWidget(this.width / 2 + 142, 20, 8, 8, 0, 60, 8,
					new Identifier("custom-fog", "textures/gui/cfog-gui.png"), 256, 256, (btn) -> {
				CustomFogClient.config.hasClosedToast = true;
				CustomFogClient.config.saveConfig();
				remove(w);
				remove(btn);
			}));
		}
		this.addDrawableChild(new ButtonWidget(this.width - DONE_WIDTH - 9, this.height - 29, DONE_WIDTH, 20,
				saveAndQuitText, btn -> {
					CustomFogClient.config.overrideConfig = null;
					if (this.client != null)
						this.client.openScreen(this.parent);
				}));
		this.addDrawableChild(new ButtonWidget(9, modeRowHeight, 150, 20,
			new TranslatableText(getKeyForType(this.entry.config.getType())), btn -> {
				this.entry.config.setType(this.entry.config.getType().next());
				btn.setMessage(new TranslatableText(getKeyForType(this.entry.config.getType())));
				removeSliders();
				addSliders();
			}
		));
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

	private String getKeyForType(CustomFogConfig.FogType type) {
		if (type == CustomFogConfig.FogType.LINEAR)
			return "button.customfog.linear";
		else if (type == CustomFogConfig.FogType.EXPONENTIAL)
			return "button.customfog.exponential";
		else if (type == CustomFogConfig.FogType.EXPONENTIAL_TWO)
			return "button.customfog.exponential2";
		else
			return "button.customfog.none";
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
		if (this.entry.config.getType() == CustomFogConfig.FogType.LINEAR) {
			slider1 = new ResizingRangeSlider(9, sliderHeight, sliderWidth,  20, true, this.entry.config.getLinearStart(), 1.0,
					nVal -> this.entry.config.setLinearStart(nVal.floatValue()),
					s -> new TranslatableText(
							"option.customfog.linearslider",
							truncateVal(s)
					)
			);
			this.addDrawable(this.addSelectableChild(slider1));
			slider2 = new ResizingRangeSlider(13 + sliderWidth, sliderHeight, sliderWidth, 20, true, this.entry.config.getLinearEnd(), 1.0,
					nVal -> this.entry.config.setLinearEnd(nVal.floatValue()),
					s -> new TranslatableText(
							"option.customfog.linearendslider",
							truncateVal(s)
					)
			);
			this.addDrawable(this.addSelectableChild(slider2));
		} else if (this.entry.config.getType() == CustomFogConfig.FogType.EXPONENTIAL) {
			slider1 = new ResizingRangeSlider(9, sliderHeight, 9 + 225, 20, false, this.entry.config.getExp(), 5.0,
					nVal -> this.entry.config.setExp(nVal.floatValue()),
					s -> new TranslatableText(
							"option.customfog.expslider",
							truncateVal(s)
					)
			);
			this.addDrawable(this.addSelectableChild(slider1));
		} else if (this.entry.config.getType() == CustomFogConfig.FogType.EXPONENTIAL_TWO) {
			slider1 = new ResizingRangeSlider(9, sliderHeight, 9 + 225, 20, false, this.entry.config.getExp2(), 5.0,
					nVal -> this.entry.config.setExp2(nVal.floatValue()),
					s -> new TranslatableText(
							"option.customfog.exp2slider",
							truncateVal(s)
					)
			);
			this.addDrawable(this.addSelectableChild(slider1));
		} // NONE has no sliders
		return sliderHeight == this.height - 29;
	}


	public void tick() {
		if (this.slider1 != null) {
			this.slider1.tick();
		}
		if (this.slider2 != null) {
			this.slider2.tick();
		}
	}


	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (this.client == null || this.client.world == null) {
			this.renderBackground(matrices);
		}
		super.render(matrices, mouseX, mouseY, delta);
		// Draw the title text.
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
	}

}
