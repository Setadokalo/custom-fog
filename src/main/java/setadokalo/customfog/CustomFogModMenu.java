package setadokalo.customfog;

import com.google.common.collect.ImmutableList;
import io.github.prospector.modmenu.api.ConfigScreenFactory;
import io.github.prospector.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.api.ConfigScreen;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.Level;

import java.util.Arrays;
import java.util.Iterator;

public class CustomFogModMenu implements ModMenuApi {
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
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent) //TODO: actual parent screen comes from somewhere
                .setTitle(new TranslatableText("title.customfog.config"))
                .setTransparentBackground(true)
                .setSavingRunnable(() -> {
                    CustomFog.config.saveConfig();
                });
        ConfigEntryBuilder eB = builder.entryBuilder();
        ConfigCategory general = builder.getOrCreateCategory(new TranslatableText("category.customfog.general"));
        general.addEntry(eB.startDropdownMenu(
                new TranslatableText("dropdown.customfog.fogtype"),
                DropdownMenuBuilder.TopCellElementBuilder.of(CustomFog.config.fogType.toString(), str -> {
                    CustomFog.config.fogType = CustomFogConfig.FogType.valueOf(str);
                    return CustomFogConfig.FogType.valueOf(str.toUpperCase().trim());
                })).setSuggestionMode(false)
                .setSelections(Arrays.asList(new CustomFogConfig.FogType[] {CustomFogConfig.FogType.LINEAR, CustomFogConfig.FogType.EXPONENTIAL, CustomFogConfig.FogType.EXPONENTIAL_TWO}))
                .build()
        );
        long currentValue = longify(CustomFog.config.linearFogStartMultiplier);
        general.addEntry(eB.startLongSlider(new TranslatableText("option.customfog.linearslider"), currentValue, 0, 999)
                .setDefaultValue(longify(CustomFogConfig.DEFAULT_LINEAR_START_MULT))
                .setTooltip(new TranslatableText("tooltip.customfog.linearslider"))
                .setTextGetter((val -> {
                    CustomFog.config.linearFogStartMultiplier = delongify(val);
                    return new LiteralText(String.format("%.1f%%", delongify(val) * 100.0F));
                }))
                .setSaveConsumer(n -> {
                    CustomFog.config.linearFogStartMultiplier = delongify(n);
                })
                .build()
        );
        currentValue = longify(CustomFog.config.linearFogEndMultiplier);
        general.addEntry(eB.startLongSlider(new TranslatableText("option.customfog.linearendslider"), currentValue, 1, 1000)
                .setDefaultValue(longify(CustomFogConfig.DEFAULT_LINEAR_END_MULT))
                .setTooltip(new TranslatableText("tooltip.customfog.linearendslider"))
                .setTextGetter((val -> {
                    CustomFog.config.linearFogEndMultiplier = delongify(val);
                    return new LiteralText(String.format("%.1f%%", delongify(val) * 100.0F));
                }))
                .setSaveConsumer(n -> {
                    CustomFog.config.linearFogEndMultiplier = delongify(n);
                })
                .build()
        );
        currentValue = longify(CustomFog.config.expFogMultiplier);
        general.addEntry(eB.startLongSlider(new TranslatableText("option.customfog.expslider"), currentValue, 0, 10000)
                .setDefaultValue(longify(CustomFogConfig.DEFAULT_EXP_MULT))
                .setTooltip(new TranslatableText("tooltip.customfog.expslider"))
                .setTextGetter((val -> {
                    CustomFog.config.expFogMultiplier = delongify(val);
                    return new LiteralText(String.format("%.1f", delongify(val)));
                }))
                .setSaveConsumer(n -> {
                    CustomFog.config.expFogMultiplier = delongify(n);
                })
                .build()
        );
        currentValue = longify(CustomFog.config.exp2FogMultiplier);
        general.addEntry(eB.startLongSlider(new TranslatableText("option.customfog.exp2slider"), currentValue, 0, 10000)
                .setDefaultValue(longify(CustomFogConfig.DEFAULT_EXP2_MULT))
                .setTooltip(new TranslatableText("tooltip.customfog.exp2slider"))
                .setTextGetter((val -> {
                    CustomFog.config.exp2FogMultiplier = delongify(val);
                    return new LiteralText(String.format("%.1f", delongify(val)));
                }))
                .setSaveConsumer(n -> {
                    CustomFog.config.exp2FogMultiplier = delongify(n);
                })
                .build()
		  );
        return builder.build();
    }
    private static float delongify(long val) {
        return ((float)val) / 1000.0F;
    }
    private static long longify(float val) {
        return (long)(val * 1000.0F);
    }
}
