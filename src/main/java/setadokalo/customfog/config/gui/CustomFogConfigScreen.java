package setadokalo.customfog.config.gui;

import java.util.Map;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import setadokalo.customfog.CustomFog;
import setadokalo.customfog.CustomFogClient;
import setadokalo.customfog.Utils;
import setadokalo.customfog.config.CustomFogConfig;
import setadokalo.customfog.config.DimensionConfig;
import setadokalo.customfog.config.gui.widgets.DimensionConfigEntry;
import setadokalo.customfog.config.gui.widgets.DimensionConfigListWidget;
import setadokalo.customfog.config.gui.widgets.WarningWidget;

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

		if (Utils.universalOverride()) {
			this.addChild(new WarningWidget(
				this.width / 2 - 120, this.height - 72, 235,
				new LiteralText("Config Overridden!").formatted(Formatting.YELLOW, Formatting.BOLD),
				new LiteralText("This config is overridden by the server!").formatted(Formatting.RED)));
		}

		// Add load button.
		this.addButton(new ButtonWidget(9, this.height - 29, 80, 20, new TranslatableText("button.customfog.load"),
				  btn -> {
					  CustomFogClient.config = CustomFogConfig.getConfig();
					  this.createList(HEADER_HEIGHT, FOOTER_HEIGHT);
				  }));
		// Add done button.
		int doneButtonX = Math.max(this.width / 2 - 100, 94);
		int doneButtonWidth = Math.min(200, this.width - (doneButtonX + 5 + 160 + 8));
		this.addButton(new ButtonWidget(doneButtonX, this.height - 29, doneButtonWidth, 20, new TranslatableText("button.customfog.saveandquit"),
			btn -> saveDimensionNames()));
		this.addButton(new ButtonWidget(this.width - 165, this.height - 29, 160, 20, new TranslatableText("button.customfog.toggleVOptions", boolToEnabled(CustomFogClient.config.videoOptionsButton)),
			btn -> {
			CustomFogClient.config.videoOptionsButton = !CustomFogClient.config.videoOptionsButton;
			btn.setMessage(new TranslatableText("button.customfog.toggleVOptions", boolToEnabled(CustomFogClient.config.videoOptionsButton)));
			}));
  }


	public static String boolToEnabled(boolean in) {
		return in ? "Enabled" : "Disabled";
	}

	private void saveDimensionNames() {
		for (DimensionConfigEntry entry : this.lWidget.children()) {
			saveDimensionName(entry);
		}
		CustomFogClient.config.saveConfig();
		this.client.openScreen(this.parent);
	}

	private void saveDimensionName(DimensionConfigEntry entry) {
		if (entry.dimensionId != null) {
			if (entry.originalDimId != null) {
				if (!entry.removable && entry.dimensionId == null) {
					CustomFogClient.config.defaultConfig = entry.config;
				} else {
					try {
						CustomFogConfig.changeKey(CustomFogClient.config, entry.originalDimId, entry.dimensionId);
					} catch (NullPointerException e) {
						CustomFog.log(Level.ERROR, "Failed to update key - invalid original key " + entry.originalDimId);
					}
				}
			} else {
				CustomFogConfig.add(CustomFogClient.config, entry.dimensionId, entry.config);
			}
		}
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
		list.add(new DimensionConfigEntry(list, CustomFogClient.config.defaultConfig));
		for (Map.Entry<Identifier, DimensionConfig> config : CustomFogClient.config.dimensions.entrySet()) {
			list.add(new DimensionConfigEntry(list, true, config.getKey(), config.getValue()));
		}
		list.addNonDimEntries(Utils.universalOverride());
		lWidget = list;
		// while rendering is done manually, it is important that the list widget is in the list of children
		// without adding it to the list, click and scroll events do not propogate
		this.addChild(lWidget);
	}
	
	@Override
	public void tick() {
		super.tick();
		this.lWidget.tick();
	}

  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
  {
		this.renderBackground(matrices);
		lWidget.render(matrices, mouseX, mouseY, delta);
		super.render(matrices, mouseX, mouseY, delta);
		for (Element child : this.children) {
			if (child instanceof Drawable) {
				((Drawable) child).render(matrices, mouseX, mouseY, delta);
			}
		}
		// Draw the title text.
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
  }

public void openScreen(DimensionConfigScreen dimensionConfigScreen) {
	client.openScreen(dimensionConfigScreen);
}

}
