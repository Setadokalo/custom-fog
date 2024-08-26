package setadokalo.customfog.config.gui;

import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextWidget;
import setadokalo.customfog.CustomFogClient;
import setadokalo.customfog.config.CustomFogConfig;
import setadokalo.customfog.config.gui.widgets.ResizingRangeSlider;
import setadokalo.customfog.config.gui.widgets.DimensionConfigEntry;
import setadokalo.customfog.config.gui.widgets.WarningWidget;

public class DimensionConfigScreen extends Screen {
	private final Screen parent;
	public static final int DONE_WIDTH = 100;
	protected final DimensionConfigEntry entry;
	protected ResizingRangeSlider slider1;
	@Nullable
	protected ResizingRangeSlider slider2 = null;

	public DimensionConfigScreen(@Nullable Screen parent, DimensionConfigEntry entry) {
		super(Text.translatable("screen.customfog.config"));
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
		Text saveAndQuitText = Text.translatable("button.customfog.saveandquit");
		Text enabledText = Text.translatable(getKeyForEnabled());
		int modeRowHeight = addSliders() ? this.height - 57 : this.height - 86;

		if (!CustomFogClient.config.hasClosedToast) {
			WarningWidget w = this.addDrawableChild(new WarningWidget(
				WarningWidget.Type.WARNING,
				this.width / 2 - 150, 20, 300,
				Text.translatable("notice.customfog.slidervalue1").formatted(Formatting.YELLOW, Formatting.BOLD),
				Text.translatable("notice.customfog.slidervalue2").formatted(Formatting.WHITE),
				Text.translatable("notice.customfog.slidervalue3").formatted(Formatting.WHITE)
			));
			w.setOnClickFunc((btn) -> {
				CustomFogClient.config.hasClosedToast = true;
				CustomFogClient.config.saveConfig();
				remove(w);
			});
//			this.addDrawableChild(new FogButtonWidget(this.width / 2 + 142, 20, 8, 8, 0, 60, 8,
//					new Identifier("custom-fog", "textures/gui/cfog-gui.png"), 256, 256, (btn) -> {
//				CustomFogClient.config.hasClosedToast = true;
//				CustomFogClient.config.saveConfig();
//				remove(w);
//				remove(btn);
//			}));
		}
		this.addDrawableChild(CustomFogClient.makeBtn(this.width - DONE_WIDTH - 9, this.height - 29, DONE_WIDTH, 20,
				saveAndQuitText, btn -> {
					CustomFogClient.config.overrideConfig = null;
					if (this.client != null)
						this.client.setScreen(this.parent);
				}));
		this.addDrawableChild(CustomFogClient.makeBtn(9, modeRowHeight, 150, 20,
			Text.translatable(getKeyForType(this.entry.config.getType())), btn -> {
				this.entry.config.setType(this.entry.config.getType().next());
				btn.setMessage(Text.translatable(getKeyForType(this.entry.config.getType())));
				removeSliders();
				addSliders();
			}
		));
		this.addDrawableChild(CustomFogClient.makeBtn(18 + 150, modeRowHeight, 75, 20,
			enabledText, btn -> {
				this.entry.config.setEnabled(!this.entry.config.getEnabled());
				btn.setMessage(Text.translatable(getKeyForEnabled()));
			}
		));
		this.addDrawable(new TextWidget(0, 8, this.width, 8, this.title, this.textRenderer).alignCenter());
	}

	// Disable blurring the background when in-game
	@Override
	public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
      if (this.client.world == null) {
         this.renderPanoramaBackground(context, delta);
      }
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
					s -> Text.translatable(
							"option.customfog.linearslider",
							truncateVal(s)
					)
			);
			this.addDrawable(this.addSelectableChild(slider1));
			slider2 = new ResizingRangeSlider(13 + sliderWidth, sliderHeight, sliderWidth, 20, true, this.entry.config.getLinearEnd(), 1.0,
					nVal -> this.entry.config.setLinearEnd(nVal.floatValue()),
					s -> Text.translatable(
							"option.customfog.linearendslider",
							truncateVal(s)
					)
			);
			this.addDrawable(this.addSelectableChild(slider2));
		} else if (this.entry.config.getType() == CustomFogConfig.FogType.EXPONENTIAL) {
			slider1 = new ResizingRangeSlider(9, sliderHeight, 9 + 225, 20, false, this.entry.config.getExp(), 5.0,
					nVal -> this.entry.config.setExp(nVal.floatValue()),
					s -> Text.translatable(
							"option.customfog.expslider",
							truncateVal(s)
					)
			);
			this.addDrawable(this.addSelectableChild(slider1));
		} else if (this.entry.config.getType() == CustomFogConfig.FogType.EXPONENTIAL_TWO) {
			slider1 = new ResizingRangeSlider(9, sliderHeight, 9 + 225, 20, false, this.entry.config.getExp2(), 5.0,
					nVal -> this.entry.config.setExp2(nVal.floatValue()),
					s -> Text.translatable(
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

	// @Override
	// public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
	// 	if (this.client.world == null) {
	// 		this.renderBackgroundTexture(context);
	// 	}
	// }

	@Override
	public void close() {
		client.setScreen(parent);
	} 
}
