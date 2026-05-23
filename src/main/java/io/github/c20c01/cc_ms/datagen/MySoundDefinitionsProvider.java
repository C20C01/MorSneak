package io.github.c20c01.cc_ms.datagen;

import io.github.c20c01.cc_ms.MorSneak;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.SoundDefinition;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = MorSneak.ID)
public class MySoundDefinitionsProvider extends SoundDefinitionsProvider {
    public MySoundDefinitionsProvider(PackOutput output) {
        super(output, MorSneak.ID);
    }

    @Override
    public void registerSounds() {
        add(MorSneak.BEEP_SOUND);
        add(MorSneak.FA_SOUND);
    }

    private void add(Holder<SoundEvent> holder) {
        String name = getSoundName(holder);
        add(holder, SoundDefinition.definition().with(sound(name)).subtitle(getSoundSubtitle(name)));
    }

    public static String getSoundName(Holder<SoundEvent> holder) {
        return holder.getRegisteredName();
    }

    public static String getSoundSubtitle(String name) {
        return "sound." + name.replace(':', '.');
    }

    public static String getSoundSubtitle(Holder<SoundEvent> holder) {
        return getSoundSubtitle(getSoundName(holder));
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        event.createProvider(MySoundDefinitionsProvider::new);
    }
}
