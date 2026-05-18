package io.github.c20c01.morsneak.config;

import io.github.c20c01.morsneak.MorSneak;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class MorSneakConfig {
    public static final byte MAX_BUZZ_SOUNDS = 64;

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final String BEEP = new BuzzConfigEntry(MorSneak.BEEP_SOUND).withLooping().toString();
    private static final String FA = new BuzzConfigEntry(MorSneak.FA_SOUND).withLooping().toString();
    private static final List<String> DEFAULT_BUZZ_STRINGS = List.of(
            BEEP,
            FA,
            new BuzzConfigEntry(SoundEvents.VILLAGER_CELEBRATE).toString(),
            new BuzzConfigEntry(SoundEvents.VILLAGER_NO).toString(),

            new BuzzConfigEntry(SoundEvents.GENERIC_EAT).toString(),
            new BuzzConfigEntry(SoundEvents.GENERIC_DRINK).toString(),
            new BuzzConfigEntry(SoundEvents.ARMOR_EQUIP_GENERIC).toString(),
            new BuzzConfigEntry(SoundEvents.STONE_BREAK).toString(),

            new BuzzConfigEntry(SoundEvents.NOTE_BLOCK_TRUMPET_OXIDIZED).withPitchRandom().toString(),
            new BuzzConfigEntry(SoundEvents.CREEPER_PRIMED).toString(),
            new BuzzConfigEntry(SoundEvents.CAT_HISS_BABY).toString(),
            new BuzzConfigEntry(SoundEvents.CAT_BEG_FOR_FOOD_BABY).toString(),

            new BuzzConfigEntry(SoundEvents.NOTE_BLOCK_BELL).withLooping().toString(),
            new BuzzConfigEntry(SoundEvents.FIREWORK_ROCKET_BLAST).toString(),
            new BuzzConfigEntry(SoundEvents.ANVIL_PLACE).toString(),
            new BuzzConfigEntry(SoundEvents.CROSSBOW_SHOOT).toString()
    );

    /**
     * When reset in game, {@link ModConfigSpec.Builder#defineList(List, Supplier, Supplier, Predicate, ModConfigSpec.Range) defineList}
     * will log debug message: {@code List on key buzz_sounds is deemed to need correction, as it is null, not a list, or the wrong size.}
     * It seems will not cause any issues.
     */
    public static final ModConfigSpec.ConfigValue<List<? extends String>> BUZZ_STRINGS = BUILDER
            .comment("""
                    Configure the sounds used by the portable radio: <sound>[<suffix>]
                    <sound> - Resource location of the sound event
                    _loop - Suffix indicating the sound should loop
                    _random - Suffix indicating the sound should have random pitch"""
            )
            .defineList(
                    List.of("buzz_sounds"),
                    () -> DEFAULT_BUZZ_STRINGS,
                    () -> BEEP,
                    MorSneakConfig::validateBuzzStrings,
                    ModConfigSpec.Range.of(0, MAX_BUZZ_SOUNDS)
            );

    public static final ModConfigSpec SPEC = BUILDER.build();

    private static boolean validateBuzzStrings(Object o) {
        if (!(o instanceof String s)) return false;
        if (s.equals(BEEP) || s.equals(FA)) return true; // Allow the default sounds even if they are not registered yet
        Identifier id = Identifier.tryParse(new BuzzConfigEntry(s).getLocation());
        return id != null && BuiltInRegistries.SOUND_EVENT.containsKey(id);
    }
}
