package io.github.c20c01.morsneak.client.buzzer;

import io.github.c20c01.morsneak.MorSneak;
import io.github.c20c01.morsneak.config.MorSneakConfig;
import io.github.c20c01.morsneak.radio.RadioSignal;
import io.github.c20c01.morsneak.radio.portable.RadioSignalOffset;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec2;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;

/**
 * Singleton buzzer that plays the radio signal as sound.
 * <p>
 * When get {@link io.github.c20c01.morsneak.network.RadioSignalPacket RadioSignalPacket}, call {@link #updateSignal} to update and play the signal.
 * <p>
 * When get {@link io.github.c20c01.morsneak.network.RadioSignalOffsetPacket RadioSignalOffsetPacket}, call {@link #updateOffset} to update the offset.
 * <p>
 * When get {@link ModConfigEvent}, call {@link #modConfig} to update the buzz factory with the new config.
 */
@EventBusSubscriber(modid = MorSneak.ID, value = Dist.CLIENT)
public class RadioBuzzer {
    private static RadioBuzzer instance;

    private final BuzzInstanceFactory buzzFactory;

    private Vec2 offset;
    private BuzzInstance buzz;

    private RadioBuzzer() {
        offset = Vec2.ZERO;
        buzzFactory = new BuzzInstanceFactory();
    }

    public static RadioBuzzer getInstance() {
        if (instance == null) instance = new RadioBuzzer();
        return instance;
    }

    public void updateSignal(byte code, long seed) {
        if (buzz != null && buzz.isLooping()) Minecraft.getInstance().getSoundManager().stop(buzz);
        if (code == RadioSignal.CODE_EMPTY) return;

        buzz = buzzFactory.get(code, seed);
        buzz.setOffset(offset);
        Minecraft.getInstance().getSoundManager().play(buzz);
    }

    public void updateOffset(RadioSignalOffset signalOffset) {
        offset = signalOffset.toVec2();
        if (buzz != null) buzz.setOffset(offset);
    }

    public BuzzInstanceFactory getBuzzFactory() {
        return buzzFactory;
    }

    @SubscribeEvent
    public static void modConfig(ModConfigEvent.Loading event) {
        getInstance().buzzFactory.update(MorSneakConfig.BUZZ_STRINGS.get());
    }

    @SubscribeEvent
    public static void modConfig(ModConfigEvent.Reloading event) {
        getInstance().buzzFactory.update(MorSneakConfig.BUZZ_STRINGS.get());
    }
}
