package io.github.c20c01.cc_ms.radio.portable;

import io.github.c20c01.cc_ms.MorSneak;
import io.github.c20c01.cc_ms.config.BuzzConfigEntry;
import io.github.c20c01.cc_ms.config.MorSneakConfig;
import io.github.c20c01.cc_ms.network.RadioSignalOffsetPacket;
import io.github.c20c01.cc_ms.network.RadioSignalPacket;
import io.github.c20c01.cc_ms.radio.RadioSignal;
import io.github.c20c01.cc_ms.radio.RadioTransmitter;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.phys.Vec2;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;

import java.util.List;

@EventBusSubscriber(modid = MorSneak.ID)
public class PortableRadioSessionSyncer {
    private static boolean[] buzzLoopingCache = new boolean[0];

    private final RadioTransmitter self;
    private final ServerGamePacketListenerImpl connection;
    private RadioSignal lastSyncedSignal;
    private RadioSignalOffset lastSyncedOffset;
    private boolean needSyncOffset = false;

    protected PortableRadioSessionSyncer(RadioTransmitter self, ServerGamePacketListenerImpl connection) {
        this.self = self;
        this.connection = connection;
        lastSyncedOffset = RadioSignalOffset.ZERO;
    }

    @SubscribeEvent
    public static void modConfig(ModConfigEvent.Loading event) {
        updateBuzzLoopingCache();
    }

    @SubscribeEvent
    public static void modConfig(ModConfigEvent.Reloading event) {
        updateBuzzLoopingCache();
    }

    private static void updateBuzzLoopingCache() {
        List<BuzzConfigEntry> buzzConfigs = MorSneakConfig.getBuzzConfigEntries();
        int size = buzzConfigs.size();
        buzzLoopingCache = new boolean[size];
        for (int i = 0; i < size; i++) {
            buzzLoopingCache[i] = buzzConfigs.get(i).isLooping();
        }
    }

    private static boolean shouldSyncOffset(RadioSignal signal) {
        if (!signal.notEmpty()) return false;
        int buzzIndex = signal.getCode() - 1;
        if (buzzIndex < 0 || buzzIndex >= buzzLoopingCache.length) return false;
        return buzzLoopingCache[buzzIndex];
    }

    protected void sync(RadioSignal signal) {
        if (signal == lastSyncedSignal) {
            if (needSyncOffset) syncOffset(signal, false);
            return;
        }

        lastSyncedSignal = signal;
        needSyncOffset = shouldSyncOffset(signal);
        syncSignal(signal);
    }

    private void syncSignal(RadioSignal signal) {
        syncOffset(signal, true);
        connection.send(new RadioSignalPacket(signal.getCode(), signal.getSeed()));
    }

    private void syncOffset(RadioSignal signal, boolean force) {
        if (!signal.notEmpty()) return;

        RadioTransmitter transmitter = signal.getTransmitter();
        assert transmitter != null;

        RadioSignalOffset signalOffset;
        if (transmitter == self) {
            signalOffset = RadioSignalOffset.ZERO;
        } else {
            // If in different dimensions, keep the last offset to show the transmitter is unreachable.
            if (transmitter.getDimension() != self.getDimension()) return;

            Vec2 transmitterPos = transmitter.getPos();
            Vec2 selfPos = self.getPos();
            Vec2 offset = new Vec2(transmitterPos.x - selfPos.x, transmitterPos.y - selfPos.y);
            signalOffset = new RadioSignalOffset(offset);
        }

        if (signalOffset.equals(lastSyncedOffset) && !force) return;

        lastSyncedOffset = signalOffset;
        connection.send(new RadioSignalOffsetPacket(signalOffset));
    }
}
