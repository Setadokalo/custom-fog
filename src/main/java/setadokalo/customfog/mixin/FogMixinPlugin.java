package setadokalo.customfog.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import setadokalo.customfog.config.CustomFogMixinConfig;

import java.util.List;
import java.util.Set;

public class FogMixinPlugin implements IMixinConfigPlugin {
	@Override
	public void onLoad(String mixinPackage) {

	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return switch (mixinClassName) {
			case "setadokalo.customfog.mixin.RendererMixin" -> !CustomFogMixinConfig.getConfig().useAggressiveFog;
			case "setadokalo.customfog.mixin.RendererMixinAggressive" -> CustomFogMixinConfig.getConfig().useAggressiveFog;
			default -> true;
		};
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

	}

	@Override
	public List<String> getMixins() {
		if (FabricLoader.getInstance().isModLoaded("sodium"))
			return java.util.Arrays.asList(new String[] {"setadokalo.customfog.mixin.SodiumOptionsGUIMixin", "setadokalo.customfog.mixin.SodiumShaderLoadMixin"});
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

	}
}
