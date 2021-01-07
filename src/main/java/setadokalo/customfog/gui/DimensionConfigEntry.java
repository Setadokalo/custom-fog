package setadokalo.customfog.gui;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import setadokalo.customfog.DimensionConfig;

public class DimensionConfigEntry extends AlwaysSelectedEntryListWidget.Entry<DimensionConfigEntry> {
	protected boolean removable;
	protected TextRenderer textRenderer;
	protected DimensionConfigListWidget parentList;
	protected String dimensionId;
	protected DimensionConfig config;
	protected TextFieldWidget dimNameWidget;

	public DimensionConfigEntry(DimensionConfigListWidget parent, boolean removable, String dimId, DimensionConfig config) {
		this.removable = removable;
		parentList = parent;
		this.textRenderer = parent.getTextRenderer();
		dimensionId = dimId;
		this.config = config;
		if (true) {
			dimNameWidget = new TextFieldWidget(textRenderer, 8, 0, 100, 20, new LiteralText(dimensionId));
			dimNameWidget.setChangedListener((newId -> {})); // TODO: implement a system for changing the name of a dimension
		}
	}

	@Override
	public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX,
			int mouseY, boolean hovered, float tickDelta) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		dimNameWidget.render(matrices, mouseX, mouseY, tickDelta);
		// DrawableHelper.drawCenteredText(matrices, this.textRenderer, new LiteralText(this.dimensionId), x + (entryWidth / 2), y + entryHeight / 2, 0xFFFFFF);

	}

	public int getXOffset() {
		return 0;
	}
	
}
