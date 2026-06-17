package io.github.c20c01.cc_ms.client.buzzer;

import com.mojang.logging.LogUtils;
import io.github.c20c01.cc_ms.MorSneak;
import io.github.c20c01.cc_ms.config.BuzzConfigEntry;
import io.github.c20c01.cc_ms.config.MorSneakConfig;
import io.github.c20c01.cc_ms.datagen.MySoundDefinitionsProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class BuzzInstanceFactory {
    private static final Component LOOP_SOUND_SUFFIX = Component.literal(" ∞");
    private static final Component RANDOM_PITCH_SUFFIX = Component.literal(" ⚄");

    private static final MutableComponent DEFAULT_TITLE = Component.translatable(MySoundDefinitionsProvider.getSoundSubtitle(MorSneak.BEEP_SOUND));
    private static final Function<Long, BuzzInstance> DEFAULT_FACTORY = seed -> new BuzzInstance(MorSneak.BEEP_SOUND.value(), true, seed);

    private final List<Component> soundTitles;
    private final List<Function<Long, BuzzInstance>> factories;

    public BuzzInstanceFactory() {
        this.soundTitles = new ArrayList<>();
        this.factories = new ArrayList<>();
    }

    public BuzzInstance get(byte code, long seed) {
        var factory = code > 0 && code <= factories.size() ? factories.get(code - 1) : DEFAULT_FACTORY;
        return factory.apply(seed);
    }

    public List<Component> getSoundTitles() {
        return soundTitles;
    }

    /**
     * Load the sound events from the config and create the factories.
     */
    public void update(List<? extends String> buzzStrings) {
        soundTitles.clear();
        factories.clear();

        int listSize = buzzStrings.size();
        if (listSize > MorSneakConfig.MAX_BUZZ_SOUNDS) {
            LogUtils.getLogger().warn("Too many buzz sounds in config, only the first {} will be used.", MorSneakConfig.MAX_BUZZ_SOUNDS);
            listSize = MorSneakConfig.MAX_BUZZ_SOUNDS;
        }

        for (int i = 0; i < listSize; i++) add(new BuzzConfigEntry(buzzStrings.get(i)));
        if (factories.isEmpty()) addDefault();
    }

    private void add(BuzzConfigEntry config) {
        Identifier identifier = Identifier.tryParse(config.getLocation());
        if (identifier == null) {
            addDefault();
            return;
        }

        var soundEvent = BuiltInRegistries.SOUND_EVENT.get(identifier).orElse(null);
        if (soundEvent == null) {
            addDefault();
            return;
        }

        var weighedSoundEvents = Minecraft.getInstance().getSoundManager().getSoundEvent(identifier);
        MutableComponent title;
        if (weighedSoundEvents != null && weighedSoundEvents.getSubtitle() != null) {
            title = weighedSoundEvents.getSubtitle().copy();
        } else {
            title = DEFAULT_TITLE;
        }

        if (config.isLooping()) {
            factories.add(seed -> new BuzzInstance(soundEvent.value(), true, seed));
            soundTitles.add(title.append(LOOP_SOUND_SUFFIX));
        } else if (config.isPitchRandom()) {
            factories.add(seed -> new BuzzInstance(soundEvent.value(), 0.5f, 1f, seed));
            soundTitles.add(title.append(RANDOM_PITCH_SUFFIX));
        } else {
            factories.add(seed -> new BuzzInstance(soundEvent.value(), false, seed));
            soundTitles.add(title);
        }
    }

    private void addDefault() {
        soundTitles.add(DEFAULT_TITLE.append(LOOP_SOUND_SUFFIX));
        factories.add(DEFAULT_FACTORY);
    }
}
