package io.github.c20c01.cc_ms.client.buzzer;

import com.mojang.logging.LogUtils;
import io.github.c20c01.cc_ms.MorSneak;
import io.github.c20c01.cc_ms.config.BuzzConfigEntry;
import io.github.c20c01.cc_ms.config.MorSneakConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class BuzzInstanceFactory {
    private static final BuzzConfigEntry DEFAULT_CONFIG = new BuzzConfigEntry(MorSneak.BEEP_SOUND).withLooping();
    private static final Function<Long, BuzzInstance> DEFAULT_FACTORY = seed -> new BuzzInstance(MorSneak.BEEP_SOUND.value(), true, seed);

    private final List<BuzzConfigEntry> configEntries;
    private final List<Function<Long, BuzzInstance>> factories;

    public BuzzInstanceFactory() {
        this.configEntries = new ArrayList<>();
        this.factories = new ArrayList<>();
    }

    public BuzzInstance get(byte code, long seed) {
        if (code > 0 && code <= factories.size()) {
            return factories.get(code - 1).apply(seed);
        } else {
            return new BuzzInstance(MorSneak.BEEP_SOUND.value(), true, seed);
        }
    }

    public List<BuzzConfigEntry> getConfigEntries() {
        return configEntries;
    }

    /**
     * Load the sound events from the config and create the factories.
     */
    public void update(List<? extends String> buzzStrings) {
        configEntries.clear();
        factories.clear();

        int listSize = buzzStrings.size();
        if (listSize > MorSneakConfig.MAX_BUZZ_SOUNDS) {
            LogUtils.getLogger().warn("Too many buzz sounds in config, only the first {} will be used.", MorSneakConfig.MAX_BUZZ_SOUNDS);
            listSize = MorSneakConfig.MAX_BUZZ_SOUNDS;
        }

        for (int i = 0; i < listSize; i++) {
            BuzzConfigEntry config = new BuzzConfigEntry(buzzStrings.get(i));
            configEntries.add(config);
            factories.add(createFactory(config));
        }
        if (factories.isEmpty()) {
            configEntries.add(DEFAULT_CONFIG);
            factories.add(DEFAULT_FACTORY);
        }
    }

    private Function<Long, BuzzInstance> createFactory(BuzzConfigEntry config) {
        String location = config.getLocation();
        Holder.Reference<SoundEvent> soundEventRef = getSoundEventRef(location);
        if (soundEventRef == null) {
            return DEFAULT_FACTORY; // ConfigTracker will correct the config, no need to log warning here.
        }

        SoundEvent soundEvent = soundEventRef.value();
        if (config.isLooping()) return seed -> new BuzzInstance(soundEvent, true, seed);
        if (config.isPitchRandom()) return seed -> new BuzzInstance(soundEvent, 0.5f, 1f, seed);
        return seed -> new BuzzInstance(soundEvent, false, seed);
    }

    @Nullable
    private static Holder.Reference<SoundEvent> getSoundEventRef(String location) {
        Identifier identifier = Identifier.tryParse(location);
        if (identifier == null) return null;

        return BuiltInRegistries.SOUND_EVENT.get(identifier).orElse(null);
    }
}
