package setadokalo.customfog.config.gui.widgets;

import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class BetterDoubleSliderWidget extends SliderWidget {

	double min;
	double max;
	Consumer<Double> setter;
	Function<BetterDoubleSliderWidget, Text> displayTextProducer;

	public BetterDoubleSliderWidget(int x, int y, int width, int height, double value, double min, double max, Consumer<Double> setter, Function<BetterDoubleSliderWidget, Text> display) {
		super(x, y, width, height, new LiteralText(""), (value - min) / (max - min));
		this.min = min;
		this.max = max;
		this.setter = setter;
		displayTextProducer = display;
		this.updateMessage();
	}

	@Override
	protected void updateMessage() {
		this.setMessage(displayTextProducer.apply(this));

	}

	protected double toInternal(double value) {
		return (value - min) / (max - min);
	}

	protected double fromInternal(double value) {
		return (value * (max - min)) + min;
	}

	public double getValue() {
		return this.value;
	}

	@Override
	protected void applyValue() {
		setter.accept(fromInternal(this.value));
	}
	
}
