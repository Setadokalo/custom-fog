package setadokalo.customfog.config.gui.widgets;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.Element;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import setadokalo.customfog.CustomFog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class WarningWidget implements Drawable, Element {
	private List<Text> warningText;
	private int x, y;
	private int width;

	public WarningWidget(int x, int y, int width, Text... lines) {
		this.warningText = Arrays.asList(lines);
		this.x = x;
		this.y = y;
		this.width = width;
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
//		CustomFog.log(Level.INFO, "hhhh");
		MinecraftClient.getInstance().getTextureManager().bindTexture(new Identifier("textures/gui/toasts.png"));
		DrawableHelper.drawTexture(matrices, this.x, this.y, 0, 64, 20, 32, 256, 256);
		int texX = 20;
		for (; texX < this.width; texX += 137) {
			DrawableHelper.drawTexture(matrices, this.x + texX, this.y,
				20, 64,
				Math.min(137, width - texX), 32,
				256, 256);
		}
		DrawableHelper.drawTexture(matrices, this.x + this.width - 3, this.y,
			157, 64,
			3, 32,
			256, 256);
		TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
		int y = 7;
		for (Text line : this.warningText) {
			OrderedText orderedText = line.asOrderedText();
			int color = 0xFFFFFFFF;
			if (line.getStyle() != null && line.getStyle().getColor() != null)
				color = line.getStyle().getColor().getRgb();
			textRenderer.drawWithShadow(matrices, orderedText, this.x + 20, this.y + y, color);
			y += textRenderer.fontHeight + 2;
		}
	}
}
