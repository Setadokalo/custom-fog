package setadokalo.customfog.config.gui.widgets;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.Level;
import org.lwjgl.glfw.GLFW;
import setadokalo.customfog.CustomFog;

public class ResizingRangeSlider extends SliderWidget {
	boolean displayPercent = true;
	final double defaultMax;
	double max;
	Consumer<Double> setter;
	Function<Double, Text> displayTextProducer;
	Function<Double, Text> typingDisplayTextProducer;

	public ResizingRangeSlider(
			int x, int y,
			int width, int height,
			double value, double defaultMax,
			Consumer<Double> setter,
			Function<Double, Text> display) {
		this(x, y, width, height, value, defaultMax, setter, display, display);
	}
	public ResizingRangeSlider(
		int x, int y,
		int width, int height,
		double value, double defaultMax,
		Consumer<Double> setter,
		Function<Double, Text> display,
		Function<Double, Text> typingDisplay) {
		super(x, y, width, height, new LiteralText(""), value / defaultMax);
		this.defaultMax = defaultMax;
		this.max = defaultMax;
		this.setter = setter;
		displayTextProducer = display;
		typingDisplayTextProducer = typingDisplay;
		this.updateMessage();
		setValue(getValue());
	}

	public void setDisplayPercent(boolean displayPercent) {
		this.displayPercent = displayPercent;
	}
	public boolean doesDisplayPercent() {
		return this.displayPercent;
	}

	private boolean isJustClick = false;

	protected boolean isTyping = false;
	protected String currentText = "";

	@Override
	public void onClick(double mouseX, double mouseY) {
		isJustClick = true;
	}

	@Override
	protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
		isJustClick = false;
		super.onDrag(mouseX, mouseY, deltaX, deltaY);
	}

	@Override
	public void onRelease(double mouseX, double mouseY) {
		if (isJustClick) {
			isTyping = true;
			currentText = "";
			System.out.println("Clicked");
			updateMessage();
		}
		super.onRelease(mouseX, mouseY);
		setValue(getValue());
	}

	private static boolean isNumericChar(char c, boolean isFirst) {
		return switch (c) {
			case '-', '+' -> isFirst;
			case '.' -> !isFirst;
			default -> c >= '0' && c <= '9';
		};
	}

	@Override
	public boolean charTyped(char chr, int modifiers) {
		if (isNumericChar(chr, currentText.isEmpty())) {
			currentText = currentText + chr;
			updateMessage();
		}

		return true;
	}

	private double truncateVal(double value) {
		return ((double)Math.round(value * 10000.0)) / 10000.0;
	}

	protected void setValue(double val) {
		val = truncateVal(val);

		double d = getValue();
		if (val <= max / 2.0 && max > defaultMax) {
			while (val <= max / 2.0 && max > defaultMax) {
				max = Math.round(max / 2.0);
			}
		} else if (val > max) {
			while (val > max) {
				max = max * 2.0;
			}
		}
		this.value = toInternal(val);
		if (d != getValue()) {
			this.applyValue();
		}
		this.updateMessage();
	}

	protected void finishTyping() {
		isTyping = false;
		try {
			setValue(Float.parseFloat(currentText) / 100.0);
		} catch (NumberFormatException e) {
			CustomFog.log(Level.ERROR, "Tried to parse bad float from slider");
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (!this.isTyping) {
			return super.keyPressed(keyCode, scanCode, modifiers);
		} else {
			switch (keyCode) {
				case GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER -> {
					finishTyping();
					return true;
				}
				case GLFW.GLFW_KEY_BACKSPACE -> {
					if (currentText.length() > 0) {
						currentText = currentText.substring(0, currentText.length()-1);
						updateMessage();
					}
					return true;
				}
			}
			return false;
		}
	}

	@Override
	protected void updateMessage() {
		if (!isTyping) {
			this.setMessage(displayTextProducer.apply(getValue() * (displayPercent ? 100.0 : 1.0)));
		} else {
			double d;
			try {
				d = Double.parseDouble(currentText);
			} catch (NullPointerException | NumberFormatException e) {
				d = 0.0;
			}
			this.setMessage(new LiteralText(currentText).append(new LiteralText("%").formatted(Formatting.GRAY)));
		}

	}

	protected double toInternal(double value) {
		return value / max;
	}

	protected double fromInternal(double value) {
		return value * max;
	}

	public double getValue() {
		return fromInternal(this.value);
	}

	@Override
	protected void applyValue() {
		setter.accept(getValue());
	}

	@Override
	protected void onFocusedChanged(boolean newFocused) {
		if (!newFocused && isTyping) {
			finishTyping();
		}
	}

	@Override
	protected void renderBackground(MatrixStack matrices, MinecraftClient client, int mouseX, int mouseY) {
		if (isTyping) {
			// adapted from TextFieldWidget
			fill(matrices, this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, -1);
			fill(matrices, this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
//			RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);
//			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//			int i = (this.isHovered() ? 2 : 1) * 20;
//			this.drawTexture(matrices, this.x + (int)(this.value * (double)(this.width - 8)), this.y, 0, 46 + i, 4, 20);
//			this.drawTexture(matrices, this.x + (int)(this.value * (double)(this.width - 8)) + 4, this.y, 196, 46 + i, 4, 20);
		} else {
			super.renderBackground(matrices, client, mouseX, mouseY);
		}
	}
}
