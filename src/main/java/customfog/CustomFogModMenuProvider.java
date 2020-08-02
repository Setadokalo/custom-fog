package customfog;

import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import net.minecraft.client.gui.screen.Screen;

import java.util.Optional;
import java.util.function.Supplier;

public class CustomFogModMenuProvider implements ModMenuApi {
	@Override
	public String getModId() {
		return CustomFog.MOD_ID;
	}

	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return (screen) -> {
			return AutoConfig.getConfigScreen(CustomFogConfig.class, screen).get();
		};
	}
}
