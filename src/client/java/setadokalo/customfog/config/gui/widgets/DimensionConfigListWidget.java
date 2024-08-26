package setadokalo.customfog.config.gui.widgets;

import java.util.Objects;

import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.VertexFormat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.MathHelper;

public class DimensionConfigListWidget extends AlwaysSelectedEntryListWidget<DimensionConfigEntry> {
	private final Screen parent;
	private boolean renderSelection = false;
	private final TextRenderer textRenderer;
	private boolean scrolling;

	public DimensionConfigListWidget(MinecraftClient minecraftClient, int x, int y, int width, int height,
			int itemHeight, Screen parent, TextRenderer textRenderer) {
		super(minecraftClient, width, height, y, itemHeight);
		this.setPosition(x, y);
		// this.right = x + width;
		// this.top = y;
		// this.bottom = y + height;
		this.height = height;
		this.parent = parent;
		this.textRenderer = textRenderer;
		//this.method_31323(false); // this disables rendering a background above and below the scroll list - useful for debugging
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
	public void remove(DimensionConfigEntry entry) {
		this.removeEntry(entry);
	}

	public void addNonDimEntries(boolean addSpacers) {
		// The Add Button - constructed by a magic constructor. I know it's bad, shut up.
		this.add(new DimensionConfigEntry(this, false));
		// A spacer element at the bottom, so the Add Button doesn't get covered up by the config overridden notice.
		// Again, shut up.
		if (addSpacers) {
			this.add(new DimensionConfigEntry(this, true));
			this.add(new DimensionConfigEntry(this, true));
		}
	}

	public void removeNonDimEntries() {
		int currentTarget = this.getEntryCount() - 1;
		while (this.getEntry(currentTarget).nonDimensionEntry) {
			this.remove(currentTarget);
			currentTarget -= 1;
		}
	}

	@Override
	protected void renderList(DrawContext context, int mouseX, int mouseY, float delta) {
		int itemCount = this.getEntryCount();
		Tessellator tessellator = Tessellator.getInstance();
		// BufferBuilder buffer = tessellator.getBuffer();
		for (int index = 0; index < itemCount; ++index) {
			int entryTop = this.getRowTop(index) + 2;
			int entryBottom = this.getRowTop(index) + this.itemHeight;
			if (entryBottom >= this.getY() && entryTop <= this.getBottom()) {
				int entryHeight = this.itemHeight - 4;
				DimensionConfigEntry entry = this.getEntry(index);
				int rowWidth = this.getRowWidth();
				int entryLeft;
				if (this.renderSelection && this.isSelectedEntry(index)) {
					entryLeft = getRowLeft() - 2;
					int selectionRight = getX() + rowWidth + 2;
					RenderSystem.setShader(GameRenderer::getPositionColorProgram);
					float bgIntensity = this.isFocused() ? 1.0F : 0.5F;
					Matrix4f matrix = context.getMatrices().peek().getPositionMatrix();
					BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
					buffer.vertex(matrix, entryLeft, entryTop + entryHeight + 2, 0.0F)
							.color(bgIntensity, bgIntensity, bgIntensity, 1.0F);
					buffer.vertex(matrix, selectionRight, entryTop + entryHeight + 2, 0.0F)
							.color(bgIntensity, bgIntensity, bgIntensity, 1.0F);
					buffer.vertex(matrix, selectionRight, entryTop - 2, 0.0F)
							.color(bgIntensity, bgIntensity, bgIntensity, 1.0F);
					buffer.vertex(matrix, entryLeft, entryTop - 2, 0.0F)
							.color(bgIntensity, bgIntensity, bgIntensity, 1.0F);
					try {
						BuiltBuffer builtBuf = buffer.end();
						BufferRenderer.draw(builtBuf);
						builtBuf.close();
					} catch (Exception e) {
						// Ignored
					}
					buffer = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
					buffer.vertex(matrix, entryLeft + 1, entryTop + entryHeight + 1, 0.0F)
							.color(0.0F, 0.0F, 0.0F, 1.0F);
					buffer.vertex(matrix, selectionRight - 1, entryTop + entryHeight + 1, 0.0F)
							.color(0.0F, 0.0F, 0.0F, 1.0F);
					buffer.vertex(matrix, selectionRight - 1, entryTop - 1, 0.0F)
							.color(0.0F, 0.0F, 0.0F, 1.0F);
					buffer.vertex(matrix, entryLeft + 1, entryTop - 1, 0.0F)
							.color(0.0F, 0.0F, 0.0F, 1.0F);
					try {
						BuiltBuffer builtBuf = buffer.end();
						BufferRenderer.draw(builtBuf);
						builtBuf.close();
					} catch (Exception e) {
						// Ignored
					}
				}

				entryLeft = this.getRowLeft();
				entry.render(context, index, entryTop, entryLeft, rowWidth, entryHeight, mouseX, mouseY, this.isMouseOver(mouseX, mouseY) && Objects.equals(this.getEntryAtPos(mouseX, mouseY), entry), delta);
			}
		}

	}

	// // We're overriding this so that when it calls `Entry#mouseClicked` it localizes the mouseY coordinate
	@Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      this.updateScrollingState(mouseX, mouseY, button);
      if (!this.isMouseOver(mouseX, mouseY)) {
         return false;
      } else {
			for (DimensionConfigEntry entry : this.children()) {
				if (entry.dimNameWidget != null)
					entry.dimNameWidget.setFocused(false);
			}
			DimensionConfigEntry entry = this.getEntryAtPosition(mouseX, mouseY);
         if (entry != null) {
				this.setSelected(entry);
            if (entry.mouseClicked(mouseX, mouseY, button)) {
					this.setFocused(entry);
               this.setDragging(true);
               return true;
            }
         } else if (button == 0) {
            this.clickedHeader(
					(int)(mouseX - (double)(this.getX() + this.width / 2 - this.getRowWidth() / 2)),
					(int)(mouseY - (double)this.getY()) + (int)this.getScrollAmount() - 4
				);
            return true;
         }

         return this.scrolling;
      }
   }

/* shut up, analyzer
	public final int toEntryPos(double yScreenspace) {
		int heightInList = MathHelper.floor(yScreenspace - (double) this.top) - this.headerHeight + (int) this.getScrollAmount() - 4;
		return heightInList % this.itemHeight;
	}
	
	public final int toEntryIndex(double yScreenspace) {
		int heightInList = MathHelper.floor(yScreenspace - (double) this.top) - this.headerHeight + (int) this.getScrollAmount() - 4;
		return heightInList / this.itemHeight;
	}
*/
	public final DimensionConfigEntry getEntryAtPos(double x, double y) {
		int heightInList = MathHelper.floor(y - (double) this.getY()) - this.headerHeight + (int) this.getScrollAmount() - 4;
		int index = heightInList / this.itemHeight;
		return x < (double) this.getScrollbarX() && x >= (double) getRowLeft() && x <= (double) (getRowLeft() + getRowWidth()) && index >= 0 && heightInList >= 0 && index < this.getEntryCount() ? this.children().get(index) : null;
	}

	@Override
   protected void updateScrollingState(double mouseX, double mouseY, int button) {
		this.scrolling = button == 0 && mouseX >= (double)this.getScrollbarX() && mouseX < (double)(this.getScrollbarX() + 6);
		super.updateScrollingState(mouseX, mouseY, button);
	}

	@Override
   protected int getScrollbarX() {
      return this.getRight() - 6;
	}
	

	@Override
	public int getRowWidth() {
		return this.width - (Math.max(0, this.getMaxPosition() - (this.getHeight() - 4)) > 0 ? 18 : 12);
	}

	@Override
	public int getRowLeft() {
		return getX() + 6;
	}
}
