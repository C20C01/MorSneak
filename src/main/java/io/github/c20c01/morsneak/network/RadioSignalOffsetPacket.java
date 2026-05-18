package io.github.c20c01.morsneak.network;

import io.github.c20c01.morsneak.MorSneak;
import io.github.c20c01.morsneak.client.buzzer.RadioBuzzer;
import io.github.c20c01.morsneak.radio.portable.RadioSignalOffset;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@SuppressWarnings("NullableProblems")
public record RadioSignalOffsetPacket(RadioSignalOffset offset) implements CustomPacketPayload {
    public static final Type<RadioSignalOffsetPacket> TYPE = new Type<>(Identifier.fromNamespaceAndPath(MorSneak.ID, "offset"));
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
