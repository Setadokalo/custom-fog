package setadokalo.customfog.config.gui;

import java.util.Map;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
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

	private static final int HEADER_HEIGHT = 25;
	private static final int FOOTER_HEIGHT = 40;

	protected int notificationHeight = 0;

	public CustomFogConfigScreen(@Nullable Screen parent) {
		super(new TranslatableText("screen.customfog.config"));
		this.parent = parent;
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	public void pushNotification(WarningWidget notification) {
		this.addDrawable(notification);
		notification.setX((width - notification.getWidth())/2);
		notificationHeight = notificationHeight - notification.getHeight();
		notification.setY(notificationHeight);
	}

	@Override
	protected void init() {
		super.init();

		// the first entry in the list should always be the default config, that applies to any dimension without a more specific config
		if (lWidget == null) {
			createList();
		} else {
			lWidget.updateSize(width, height, HEADER_HEIGHT, height - FOOTER_HEIGHT);
			this.addDrawableChild(lWidget);
		}
		notificationHeight = this.height - 40;
		if (FabricLoader.getInstance().isModLoaded("canvas")
				&& !MinecraftClient.getInstance().getResourcePackManager().getEnabledNames().contains("customfog/canvasfog")) {
			pushNotification(new WarningWidget(
					235,
					new TranslatableText("notice.customfog.canvaspack1").formatted(Formatting.YELLOW, Formatting.BOLD),
					new TranslatableText("notice.customfog.canvaspack2").formatted(Formatting.WHITE),
					new TranslatableText("notice.customfog.canvaspack3").formatted(Formatting.WHITE)));
		}
		if (Utils.universalOverride()) {
			pushNotification(new WarningWidget(
					235,
					new TranslatableText("notice.customfog.overridden1").formatted(Formatting.YELLOW, Formatting.BOLD),
					new TranslatableText("notice.customfog.overridden2").formatted(Formatting.RED)
			));
		}

		// Add load button.
		this.addDrawableChild(new ButtonWidget(9, this.height - 29, 80, 20, new TranslatableText("button.customfog.load"),
				  btn -> {
					  CustomFogClient.config = CustomFogConfig.getConfig();
					  createList();
				  }));
		// Add done button.
		int doneButtonX = Math.max(this.width / 2 - 100, 94);
		int doneButtonWidth = Math.min(200, this.width - (doneButtonX + 5 + 160 + 8));
		this.addDrawableChild(new ButtonWidget(doneButtonX, this.height - 29, doneButtonWidth, 20, new TranslatableText("button.customfog.saveandquit"),
			btn -> saveDimensionNames()));
		this.addDrawableChild(new ButtonWidget(this.width - 165, this.height - 29, 160, 20, new TranslatableText("button.customfog.toggleVOptions", boolToEnabled(CustomFogClient.config.videoOptionsButton)),
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
		if (this.client != null) {
			this.client.setScreen(this.parent);
		}
	}

	private void saveDimensionName(DimensionConfigEntry entry) {
		if (entry.dimensionId != null) {
			if (entry.dimensionId.toString().equals(Utils.WATER_CONFIG)) {
				CustomFogClient.config.waterConfig = entry.config;
			} else if (entry.dimensionId.toString().equals(Utils.POWDER_SNOW_CONFIG)) {
				CustomFogClient.config.snowConfig = entry.config;
			} else if (entry.originalDimId != null) {
				try {
					CustomFogConfig.changeKey(CustomFogClient.config, entry.originalDimId, entry.dimensionId);
				} catch (NullPointerException e) {
					CustomFog.log(Level.ERROR, "Failed to update key - invalid original key " + entry.originalDimId);
				}
			} else {
				CustomFogConfig.add(CustomFogClient.config, entry.dimensionId, entry.config);
			}
		}
	}

	private void createList() {
//		this.remove(lWidget);
		if (lWidget == null) {
			lWidget = new DimensionConfigListWidget(
					client, // the client
					0, // x pos
					CustomFogConfigScreen.HEADER_HEIGHT, // y pos
					width, //width
					height - (CustomFogConfigScreen.HEADER_HEIGHT + CustomFogConfigScreen.FOOTER_HEIGHT), //height
					25, // line height (height of each entry in the list)
					this, // parent screen (of the widget)
					this.textRenderer // text renderer to use
			);
			// while rendering is done manually, it is important that the list widget is in the list of children
			// without adding it to the list, click and scroll events do not propogate
			this.addDrawableChild(lWidget);
		}

		lWidget.children().clear();
		lWidget.add(new DimensionConfigEntry(lWidget, CustomFogClient.config.defaultConfig));
		lWidget.add(new DimensionConfigEntry(lWidget, false, new Identifier(Utils.WATER_CONFIG), CustomFogClient.config.waterConfig, new TranslatableText("config.customfog.water")));
		lWidget.add(new DimensionConfigEntry(lWidget, false, new Identifier(Utils.POWDER_SNOW_CONFIG), CustomFogClient.config.snowConfig, new TranslatableText("config.customfog.snow")));
		for (Map.Entry<Identifier, DimensionConfig> config : CustomFogClient.config.dimensions.entrySet()) {
			lWidget.add(new DimensionConfigEntry(lWidget, true, config.getKey(), config.getValue()));
		}
		lWidget.addNonDimEntries(Utils.universalOverride());
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
//		lWidget.render(matrices, mouseX, mouseY, delta);
		super.render(matrices, mouseX, mouseY, delta);
		// Draw the title text.
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
  }

public void openScreen(DimensionConfigScreen dimensionConfigScreen) {
	if (client != null) {
		client.setScreen(dimensionConfigScreen);
	}
}

}
