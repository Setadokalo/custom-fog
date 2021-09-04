package setadokalo.customfog.config.gui.widgets;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Formatting;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import setadokalo.customfog.CustomFogClient;
import setadokalo.customfog.Utils;
import setadokalo.customfog.config.DimensionConfig;
import setadokalo.customfog.config.gui.CustomFogConfigScreen;
import setadokalo.customfog.config.gui.DimensionConfigScreen;

public class DimensionConfigEntry extends AlwaysSelectedEntryListWidget.Entry<DimensionConfigEntry>
		implements ParentElement {


	private static final int REMOVE_WIDGET_WIDTH = 20;
	@Nullable
	private Element focused;
	private boolean dragging;
	public final boolean removable;
	protected final boolean nonDimensionEntry; // I'm lazy as hell and I'm not gonna implement this properly
	protected TextRenderer textRenderer;
	protected DimensionConfigListWidget parentList;
	public Identifier originalDimId;
	@Nullable
	public Identifier dimensionId;
	public DimensionConfig config;
	protected List<Element> children = new ArrayList<>();
	@Nullable
	protected TextFieldWidget dimNameWidget;
	protected TexturedButtonWidget removeWidget;
	protected ButtonWidget addWidget;
	protected ButtonWidget configureWidget;

	public DimensionConfigEntry(DimensionConfigListWidget parent, boolean removable, @Nullable Identifier dimId,
								DimensionConfig config) {
		nonDimensionEntry = false;
		this.removable = removable;
		parentList = parent;
		this.textRenderer = parent.getTextRenderer();
		dimensionId = dimId;
		originalDimId = dimId;
		this.config = config;
		if (removable) {
			dimNameWidget = new TextFieldWidget(textRenderer, 0, 0, 150, 20, new LiteralText(""));
			dimNameWidget.setText(dimensionId == null ? "" : dimensionId.toString());
			lintInput();
			dimNameWidget.setChangedListener(str -> {
				dimensionId = Identifier.tryParse(str);
				lintInput();
			});
			children.add(dimNameWidget);
			removeWidget = new TexturedButtonWidget(
				-20000, -20000, 
				REMOVE_WIDGET_WIDTH, 20, 
				0, 0, 
				20, 
				new Identifier("custom-fog", "textures/gui/minus.png"), 
				20, 40,
				btn -> {
					// there should never be an entry that has a visible remove widget 
					// that is not also in the dimensions array with the originalDimId key
					CustomFogClient.config.dimensions.remove(this.originalDimId);
					parentList.remove(this);
				}
			);
			children.add(removeWidget);
		}
		setupConfigureButton();
	}

	private void setupConfigureButton() {
		configureWidget = new ButtonWidget(-20000, -20000,
			removable ? 80 : 84 + REMOVE_WIDGET_WIDTH, 20,
			new TranslatableText("button.customfog.configure"),
			btn -> ((CustomFogConfigScreen)this.parentList.getParent()).openScreen(
				new DimensionConfigScreen(this.parentList.getParent(), this)
			),
			(ButtonWidget button, MatrixStack matrices, int mouseX, int mouseY) -> {
				if ((this.dimensionId != null && Utils.getDimensionConfigFor(this.dimensionId) != this.config) ||
					(this.dimensionId == null &&
						CustomFogClient.serverConfig != null &&
						CustomFogClient.serverConfig.defaultOverride != null
					)
				)
					this.parentList.getParent().renderTooltip(matrices,
						new LiteralText("This config is overridden by the server's config!")
							.formatted(Formatting.RED),
						mouseX, mouseY);
			}
		);
		children.add(configureWidget);
	}

	private void lintInput() {
		if (dimNameWidget == null) return;
		if (MinecraftClient.getInstance().world != null) {
			if (
				!MinecraftClient.getInstance().world.getRegistryManager().get(Registry.DIMENSION_TYPE_KEY).containsId(
					Identifier.tryParse(dimNameWidget.getText())
				)
			)
				dimNameWidget.setEditableColor(0xFF0000);
			else
				dimNameWidget.setEditableColor(0xFFFFFF);
		}
	}

	public DimensionConfigEntry(DimensionConfigListWidget parent, boolean trueDummy) {
		originalDimId = Identifier.tryParse("");
		parentList = parent;
		nonDimensionEntry = true;
		removable = false;
		if (!trueDummy) {
			addWidget = new ButtonWidget(-20000, -20000,
				80, 20,
				new TranslatableText("button.customfog.add"), btn -> {
					parentList.removeNonDimEntries();
					parentList.add(new DimensionConfigEntry(parentList, true, null, CustomFogClient.config.defaultConfig.copy()));
					parentList.addNonDimEntries(Utils.universalOverride());
				}
			);
			children.add(addWidget);
		}
	}

	public DimensionConfigEntry(@NotNull DimensionConfigListWidget parent, DimensionConfig config) {
		nonDimensionEntry = false;
		parentList = parent;
		this.textRenderer = parent.getTextRenderer();
		dimensionId = null;
		originalDimId = null;
		this.config = config;
		this.removable = false;
		setupConfigureButton();
	}

	@Override
	public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX,
			int mouseY, boolean hovered, float tickDelta) {
		if (nonDimensionEntry) {
			if (addWidget != null) {
				addWidget.setWidth(Math.min(200, entryWidth - 10));
				addWidget.x = x + entryWidth / 2 - addWidget.getWidth() / 2;
				addWidget.y = y;
				addWidget.render(matrices, mouseX, mouseY, tickDelta);
			}
			return;
		}
		if (dimNameWidget != null && removable) {
			removeWidget.x = x + entryWidth - removeWidget.getWidth() - 8;
			removeWidget.y = y;
			removeWidget.render(matrices, mouseX, mouseY, tickDelta);

			dimNameWidget.x = x + 8;
			dimNameWidget.y = y;
			dimNameWidget.render(matrices, mouseX, mouseY, tickDelta);

			configureWidget.x = x + entryWidth - 12 - REMOVE_WIDGET_WIDTH - configureWidget.getWidth();

		} else {
			configureWidget.x = x + entryWidth - 8 - configureWidget.getWidth();
			drawText(matrices, textRenderer, new LiteralText(dimensionId == null ? "Default" : dimensionId.toString()), x + 12, y + 4, 0xFFFFFF);
		}
		configureWidget.y = y;
		configureWidget.render(matrices, mouseX, mouseY, tickDelta);
	}

	public static void drawText(MatrixStack matrices, TextRenderer textRenderer, Text text, int x, int y, int color) {
		OrderedText orderedText = text.asOrderedText();
		textRenderer.drawWithShadow(matrices, orderedText, (float)x, (float)y, color);
	}
	
	public void tick() {
		if (dimNameWidget != null)
			dimNameWidget.tick();
		this.setFocused(focused);
	}

	@Override
	public List<? extends Element> children() {
		return this.children;
	}

	@Override
	public boolean isDragging() {
		return this.dragging;
	}

	@Override
	public void setDragging(boolean dragging) {
      this.dragging = dragging;
	}

	@Override
	public @Nullable Element getFocused() {
		return this.focused;
	}

	@Override
	public void setFocused(@Nullable Element focused) {
		this.focused = focused;
	}

	@Override
	public Text method_37006() {
		return new LiteralText("No clue what this is for");
	}
}
