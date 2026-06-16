package io.github.c20c01.cc_ms.network;

import io.github.c20c01.cc_ms.MorSneak;
import io.github.c20c01.cc_ms.client.buzzer.RadioBuzzer;
import io.github.c20c01.cc_ms.radio.RadioSignal;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@SuppressWarnings("NullableProblems")
public record RadioSignalPacket(byte code, long seed) implements CustomPacketPayload {
    public static final Type<RadioSignalPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(MorSneak.ID, NetworkRegistration.RADIO_SIGNAL_PATH));
    public static final StreamCodec<FriendlyByteBuf, RadioSignalPacket> STREAM_CODEC = CustomPacketPayload.codec(RadioSignalPacket::write, RadioSignalPacket::new);

    private RadioSignalPacket(FriendlyByteBuf input) {
        byte code = input.readByte();
        long seed = code == RadioSignal.CODE_EMPTY ? 0 : input.readLong();
        this(code, seed);
    }

    private void write(FriendlyByteBuf output) {
        output.writeByte(code);
        if (code != RadioSignal.CODE_EMPTY) output.writeLong(seed);
    }

    public static void handle(final RadioSignalPacket packet, final IPayloadContext context) {
        context.enqueueWork(() -> RadioBuzzer.getInstance().updateSignal(packet.code, packet.seed));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
