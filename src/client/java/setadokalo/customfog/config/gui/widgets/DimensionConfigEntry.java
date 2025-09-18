package setadokalo.customfog.config.gui.widgets;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.RegistryKeys;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import setadokalo.customfog.CustomFog;
import setadokalo.customfog.CustomFogClient;
import setadokalo.customfog.Utils;
import setadokalo.customfog.config.DimensionConfig;
import setadokalo.customfog.config.gui.CustomFogConfigScreen;
import setadokalo.customfog.config.gui.DimensionConfigScreen;
import setadokalo.customfog.config.gui.widgets.FogButtonWidget.FogButtonCoords;

public class DimensionConfigEntry extends AlwaysSelectedEntryListWidget.Entry<DimensionConfigEntry>
		implements ParentElement {


	private static final int REMOVE_WIDGET_WIDTH = 20;
	@Nullable
	private Element focused;
	private boolean dragging;
	public final boolean removable;
	protected final boolean nonDimensionEntry; // I'm lazy as hell and I'm not gonna implement this properly
	protected TextRenderer textRenderer;
	protected final DimensionConfigListWidget parentList;
	public final Identifier originalDimId;
	@Nullable
	public Identifier dimensionId;
	@Nullable
	public Text name;
	public DimensionConfig config;
	protected final List<Element> children = new ArrayList<>();
	@Nullable
	protected TextFieldWidget dimNameWidget;
	protected FogButtonWidget removeWidget;
	protected ButtonWidget addWidget;
	protected ButtonWidget configureWidget;
	protected ButtonWidget pushToServerWidget;
	protected ButtonWidget pushAsOverrideWidget;

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
			dimNameWidget = new TextFieldWidget(textRenderer, 0, 0, 150, 20, Text.literal(""));
			dimNameWidget.active = true;
			dimNameWidget.visible = true;
			dimNameWidget.setText(dimensionId == null ? "" : dimensionId.toString());
			lintInput();
			dimNameWidget.setChangedListener(str -> {
				dimensionId = Identifier.tryParse(str);
				lintInput();
			});
			children.add(dimNameWidget);

			removeWidget = new FogButtonWidget(
					-20000, -20000,
					REMOVE_WIDGET_WIDTH, 20,
					new FogButtonCoords(0, 20, 0, 40),
					btn -> {
						// there should never be an entry that has a visible remove widget
						// that is not also in the dimensions array with the originalDimId key
						CustomFogClient.config.dimensions.remove(this.originalDimId);
						parentList.remove(this);
					}
			);
			children.add(removeWidget);
		}
		if (CustomFogClient.serverConfig != null
			&& MinecraftClient.getInstance().player != null
			&& MinecraftClient.getInstance().player.hasPermissionLevel(3)) {
			pushToServerWidget = new FogButtonWidget(
				-20000, -20000,
				20, 20,
				new FogButtonCoords(40, 0, 40, 20),
				btn -> { sendToServer(null); }
			);
			pushToServerWidget.setTooltip(Tooltip.of(Text.translatable("tooltip.customfog.pushtoserver")));
			children.add(pushToServerWidget);
		}
		setupConfigureButton();
	}

	private void sendToServer(@Nullable Identifier as) {
		// PacketByteBuf buf = PacketByteBufs.create();
		// buf.writeIdentifier(as != null ? as : (
		// 	this.dimensionId != null ? this.dimensionId : Identifier.of("_customfog_internal:__/default/__")
		// ));
		// buf.writeBoolean(config.getEnabled());
		// buf.writeEnumConstant(config.getType());
		// buf.writeFloat(config.getLinearStart());
		// buf.writeFloat(config.getLinearEnd());
		// buf.writeFloat(config.getExp());
		// buf.writeFloat(config.getExp2());
		var dimId = Objects.requireNonNullElse(as, Objects.requireNonNullElse(this.dimensionId, Identifier.of("_customfog_internal", "__/default/__")));
		ClientPlayNetworking.send(
			new CustomFog.UpdateServerConfigPayload(
				dimId,
				config
			)
		);
	}

	public DimensionConfigEntry(DimensionConfigListWidget parent, boolean removable, @Nullable Identifier dimId,
										  DimensionConfig config, @Nullable Text nameOverride) {
		this(parent, removable, dimId, config);
		name = nameOverride;
	}

	public DimensionConfigEntry(DimensionConfigListWidget parent, boolean trueDummy) {
		originalDimId = Identifier.tryParse("");
		parentList = parent;
		nonDimensionEntry = true;
		removable = false;
		if (!trueDummy) {
			addWidget = CustomFogClient.makeBtn(-20000, -20000,
					80, 20,
					Text.translatable("button.customfog.add"), btn -> {
				parentList.removeNonDimEntries();
				parentList.add(new DimensionConfigEntry(parentList, true, null, CustomFogClient.config.defaultConfig.copy()));
				parentList.addNonDimEntries(Utils.universalOverride());
			}
			);
			children.add(addWidget);
		}
	}

	// Special case constructor for the "Default" entry
	public DimensionConfigEntry(@NotNull DimensionConfigListWidget parent, DimensionConfig config) {
		nonDimensionEntry = false;
		parentList = parent;
		this.textRenderer = parent.getTextRenderer();
		dimensionId = null;
		originalDimId = null;
		this.config = config;
		this.removable = false;
		setupConfigureButton();

		if (CustomFogClient.serverConfig != null
			&& MinecraftClient.getInstance().player != null
			&& MinecraftClient.getInstance().player.hasPermissionLevel(3)) {
			pushToServerWidget = new FogButtonWidget(
				-20000, -20000,
				20, 20,
				new FogButtonCoords(40, 0, 40, 20),
				btn -> { sendToServer(null); }
			);
			pushToServerWidget.setTooltip(Tooltip.of(Text.translatable("tooltip.customfog.pushtoserver")));
			children.add(pushToServerWidget);
			pushAsOverrideWidget = new FogButtonWidget(
				-20000, -20000,
				20, 20,
				new FogButtonCoords(60, 0, 60, 20),
				btn -> { sendToServer(Identifier.of("_customfog_internal", "__/universal/__")); }
			);
			pushAsOverrideWidget.setTooltip(Tooltip.of(Text.translatable("tooltip.customfog.pushtouniversal")));
			children.add(pushAsOverrideWidget);
		}
	}

	private void setupConfigureButton() {
		configureWidget = CustomFogClient.makeBtn(-20000, -20000,
			removable ? 80 : 84 + REMOVE_WIDGET_WIDTH, 20,
			Text.translatable("button.customfog.configure"),
			btn -> ((CustomFogConfigScreen)this.parentList.getParent()).openScreen(
				new DimensionConfigScreen(this.parentList.getParent(), this)
			)
			// TODO: reimplement this
			// , (ButtonWidget button, MatrixStack matrices, int mouseX, int mouseY) -> {
			// 	if ((this.dimensionId != null && Utils.getDimensionConfigFor(this.dimensionId) != this.config) ||
			// 		(this.dimensionId == null &&
			// 			CustomFogClient.serverConfig != null &&
			// 			CustomFogClient.serverConfig.defaultOverride != null
			// 		)
			// 	)
			// 		this.parentList.getParent().renderTooltip(matrices,
			// 			Text.literal("This config is overridden by the server's config!")
			// 				.formatted(Formatting.RED),
			// 			mouseX, mouseY);
			// }
		);
		children.add(configureWidget);
	}

	private void lintInput() {
		if (dimNameWidget == null) return;
		if (MinecraftClient.getInstance().world != null) {
			if (
				!MinecraftClient.getInstance().world.getRegistryManager().getOrThrow(RegistryKeys.DIMENSION_TYPE).containsId(
					Identifier.tryParse(dimNameWidget.getText())
				)
			)
				dimNameWidget.setEditableColor(0xFF5555);
			else
				dimNameWidget.setEditableColor(0xFFFFFF);
		}
	}

	@Override
	public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX,
			int mouseY, boolean hovered, float tickDelta) {
		if (nonDimensionEntry) {
			if (addWidget != null) {
				addWidget.setWidth(Math.min(200, entryWidth - 10));
				addWidget.setX(x + entryWidth / 2 - addWidget.getWidth() / 2);
				addWidget.setY(y);
				addWidget.render(context, mouseX, mouseY, tickDelta);
			}
			return;
		}
		if (dimNameWidget != null && removable) {
			removeWidget.setX(x + entryWidth - removeWidget.getWidth() - 8);
			removeWidget.setY(y);
			removeWidget.render(context, mouseX, mouseY, tickDelta);

			dimNameWidget.setX(x + 8);
			dimNameWidget.setY(y);
			dimNameWidget.render(context, mouseX, mouseY, tickDelta);

			configureWidget.setX(removeWidget.getX() - 4 - configureWidget.getWidth());

		} else {
			configureWidget.setX(x + entryWidth - 8 - configureWidget.getWidth());
			OrderedText text = null;
			if (name != null) text = name.asOrderedText();
			else if (dimensionId == null) text = Text.translatable("config.customfog.default").asOrderedText();
			else text = Text.literal(dimensionId.toString()).asOrderedText();
			context.drawText(textRenderer, text, x + 12, y + 4, 0xFFFFFF, true);
		}
		configureWidget.setY(y);
		configureWidget.render(context, mouseX, mouseY, tickDelta);
		if (pushToServerWidget != null) {
			pushToServerWidget.setY(y);
			pushToServerWidget.setX(configureWidget.getX() - 4 - pushToServerWidget.getWidth());
			pushToServerWidget.render(context, mouseX, mouseY, tickDelta);
			if (pushAsOverrideWidget != null) {
				pushAsOverrideWidget.setY(y);
				pushAsOverrideWidget.setX(pushToServerWidget.getX() - 4 - pushAsOverrideWidget.getWidth());
				pushAsOverrideWidget.render(context, mouseX, mouseY, tickDelta);
			}
		}
	}

	@Override
	public List<? extends Element> children() {
		return this.children;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return ParentElement.super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		return ParentElement.super.mouseReleased(mouseX, mouseY, button);
	}
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		return ParentElement.super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
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
		if (this.focused != null)
			this.focused.setFocused(false);
		this.focused = focused;
		this.setFocused(true);
		if (focused != null)
			focused.setFocused(true);
	}

	@Override
	public Text getNarration() {
		return Text.literal(dimensionId == null ? "Default" : dimensionId.toString());
	}
}
