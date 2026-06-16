package io.github.c20c01.cc_ms.network;

import io.github.c20c01.cc_ms.MorSneak;
import io.github.c20c01.cc_ms.client.buzzer.RadioBuzzer;
import io.github.c20c01.cc_ms.radio.portable.RadioSignalOffset;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@SuppressWarnings("NullableProblems")
public record RadioSignalOffsetPacket(RadioSignalOffset offset) implements CustomPacketPayload {
    public static final Type<RadioSignalOffsetPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(MorSneak.ID, NetworkRegistration.RADIO_SIGNAL_OFFSET_PATH));
    public static final StreamCodec<FriendlyByteBuf, RadioSignalOffsetPacket> STREAM_CODEC = CustomPacketPayload.codec(RadioSignalOffsetPacket::write, RadioSignalOffsetPacket::new);

    private RadioSignalOffsetPacket(FriendlyByteBuf input) {
        this(new RadioSignalOffset(input.readByte(), input.readByte()));
    }

    private void write(FriendlyByteBuf output) {
        output.writeByte(offset.x());
        output.writeByte(offset.z());
    }

    public static void handle(final RadioSignalOffsetPacket packet, final IPayloadContext context) {
        context.enqueueWork(() -> RadioBuzzer.getInstance().updateOffset(packet.offset));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
