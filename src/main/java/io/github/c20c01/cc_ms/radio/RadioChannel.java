package io.github.c20c01.cc_ms.radio;

import java.util.*;

public class RadioChannel {
    private final Set<RadioReceiver> receivers;
    private final Set<RadioTransmitter> transmitters;
    private final Deque<RadioSignal> signalDeque;
    private final Map<RadioTransmitter, RadioSignal> signalMap;

    public RadioChannel() {
        receivers = new HashSet<>();
        transmitters = new HashSet<>();
        signalDeque = new ArrayDeque<>();
        signalMap = new HashMap<>();
    }

    protected void addReceiver(RadioReceiver receiver) {
        if (!receivers.add(receiver)) return;
        receiver.receive(getSignalToReceive());
    }

    protected void removeReceiver(RadioReceiver receiver) {
        if (!receivers.remove(receiver)) return;
        receiver.receive(RadioSignal.EMPTY_SIGNAL);
    }

    protected void addTransmitter(RadioTransmitter transmitter) {
        if (!transmitters.add(transmitter)) return;
        transmit(transmitter.getSignalToTransmit());
    }

    protected void removeTransmitter(RadioTransmitter transmitter) {
        if (!transmitters.remove(transmitter)) return;
        transmit(RadioSignal.ofEmpty(transmitter));
    }

    public boolean isEmpty() {
        return receivers.isEmpty() && transmitters.isEmpty();
    }

    public RadioSignal getSignalToReceive() {
        return signalDeque.isEmpty() ? RadioSignal.EMPTY_SIGNAL : signalDeque.peek();
    }

    /**
     * @param signal Must have a non-null transmitter!
     */
    public void transmit(RadioSignal signal) {
        updateQueue(signal);
        transmit();
    }

    private void updateQueue(RadioSignal signal) {
        RadioTransmitter transmitter = signal.getTransmitter();
        RadioSignal oldSignal = signalMap.remove(transmitter);

        if (oldSignal != null) {
            signalDeque.remove(oldSignal);
        }

        if (signal.notEmpty()) {
            signalDeque.push(signal);
            signalMap.put(transmitter, signal);
        }
    }

    private void transmit() {
        if (receivers.isEmpty()) return;
        RadioSignal signalToReceive = getSignalToReceive();
        for (RadioReceiver receiver : receivers) receiver.receive(signalToReceive);
    }
}
