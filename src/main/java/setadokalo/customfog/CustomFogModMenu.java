package setadokalo.customfog;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.gui.screen.Screen;
import setadokalo.customfog.config.gui.CustomFogConfigScreen;

public class CustomFogModMenu implements ModMenuApi {

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return CustomFogModMenu::genConfig;
	}

	private static Screen genConfig(Screen parent) {
		return new CustomFogConfigScreen(parent);
	}
}
