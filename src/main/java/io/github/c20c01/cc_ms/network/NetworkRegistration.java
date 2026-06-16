package io.github.c20c01.cc_ms.network;

import io.github.c20c01.cc_ms.MorSneak;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = MorSneak.ID)
public class NetworkRegistration {
    protected static final String RADIO_SIGNAL_PATH = "s";
    protected static final String RADIO_SIGNAL_OFFSET_PATH = "o";
    protected static final String PORTABLE_RADIO_UPDATE_PATH = "u";

    @SubscribeEvent
    public static void registerPayload(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToClient(RadioSignalPacket.TYPE, RadioSignalPacket.STREAM_CODEC, RadioSignalPacket::handle);
        registrar.playToClient(RadioSignalOffsetPacket.TYPE, RadioSignalOffsetPacket.STREAM_CODEC, RadioSignalOffsetPacket::handle);
        registrar.playToServer(PortableRadioUpdatePacket.TYPE, PortableRadioUpdatePacket.STREAM_CODEC, PortableRadioUpdatePacket::handle);
    }
}
