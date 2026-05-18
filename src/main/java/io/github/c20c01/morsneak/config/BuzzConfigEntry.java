package io.github.c20c01.morsneak.config;

import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;

public class BuzzConfigEntry {
    public static final String LOOP_SUFFIX = "_loop";
    public static final String PITCH_RANDOM_SUFFIX = "_random";

    private final String location;

    private boolean looping;
    private boolean pitchRandom;

    public BuzzConfigEntry(Holder<SoundEvent> soundEvent) {
        this.location = soundEvent.unwrapKey().map(key -> key.identifier().toShortString()).orElse("[unregistered]");
    }

    public BuzzConfigEntry(SoundEvent soundEvent) {
        this.location = soundEvent.location().toShortString();
    }

    public BuzzConfigEntry(String configString) {
        if (configString.endsWith(LOOP_SUFFIX)) {
            this.looping = true;
            this.location = configString.substring(0, configString.length() - LOOP_SUFFIX.length());
            return;
        }

        if (configString.endsWith(PITCH_RANDOM_SUFFIX)) {
            this.pitchRandom = true;
            this.location = configString.substring(0, configString.length() - PITCH_RANDOM_SUFFIX.length());
            return;
        }

        this.location = configString;
    }

    public String getLocation() {
        return location;
    }

    public boolean isLooping() {
        return looping;
    }

    public boolean isPitchRandom() {
        return pitchRandom;
    }

    public BuzzConfigEntry withLooping() {
        this.looping = true;
        this.pitchRandom = false;
        return this;
    }

    public BuzzConfigEntry withPitchRandom() {
        this.pitchRandom = true;
        this.looping = false;
        return this;
    }

    @Override
    public String toString() {
        return location + (looping ? LOOP_SUFFIX : "") + (pitchRandom ? PITCH_RANDOM_SUFFIX : "");
    }
}
