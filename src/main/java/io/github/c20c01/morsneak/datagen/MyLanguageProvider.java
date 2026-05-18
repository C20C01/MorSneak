package io.github.c20c01.morsneak.datagen;

import io.github.c20c01.morsneak.MorSneak;
import net.minecraft.data.DataGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = MorSneak.ID)
public class MyLanguageProvider extends LanguageProvider {
    private static final String EN_US = "en_us";
    private static final String ZH_CN = "zh_cn";

    private final String locale;

    private MyLanguageProvider(DataGenerator gen, String locale) {
        super(gen.getPackOutput(), MorSneak.ID, locale);
        this.locale = locale;
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        var generator = event.getGenerator();
        generator.addProvider(true, new MyLanguageProvider(generator, EN_US));
        generator.addProvider(true, new MyLanguageProvider(generator, ZH_CN));
    }

    @Override
    protected void addTranslations() {
        this.addItem(MorSneak.PORTABLE_RADIO, switch (this.locale) {
            case EN_US -> "Portable Radio";
            case ZH_CN -> "便携电台";
            default -> throw new IllegalStateException();
        });
        this.addBlock(MorSneak.RADIO_TRANSMITTER_BLOCK, switch (this.locale) {
            case EN_US -> "Radio Transmitter";
            case ZH_CN -> "无线电发射器";
            default -> throw new IllegalStateException();
        });
        this.addBlock(MorSneak.RADIO_RECEIVER_BLOCK, switch (this.locale) {
            case EN_US -> "Radio Receiver";
            case ZH_CN -> "无线电接收器";
            default -> throw new IllegalStateException();
        });

        this.add(MySoundDefinitionsProvider.getSoundSubtitle(MorSneak.BEEP_SOUND), switch (this.locale) {
            case EN_US -> "Beep";
            case ZH_CN -> "哔";
            default -> throw new IllegalStateException();
        });
        this.add(MySoundDefinitionsProvider.getSoundSubtitle(MorSneak.FA_SOUND), switch (this.locale) {
            case EN_US -> "Fa";
            case ZH_CN -> "发";
            default -> throw new IllegalStateException();
        });

        this.add(MorSneak.TEXT_KEY_TAB_TITLE, switch (this.locale) {
            case EN_US -> "MorSneak";
            case ZH_CN -> "撅电报";
            default -> throw new IllegalStateException();
        });

        this.add(MorSneak.TEXT_KEY_PORTABLE_RADIO_FULL, switch (this.locale) {
            case EN_US -> "Radio is full of positions";
            case ZH_CN -> "电台无法容纳更多位置";
            default -> throw new IllegalStateException();
        });

        this.add(MorSneak.TEXT_KEY_PORTABLE_RADIO_TITLE, switch (this.locale) {
            case EN_US -> "Portable Radio";
            case ZH_CN -> "便携电台";
            default -> throw new IllegalStateException();
        });
        this.add(MorSneak.TEXT_KEY_PORTABLE_RADIO_SELECT_MODE, switch (this.locale) {
            case EN_US -> "Channel Selection Mode";
            case ZH_CN -> "频道选择模式";
            default -> throw new IllegalStateException();
        });
        this.add(MorSneak.TEXT_KEY_PORTABLE_RADIO_DELETE_MODE, switch (this.locale) {
            case EN_US -> "Channel Deletion Mode";
            case ZH_CN -> "频道删除模式";
            default -> throw new IllegalStateException();
        });
        this.add(MorSneak.TEXT_KEY_PORTABLE_RADIO_SOUND_MODE, switch (this.locale) {
            case EN_US -> "Sound Setting Mode";
            case ZH_CN -> "声音设置模式";
            default -> throw new IllegalStateException();
        });
        this.add(MorSneak.TEXT_KEY_PORTABLE_RADIO_MIC, switch (this.locale) {
            case EN_US -> "Toggle Broadcast State";
            case ZH_CN -> "切换广播状态";
            default -> throw new IllegalStateException();
        });
        this.add(MorSneak.TEXT_KEY_PORTABLE_RADIO_SELECT, switch (this.locale) {
            case EN_US -> "Click to Select";
            case ZH_CN -> "点击加入频道";
            default -> throw new IllegalStateException();
        });
        this.add(MorSneak.TEXT_KEY_PORTABLE_RADIO_UNSELECT, switch (this.locale) {
            case EN_US -> "Click to Unselect";
            case ZH_CN -> "点击退出频道";
            default -> throw new IllegalStateException();
        });
        this.add(MorSneak.TEXT_KEY_PORTABLE_RADIO_DELETE, switch (this.locale) {
            case EN_US -> "Click to Delete";
            case ZH_CN -> "点击删除频道";
            default -> throw new IllegalStateException();
        });
        this.add(MorSneak.TEXT_KEY_PORTABLE_RADIO_OTHER_OPEN, switch (this.locale) {
            case EN_US -> "Cannot open multiple radios at the same time";
            case ZH_CN -> "无法同时打开多个电台";
            default -> throw new IllegalStateException();
        });

        this.add("morsneak.configuration.buzz_sounds", switch (this.locale) {
            case EN_US -> "Radio Buzzer Sounds";
            case ZH_CN -> "电台声音设置";
            default -> throw new IllegalStateException();
        });
        this.add("morsneak.configuration.buzz_sounds.button", switch (this.locale) {
            case EN_US -> "Edit";
            case ZH_CN -> "编辑";
            default -> throw new IllegalStateException();
        });
        this.add("morsneak.configuration.title", switch (this.locale) {
            case EN_US -> "MorSneak Server Configuration";
            case ZH_CN -> "撅电报服务器配置";
            default -> throw new IllegalStateException();
        });
        this.add("morsneak.configuration.section.morsneak.server.toml", switch (this.locale) {
            case EN_US -> "MorSneak Server Configuration";
            case ZH_CN -> "撅电报服务器配置";
            default -> throw new IllegalStateException();
        });
        this.add("morsneak.configuration.section.morsneak.server.toml.title", switch (this.locale) {
            case EN_US -> "MorSneak Server Configuration";
            case ZH_CN -> "撅电报服务器配置";
            default -> throw new IllegalStateException();
        });
        this.add("morsneak.configuration.buzz_sounds.tooltip", switch (this.locale) {
            case EN_US -> """
                    Configure the sounds used by the portable radio.
                    Format: <sound>[<suffix>]
                    <sound> - Resource location of the sound event
                    _loop - Suffix indicating the sound should loop
                    _random - Suffix indicating the sound should have random pitch""";
            case ZH_CN -> """
                    配置便携电台使用的声音。
                    格式：<sound>[<后缀>]
                    <sound> - 声音事件的命名空间ID
                    _loop - 表示声音应循环播放的后缀
                    _random - 表示声音应具有随机音高的后缀""";
            default -> throw new IllegalStateException();
        });
    }
}