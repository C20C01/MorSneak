package io.github.c20c01.morsneak.radio;

import net.minecraft.core.GlobalPos;

import javax.annotation.Nullable;
import java.util.Optional;

public class Radio {
    private final RadioHolder holder;

    @Nullable
    private GlobalPos frequency;

    @Nullable
    protected RadioChannel channel;

    public Radio(RadioHolder holder) {
        this.holder = holder;
    }

    public Optional<GlobalPos> getFrequency() {
        return Optional.ofNullable(frequency);
    }

    public void setFrequency(@Nullable GlobalPos frequency) {
        if (this.frequency != frequency) {
            unregister();
            this.frequency = frequency;
            register();
        }
    }

    public RadioSignal getSignal() {
        return channel == null ? RadioSignal.EMPTY_SIGNAL : channel.getSignal();
    }

    /**
     * @param signal Must have a non-null transmitter!
     */
    public void transmit(RadioSignal signal) {
        if (channel != null) channel.transmit(signal);
    }

    public void register() {
        if (frequency != null) channel = holder.registerRadio(RadioManager.getInstance(), frequency);
    }

    public void unregister() {
        if (frequency != null) {
            holder.unregisterRadio(RadioManager.getInstance(), frequency);
            channel = null;
            frequency = null;
        }
    }
}
