package setadokalo.customfog;

import java.util.Map;

import javax.annotation.Nullable;

import me.lambdaurora.spruceui.SpruceButtonWidget;
import me.lambdaurora.spruceui.SpruceTexts;
import me.lambdaurora.spruceui.Tooltip;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import setadokalo.customfog.gui.DimensionConfigEntry;
import setadokalo.customfog.gui.DimensionConfigListWidget;

public class CustomFogConfigScreen extends Screen {
	private final Screen parent;
	protected DimensionConfigListWidget lWidget;

	protected double value = 0.0;
	private static final int HEADER_HEIGHT = 25;
	private static final int FOOTER_HEIGHT = 40;

	public CustomFogConfigScreen(@Nullable Screen parent) {
		super(new TranslatableText("screen.customfog.config"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		super.init();

		// the first entry in the list should always be the default config, that applies to any dimension without a more specific config
		if (lWidget == null) {
			createList(HEADER_HEIGHT, FOOTER_HEIGHT);
		} else {
			lWidget.updateSize(width, height, HEADER_HEIGHT, height - FOOTER_HEIGHT);
			this.children.add(lWidget);
		}
		// Add load button.
		this.addButton(new SpruceButtonWidget(9, this.height - 29, 80, 20, new TranslatableText("button.customfog.load"),
				  btn -> {
					  CustomFog.config = CustomFogConfig.getConfig();
					  this.createList(HEADER_HEIGHT, FOOTER_HEIGHT);
				  }));
		// Add done button.
		int doneButtonX = Math.max(this.width / 2 - 100, 94);
		this.addButton(new SpruceButtonWidget(doneButtonX, this.height - 29, Math.min(200, this.width - (doneButtonX + 5)), 20, SpruceTexts.GUI_DONE,
					btn -> this.client.openScreen(this.parent)));
  }

	private void createList(int headerHeight, int footerHeight) {
		this.children.remove(lWidget);
		DimensionConfigListWidget list = new DimensionConfigListWidget(
			client, // the client
			0, // x pos
			headerHeight, // y pos 
			width, //width
			height - (headerHeight + footerHeight), //height
			25, // line height (height of each entry in the list)
			this, // parent screen (of the widget)
			this.textRenderer // text renderer to use
		);
		list.add(new DimensionConfigEntry(list, false, "Default", CustomFog.config.defaultConfig));
		list.add(new DimensionConfigEntry(list, false, "Default", CustomFog.config.defaultConfig));
		list.add(new DimensionConfigEntry(list, false, "Default", CustomFog.config.defaultConfig));
		list.add(new DimensionConfigEntry(list, false, "Default", CustomFog.config.defaultConfig));
		list.add(new DimensionConfigEntry(list, false, "Default", CustomFog.config.defaultConfig));
		list.add(new DimensionConfigEntry(list, false, "Default", CustomFog.config.defaultConfig));
		list.add(new DimensionConfigEntry(list, false, "Default", CustomFog.config.defaultConfig));
		list.add(new DimensionConfigEntry(list, false, "Default", CustomFog.config.defaultConfig));
		list.add(new DimensionConfigEntry(list, false, "Default", CustomFog.config.defaultConfig));
		list.add(new DimensionConfigEntry(list, false, "Default", CustomFog.config.defaultConfig));
		list.add(new DimensionConfigEntry(list, false, "Default", CustomFog.config.defaultConfig));
		list.add(new DimensionConfigEntry(list, false, "Default", CustomFog.config.defaultConfig));
		list.add(new DimensionConfigEntry(list, false, "Default", CustomFog.config.defaultConfig));
		list.add(new DimensionConfigEntry(list, false, "Default", CustomFog.config.defaultConfig));
		list.add(new DimensionConfigEntry(list, false, "Default", CustomFog.config.defaultConfig));
		list.add(new DimensionConfigEntry(list, false, "Default", CustomFog.config.defaultConfig));
		list.add(new DimensionConfigEntry(list, false, "Default", CustomFog.config.defaultConfig));
		list.add(new DimensionConfigEntry(list, false, "Default", CustomFog.config.defaultConfig));
		list.add(new DimensionConfigEntry(list, false, "Default", CustomFog.config.defaultConfig));
		list.add(new DimensionConfigEntry(list, false, "Default", CustomFog.config.defaultConfig));
		list.add(new DimensionConfigEntry(list, false, "Default", CustomFog.config.defaultConfig));
		list.add(new DimensionConfigEntry(list, false, "Default", CustomFog.config.defaultConfig));
		list.add(new DimensionConfigEntry(list, false, "Default", CustomFog.config.defaultConfig));
		list.add(new DimensionConfigEntry(list, false, "Default", CustomFog.config.defaultConfig));
		list.add(new DimensionConfigEntry(list, false, "Default", CustomFog.config.defaultConfig));
		list.add(new DimensionConfigEntry(list, false, "Default", CustomFog.config.defaultConfig));
		list.add(new DimensionConfigEntry(list, false, "Default", CustomFog.config.defaultConfig));
		for (Map.Entry<String, DimensionConfig> config : CustomFog.config.dimensions.entrySet()) {
			list.add(new DimensionConfigEntry(list, true, config.getKey(), config.getValue()));
		}
		lWidget = list;
		// while rendering is done manually, it is important that the list widget is in the list of children
		// without adding it to the list, click and scroll events do not propogate
		this.addChild(lWidget);
	}
	

  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
  {
		this.renderBackground(matrices);
		lWidget.render(matrices, mouseX, mouseY, delta);
		super.render(matrices, mouseX, mouseY, delta);
		// Draw the title text.
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
		// Render all the tooltips.
		Tooltip.renderAll(matrices);
  }
}
