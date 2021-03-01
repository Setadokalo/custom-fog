package setadokalo.customfog.gui;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import setadokalo.customfog.CustomFog;
import setadokalo.customfog.CustomFogConfig.FogType;

public class DimensionConfigScreen extends Screen {
	private final Screen parent;
	public static final int DONE_WIDTH = 100;
	protected DimensionConfigEntry entry;
	protected AbstractButtonWidget slider1;
	@Nullable
	protected AbstractButtonWidget slider2 = null;

	public DimensionConfigScreen(@Nullable Screen parent, DimensionConfigEntry entry) {
		super(new TranslatableText("screen.customfog.config"));
		this.parent = parent;
		this.entry = entry;
		CustomFog.config.overrideConfig = entry.config;
	}

	@Override
	protected void init() {
		super.init();
		this.addButton(new ButtonWidget(this.width - DONE_WIDTH - 9, this.height - 29, DONE_WIDTH, 20,
				new TranslatableText("button.customfog.saveandquit"), btn -> {
					CustomFog.config.overrideConfig = null;
					this.client.openScreen(this.parent);
				}));
		this.addButton(new ButtonWidget(9, this.height - 58, 150, 20,
			new TranslatableText(getKeyForFogMode()), btn -> {
				this.entry.config.setType(this.entry.config.getType().next());
				btn.setMessage(new TranslatableText(getKeyForFogMode())); 
				removeSliders();
				addSliders();
			}
		));
		this.addButton(new ButtonWidget(18 + 150, this.height - 58, 75, 20,
			new TranslatableText(getKeyForEnabled()), btn -> {
				this.entry.config.setEnabled(!this.entry.config.getEnabled());
				btn.setMessage(new TranslatableText(getKeyForEnabled()));
			}
		));
		addSliders();
	}

	private String getKeyForEnabled() {

		return "button.customfog." + (this.entry.config.getEnabled() ? "enabled" : "disabled");
	}

	protected void removeSliders() {
		if (slider1 != null) {
			this.buttons.remove(slider1);
			this.children.remove(slider1);
		}
		if (slider2 != null) {
			this.buttons.remove(slider2);
			this.children.remove(slider2);
		}
		slider1 = null;
		slider2 = null;
	}

	private double truncateVal(double value) {
		return ((double)(int)(value * 100.0)) / 100.0;
	}

	protected void addSliders() {
		FogType type = this.entry.config.getType();
		int sliderWidth = 150;
		if (type.equals(FogType.LINEAR)) {
			sliderWidth = Math.min(sliderWidth, (this.width - (DONE_WIDTH + 26)) / 2);
			slider1 = new BetterDoubleSliderWidget(9, this.height - 29, sliderWidth, 20, this.entry.config.getLinearStart(), 0.0, 1.0,
				nVal -> this.entry.config.setLinearStart(nVal.floatValue()), 
				s -> new TranslatableText(
					"option.customfog.linearslider", 
					truncateVal(this.entry.config.getLinearStart() * 100.0)
				)
			);
			this.addButton(slider1);
			slider2 = new BetterDoubleSliderWidget(13 + sliderWidth, this.height - 29, sliderWidth, 20, this.entry.config.getLinearEnd(), 0.0, 1.0,
			nVal -> this.entry.config.setLinearEnd(nVal.floatValue()), 
			s -> new TranslatableText(
				"option.customfog.linearendslider", 
				truncateVal(this.entry.config.getLinearEnd() * 100.0)
			)
		);
			this.addButton(slider2);
		} else if (type.equals(FogType.EXPONENTIAL)) {
			slider1 = new BetterDoubleSliderWidget(9, this.height - 29, sliderWidth, 20, this.entry.config.getExp(), 0.0, 20.0,
				nVal -> this.entry.config.setExp(nVal.floatValue()), 
				s -> new TranslatableText(
					"option.customfog.expslider", 
					truncateVal(this.entry.config.getExp())
				)
			);
			this.addButton(slider1);
			slider2 = null;
		} else if (type.equals(FogType.EXPONENTIAL_TWO)) {
			slider1 = new BetterDoubleSliderWidget(9, this.height - 29, sliderWidth, 20, this.entry.config.getExp2(), 0.0, 20.0,
				nVal -> this.entry.config.setExp2(nVal.floatValue()), 
				s -> new TranslatableText(
					"option.customfog.exp2slider", 
					truncateVal(this.entry.config.getExp2())
				)
			);
			this.addButton(slider1);
			slider2 = null;
		}

	}

	private String getKeyForFogMode() {
		FogType type = this.entry.config.getType();
		if (type.equals(FogType.LINEAR)) {
			return "button.customfog.linear";
		} else if (type.equals(FogType.EXPONENTIAL)) {
			return "button.customfog.exponential";
		} else if (type.equals(FogType.EXPONENTIAL_TWO)) {
			return "button.customfog.exponential2";
		} else if (type.equals(FogType.NONE)) {
			return "button.customfog.none";
		}
		throw new NullPointerException("Invalid state");
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
