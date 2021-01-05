package setadokalo.customfog;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;

public class CustomFogModMenu implements ModMenuApi {
	protected static final float RESOLUTION = 1000.0F;


	@Override
	@Deprecated
	public String getModId() {
		return CustomFog.MOD_ID;
	}

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return CustomFogModMenu::genConfig;
	}

	private static Screen genConfig(Screen parent) {
		return new CustomFogConfigScreen(parent);
	}

	private static float delongify(long val) {
		return ((float) val) / RESOLUTION;
	}

	private static long longify(float val) {
		return (long) (val * RESOLUTION);
	}
}
