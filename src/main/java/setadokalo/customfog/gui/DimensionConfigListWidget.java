package setadokalo.customfog.gui;

import java.util.Objects;

import com.mojang.blaze3d.systems.RenderSystem;

import org.apache.logging.log4j.Level;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix4f;
import setadokalo.customfog.CustomFog;

public class DimensionConfigListWidget extends AlwaysSelectedEntryListWidget<DimensionConfigEntry> {
	private Screen parent;
	private boolean renderSelection;
	private TextRenderer textRenderer;

	public DimensionConfigListWidget(MinecraftClient minecraftClient, int x, int y, int width, int height,
			int itemheight, Screen parent, TextRenderer textRenderer) {
		super(minecraftClient, width, height, y, y + height, itemheight);
		this.left = x;
		this.right = x + width;
		this.top = y;
		this.bottom = y + height;
		this.height = parent.height;
		this.setRenderSelection(false);
		this.parent = parent;
		this.textRenderer = textRenderer;
		// this.method_31323(false); // this disables rendering a bugged background above the scroll list
	}

	public TextRenderer getTextRenderer() {
		return textRenderer;
	}

	public Screen getParent() {
		return parent;
	}

	public void add(DimensionConfigEntry entry) {
		this.addEntry(entry);
	}

	@Override
	public void setRenderSelection(boolean renderSelection) {
		super.setRenderSelection(renderSelection);
		this.renderSelection = renderSelection;
	}


	@Override
	protected void renderList(MatrixStack matrices, int x, int y, int mouseX, int mouseY, float delta) {
		int itemCount = this.getItemCount();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		// this is probably a bad idea, but bite me okay? okay
		for (int index = 0; index < itemCount; ++index) {
			int entryTop = this.getRowTop(index) + 2;
			int entryBottom = this.getRowTop(index) + this.itemHeight;
			if (entryBottom >= this.top && entryTop <= this.bottom) {
				int entryHeight = this.itemHeight - 4;
				DimensionConfigEntry entry = this.getEntry(index);
				int rowWidth = this.getRowWidth();
				int entryLeft;
				if (this.renderSelection && this.isSelectedItem(index)) {
					entryLeft = getRowLeft() - 2 + entry.getXOffset();
					int selectionRight = x + rowWidth + 2;
					RenderSystem.disableTexture();
					float bgIntensity = this.isFocused() ? 1.0F : 0.5F;
					RenderSystem.color4f(bgIntensity, bgIntensity, bgIntensity, 1.0F);
					Matrix4f matrix = matrices.peek().getModel();
					buffer.begin(7, VertexFormats.POSITION);
					buffer.vertex(matrix, entryLeft, entryTop + entryHeight + 2, 0.0F).next();
					buffer.vertex(matrix, selectionRight, entryTop + entryHeight + 2, 0.0F).next();
					buffer.vertex(matrix, selectionRight, entryTop - 2, 0.0F).next();
					buffer.vertex(matrix, entryLeft, entryTop - 2, 0.0F).next();
					tessellator.draw();
					RenderSystem.color4f(0.0F, 0.0F, 0.0F, 1.0F);
					buffer.begin(7, VertexFormats.POSITION);
					buffer.vertex(matrix, entryLeft + 1, entryTop + entryHeight + 1, 0.0F).next();
					buffer.vertex(matrix, selectionRight - 1, entryTop + entryHeight + 1, 0.0F).next();
					buffer.vertex(matrix, selectionRight - 1, entryTop - 1, 0.0F).next();
					buffer.vertex(matrix, entryLeft + 1, entryTop - 1, 0.0F).next();
					tessellator.draw();
					RenderSystem.enableTexture();
				}

				entryLeft = this.getRowLeft();
				entry.render(matrices, index, entryTop, entryLeft, rowWidth, entryHeight, mouseX, mouseY, this.isMouseOver(mouseX, mouseY) && Objects.equals(this.getEntryAtPos(mouseX, mouseY), entry), delta);
			}
		}

	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int btn_idx) {
		CustomFog.log(Level.INFO, "clicked in the list");
		return super.mouseClicked(mouseX, mouseY, btn_idx);
	}


	public final DimensionConfigEntry getEntryAtPos(double x, double y) {
		int heightInList = MathHelper.floor(y - (double) this.top) - this.headerHeight + (int) this.getScrollAmount() - 4;
		int index = heightInList / this.itemHeight;
		return x < (double) this.getScrollbarPositionX() && x >= (double) getRowLeft() && x <= (double) (getRowLeft() + getRowWidth()) && index >= 0 && heightInList >= 0 && index < this.getItemCount() ? this.children().get(index) : null;
	}

	@Override
   protected int getScrollbarPositionX() {
      return this.right - 6;
	}
	

	@Override
	public int getRowWidth() {
		return this.width - (Math.max(0, this.getMaxPosition() - (this.bottom - this.top - 4)) > 0 ? 18 : 12);
	}

	@Override
	public int getRowLeft() {
		return left + 6;
	}
}
