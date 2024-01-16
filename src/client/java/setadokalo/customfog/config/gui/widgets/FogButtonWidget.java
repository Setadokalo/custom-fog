package setadokalo.customfog.config.gui.widgets;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class FogButtonWidget extends TexturedButtonWidget {
    public static record FogButtonCoords(
        int enabledU, int enabledV, 
        int disabledU, int disabledV, 
        int focusedU, int focusedV, 
        int disabledFocusedU, int disabledFocusedV)
    {
        public FogButtonCoords(int unfocusedU, int unfocusedV, int focusedU, int focusedV) {
            this(unfocusedU, unfocusedV, unfocusedU, unfocusedV, focusedU, focusedV, focusedU, focusedV);
        }
        public FogButtonCoords(int enabledU, int enabledV, int disabledU, int disabledV, int focusedU, int focusedV) {
            this(enabledU, enabledV, disabledU, disabledV, focusedU, focusedV, disabledU, disabledV);
        }
        public int getU(boolean enabled, boolean focused) {
            if (enabled) {
                return focused ? this.focusedU : this.enabledU;
            }
            return focused ? this.disabledFocusedU : this.disabledU;
        }
        public int getV(boolean enabled, boolean focused) {
            if (enabled) {
                return focused ? this.focusedV : this.enabledV;
            }
            return focused ? this.disabledFocusedV : this.disabledV;
        }
    }

    private static final Identifier GUI_TEX = new Identifier("custom-fog", "textures/gui/cfog-gui.png");
    protected FogButtonCoords coords;

    public FogButtonWidget(int x, int y, int width, int height, FogButtonCoords coords, ButtonWidget.PressAction pressAction) {
        this(x, y, width, height, coords, pressAction, ScreenTexts.EMPTY);
    }

    public FogButtonWidget(int x, int y, int width, int height, FogButtonCoords coords, ButtonWidget.PressAction pressAction, Text text) {
        super(x, y, width, height, null, pressAction, text);
        this.coords = coords;
    }

    public FogButtonWidget(int width, int height, FogButtonCoords coords, ButtonWidget.PressAction pressAction, Text text) {
        this(0, 0, width, height, coords, pressAction, text);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        int u = this.coords.getU(this.isNarratable(), this.isSelected());
        int v = this.coords.getV(this.isNarratable(), this.isSelected());
        context.drawTexture(GUI_TEX, this.getX(), this.getY(), u, v, this.width, this.height);
    }
}