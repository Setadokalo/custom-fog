package setadokalo.customfog.config.gui;

import java.util.Map;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import setadokalo.customfog.CustomFog;
import setadokalo.customfog.CustomFogClient;
import setadokalo.customfog.CustomFogLogger;
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

	// the Y coordinate of the bottom of the next notification
	protected int nextNotifBottom = 0;

	public CustomFogConfigScreen(@Nullable Screen parent) {
		super(Text.translatable("screen.customfog.config"));
		this.parent = parent;
	}

	@Override
	public boolean shouldCloseOnEsc() {
		return false;
	}

	public void pushNotification(WarningWidget notification) {
		this.addDrawableChild(notification);
		notification.setX((width - notification.getWidth())/2);
		nextNotifBottom = nextNotifBottom - notification.getHeight() - 2;
		notification.setY(nextNotifBottom + 2);
	}

	@Override
	protected void init() {
		super.init();

		// the first entry in the list should always be the default config, that applies to any dimension without a more specific config
		if (lWidget == null) {
			createList();
		} else {
			lWidget.setDimensions(width, height - HEADER_HEIGHT - FOOTER_HEIGHT);
			lWidget.setPosition(0, HEADER_HEIGHT);
			// lWidget.updateSize(width, height, HEADER_HEIGHT, height - FOOTER_HEIGHT);
		}
		// We add this to the drawable list *before* notifications so they draw on top of it,
		// but add it to the *selectable* list *after* the notifications so they can receive input first
		this.addDrawable(lWidget);
		// A couple "subtle" things can cause very unexpected behavior for users, so
		// we show them these notifications in the main config screen to warn them.
		nextNotifBottom = this.height - 40;
		// If canvas is loaded without the canvas-specific compatibility pack, EXP and EXP2 fog can't work right.
		// Warn the user to enable the canvasfog pack or only use linear/disabled.
		if (FabricLoader.getInstance().isModLoaded("canvas")
				&& !MinecraftClient.getInstance().getResourcePackManager().getEnabledIds().contains("customfog/canvasfog")) {
			pushNotification(new WarningWidget(
					235,
					Text.translatable("notice.customfog.canvaspack1").formatted(Formatting.YELLOW, Formatting.BOLD),
					Text.translatable("notice.customfog.canvaspack2").formatted(Formatting.WHITE),
					Text.translatable("notice.customfog.canvaspack3").formatted(Formatting.WHITE)));
		} else if (FabricLoader.getInstance().isModLoaded("sodium") && !CustomFogClient.config.hasAcknowledgedSodium) {
			WarningWidget w = new WarningWidget(
				270,
				Text.translatable("notice.customfog.sodiumfog1").formatted(Formatting.YELLOW, Formatting.BOLD),
				Text.translatable("notice.customfog.sodiumfog2").formatted(Formatting.WHITE),
				Text.translatable("notice.customfog.sodiumfog3").formatted(Formatting.WHITE)
			);
			w.setOnClickFunc((btn) -> {
				CustomFogClient.config.hasAcknowledgedSodium = true;
				CustomFogClient.config.saveConfig();
				remove(w);
			});
			pushNotification(w);
		}
		// If a universal override is applied by the server, none of the configs the user sets will be visible outside
		// the config gui. Warn them they can't change their fog on this server.
		if (Utils.universalOverride()) {
			pushNotification(new WarningWidget(
					235,
					Text.translatable("notice.customfog.overridden1").formatted(Formatting.YELLOW, Formatting.BOLD),
					Text.translatable("notice.customfog.overridden2").formatted(Formatting.RED)
			));
		}
		this.addSelectableChild(lWidget);

		// Add load button.
		this.addDrawableChild(CustomFogClient.makeBtn(9, this.height - 29, 80, 20, Text.translatable("button.customfog.load"),
				  btn -> {
					  CustomFogClient.config = CustomFogConfig.getConfig();
					  createList();
				  }
				  ));
		// Add done button.
		int saveBtnX = Math.max(this.width / 2 - 100, 94);
		int saveBtnWidth = Math.min(200, this.width - (saveBtnX + 5 + 160 + 8));
		this.addDrawableChild(CustomFogClient.makeBtn(
				saveBtnX, this.height - 29,
				saveBtnWidth, 20,
				Text.translatable("button.customfog.saveandquit"),
			btn -> saveDimensions()));
		this.addDrawableChild(CustomFogClient.makeBtn(
				this.width - 165, this.height - 29,
				160, 20,
				Text.translatable("button.customfog.toggleVOptions", boolToEnabled(CustomFogClient.config.videoOptionsButton)),
			btn -> {
			CustomFogClient.config.videoOptionsButton = !CustomFogClient.config.videoOptionsButton;
			btn.setMessage(Text.translatable("button.customfog.toggleVOptions", boolToEnabled(CustomFogClient.config.videoOptionsButton)));
			}));
  }


	@Contract(pure = true)
	public static @NotNull String boolToEnabled(boolean in) {
		return in ? "Enabled" : "Disabled";
	}

	private void saveDimensions() {
		for (DimensionConfigEntry entry : this.lWidget.children()) {
			saveDimension(entry);
		}
		CustomFogClient.config.saveConfig();
		if (this.client != null) {
			this.client.setScreen(this.parent);
		}
	}

	private void saveDimension(DimensionConfigEntry entry) {
		if (entry.dimensionId != null) {
			// We do water/powdered snow fog config with a hack, by pretending it's a dimension with
			// a weird identifier. Here we check for the special identifiers and write their configs where they
			// actually belong.
			if (entry.dimensionId.toString().equals(CustomFog.WATER_CONFIG)) {
				CustomFogClient.config.waterConfig = entry.config;
			} else if (entry.dimensionId.toString().equals(CustomFog.POWDER_SNOW_CONFIG)) {
				CustomFogClient.config.snowConfig = entry.config;
			} else if (entry.originalDimId != null) {
				try {
					CustomFogConfig.changeKey(CustomFogClient.config, entry.originalDimId, entry.dimensionId);
				} catch (NullPointerException e) {
					CustomFogLogger.log(Level.ERROR, "Failed to update key - invalid original key " + entry.originalDimId);
				}
			} else {
				CustomFogConfig.add(CustomFogClient.config, entry.dimensionId, entry.config);
			}
		}
	}

	// reset or create the dimension config list widget and add all dimensions from the config file.
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
		}

		lWidget.children().clear();
		lWidget.add(new DimensionConfigEntry(lWidget, CustomFogClient.config.defaultConfig));
		lWidget.add(new DimensionConfigEntry(lWidget, false, Identifier.of(CustomFog.WATER_CONFIG), CustomFogClient.config.waterConfig, Text.translatable("config.customfog.water")));
		lWidget.add(new DimensionConfigEntry(lWidget, false, Identifier.of(CustomFog.POWDER_SNOW_CONFIG), CustomFogClient.config.snowConfig, Text.translatable("config.customfog.snow")));
		for (Map.Entry<Identifier, DimensionConfig> config : CustomFogClient.config.dimensions.entrySet()) {
			lWidget.add(new DimensionConfigEntry(lWidget, true, config.getKey(), config.getValue()));
		}
		lWidget.addNonDimEntries(Utils.universalOverride());
	}
	
	@Override
	public void tick() {
		super.tick();
		// lWidget ticks so the text boxes can have a blinking cursor
	}

  @Override
  public void render(DrawContext context, int mouseX, int mouseY, float delta)
  {
		this.renderBackground(context, mouseX, mouseY, delta);
		super.render(context, mouseX, mouseY, delta);
		// Draw the title text.
		context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 8, 0xFFFFFF);
  }

	public void openScreen(DimensionConfigScreen dimensionConfigScreen) {
		if (client != null) {
			client.setScreen(dimensionConfigScreen);
		}
	}

	@Override
	public void close() {
		client.setScreen(parent);
	} 
}
