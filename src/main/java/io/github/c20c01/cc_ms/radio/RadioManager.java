package io.github.c20c01.cc_ms.radio;

import net.minecraft.core.GlobalPos;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Singleton manager that handles the registration of radios with frequencies (The {@link GlobalPos} of the lodestone that the radio is bound to).
 */
public class RadioManager {
    private static RadioManager instance;

    private final Map<GlobalPos, RadioChannel> channels;
    private final List<RadioChannel> emptyChannels;

    private RadioManager() {
        channels = new HashMap<>();
        emptyChannels = new LinkedList<>();
    }

    public static RadioManager getInstance() {
        if (instance == null) instance = new RadioManager();
        return instance;
    }

    public RadioChannel registerReceiver(GlobalPos frequency, RadioReceiver receiver) {
        RadioChannel channel = getOrCreateChannel(frequency);
        channel.addReceiver(receiver);
        return channel;
    }

    public RadioChannel registerTransmitter(GlobalPos frequency, RadioTransmitter transmitter) {
        RadioChannel channel = getOrCreateChannel(frequency);
        channel.addTransmitter(transmitter);
        return channel;
    }

    private RadioChannel getOrCreateChannel(GlobalPos frequency) {
        return channels.computeIfAbsent(frequency, _ -> emptyChannels.isEmpty() ? new RadioChannel() : emptyChannels.removeLast());
    }

    public void unregisterReceiver(GlobalPos frequency, RadioReceiver receiver) {
        RadioChannel channel = channels.get(frequency);
        if (channel == null) return;
        channel.removeReceiver(receiver);
        if (channel.isEmpty()) emptyChannels.add(channels.remove(frequency));
    }

    public void unregisterTransmitter(GlobalPos frequency, RadioTransmitter transmitter) {
        RadioChannel channel = channels.get(frequency);
        if (channel == null) return;
        channel.removeTransmitter(transmitter);
        if (channel.isEmpty()) emptyChannels.add(channels.remove(frequency));
    }
}
