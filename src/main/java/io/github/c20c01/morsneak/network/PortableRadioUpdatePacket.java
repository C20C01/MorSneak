package io.github.c20c01.morsneak.network;

import io.github.c20c01.morsneak.MorSneak;
import io.github.c20c01.morsneak.item.PortableRadio;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

@SuppressWarnings("NullableProblems")
public record PortableRadioUpdatePacket(byte slot, byte operation, byte data) implements CustomPacketPayload {
    public static final byte UPDATE_SIGNAL_CODE = 0;
    public static final byte UPDATE_SOUND_CODE = 1;
    public static final byte REMOVE_FREQUENCY = 2;
    public static final byte SELECT_FREQUENCY = 3;

    public static final CustomPacketPayload.Type<PortableRadioUpdatePacket> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(MorSneak.ID, "portable_radio_update"));
    public static final StreamCodec<FriendlyByteBuf, PortableRadioUpdatePacket> STREAM_CODEC = CustomPacketPayload.codec(PortableRadioUpdatePacket::write, PortableRadioUpdatePacket::new);

    private PortableRadioUpdatePacket(FriendlyByteBuf input) {
        this(input.readByte(), input.readByte(), input.readByte());
    }

    private void write(FriendlyByteBuf output) {
        output.writeByte(slot);
        output.writeByte(operation);
        output.writeByte(data);
    }

    public static void handle(final PortableRadioUpdatePacket packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            int slot = packet.slot();
            if (!Inventory.isHotbarSlot(slot) && slot != Inventory.SLOT_OFFHAND) return;

            ItemStack radio = context.player().getInventory().getItem(slot);
            if (!radio.is(MorSneak.PORTABLE_RADIO.get())) return;

            switch (packet.operation()) {
                case UPDATE_SIGNAL_CODE -> PortableRadio.updateSignalCode(radio, packet.data());
                case UPDATE_SOUND_CODE -> PortableRadio.updateSoundCode(radio, packet.data());
                case REMOVE_FREQUENCY -> PortableRadio.removeFrequency(radio, packet.data());
                case SELECT_FREQUENCY -> PortableRadio.selectFrequency(radio, packet.data());
            }
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
