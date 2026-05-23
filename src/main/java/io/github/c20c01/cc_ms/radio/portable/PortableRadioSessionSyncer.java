package io.github.c20c01.cc_ms.radio.portable;

import io.github.c20c01.cc_ms.network.RadioSignalOffsetPacket;
import io.github.c20c01.cc_ms.network.RadioSignalPacket;
import io.github.c20c01.cc_ms.radio.RadioSignal;
import io.github.c20c01.cc_ms.radio.RadioTransmitter;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.phys.Vec2;

public class PortableRadioSessionSyncer {
    private final RadioTransmitter self;
    private final ServerGamePacketListenerImpl connection;
    private RadioSignal lastSyncedSignal;
    private RadioSignalOffset lastSyncedOffset;

    protected PortableRadioSessionSyncer(RadioTransmitter self, ServerGamePacketListenerImpl connection) {
        this.self = self;
        this.connection = connection;
        lastSyncedOffset = RadioSignalOffset.ZERO;
    }

    protected void sync(RadioSignal signal) {
        if (signal == lastSyncedSignal) {
            if (signal.notEmpty()) syncOffset(signal, false);
            return;
        }

        lastSyncedSignal = signal;
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
