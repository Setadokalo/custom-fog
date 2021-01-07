package setadokalo.customfog.gui;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;

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
import setadokalo.customfog.CustomFog;
import setadokalo.customfog.DimensionConfig;

public class DimensionConfigEntry extends AlwaysSelectedEntryListWidget.Entry<DimensionConfigEntry>
		implements ParentElement {


	private static final int REMOVE_WIDGET_WIDTH = 20;
	@Nullable
	private Element focused;
	private boolean dragging;
	protected final boolean removable;
	protected final boolean justAdd; // I'm lazy as hell and I'm not gonna implement this properly
	protected TextRenderer textRenderer;
	protected DimensionConfigListWidget parentList;
	public String originalDimId;
	public String dimensionId;
	public DimensionConfig config;
	protected List<Element> children = new ArrayList<>();
	@Nullable
	protected TextFieldWidget dimNameWidget;
	protected TexturedButtonWidget removeWidget;
	protected ButtonWidget addWidget = new ButtonWidget(-20000, -20000, 80, 20, new TranslatableText("button.customfog.add"), btn -> {
		parentList.remove(this);
		parentList.add(new DimensionConfigEntry(parentList, true, null, CustomFog.config.defaultConfig.copy()));
		parentList.add(this);
	});
	protected ButtonWidget configureWidget;

	public DimensionConfigEntry(DimensionConfigListWidget parent, boolean removable, String dimId,
			DimensionConfig config) {
		justAdd = false;
		this.removable = removable;
		parentList = parent;
		this.textRenderer = parent.getTextRenderer();
		dimensionId = dimId;
		originalDimId = dimId;
		this.config = config;
		if (removable) {
			dimNameWidget = new TextFieldWidget(textRenderer, 0, 0, 150, 20, new LiteralText(dimensionId));
			dimNameWidget.setText(dimensionId);
			dimNameWidget.setChangedListener(str -> dimensionId = str);
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
					CustomFog.config.dimensions.remove(this.originalDimId);
					parentList.remove(this);
				}
			);
			children.add(removeWidget);
		}
		configureWidget = new ButtonWidget(-20000, -20000, 
		removable ? 80 : 84 + REMOVE_WIDGET_WIDTH, 20, 
		new TranslatableText("button.customfog.configure"), 
		btn -> ((CustomFogConfigScreen)this.parentList.getParent()).openScreen(new DimensionConfigScreen(this.parentList.getParent())));
		children.add(configureWidget);
	}
	public DimensionConfigEntry(DimensionConfigListWidget parent) {
		originalDimId = "";
		parentList = parent;
		justAdd = true;
		removable = false;
		children.add(addWidget);
	}

	@Override
	public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX,
			int mouseY, boolean hovered, float tickDelta) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		if (justAdd) {
			addWidget.setWidth(Math.min(200, entryWidth - 10));
			addWidget.x = x + entryWidth / 2 - addWidget.getWidth() / 2;
			addWidget.y = y;
			addWidget.render(matrices, mouseX, mouseY, tickDelta);
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
			drawText(matrices, textRenderer, new LiteralText(dimensionId), x + 12, y + 4, 0xFFFFFF);
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
	public Element getFocused() {
		return this.focused;
	}

	@Override
	public void setFocused(Element focused) {
		this.focused = focused;
	}
}
