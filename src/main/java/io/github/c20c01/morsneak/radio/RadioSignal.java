package io.github.c20c01.morsneak.radio;

import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec2;

import javax.annotation.Nullable;

public class RadioSignal {
    private static final RandomSource random = RandomSource.create();

    public static final byte CODE_EMPTY = 0;

    /**
     * Be careful, Null transmitter!
     */
    public static final RadioSignal EMPTY_SIGNAL = RadioSignal.ofEmpty(null);

    @Nullable
    private final RadioTransmitter transmitter;
    private final byte code;
    private long seed;

    private RadioSignal(byte code, @Nullable RadioTransmitter transmitter) {
        this.code = code;
        this.transmitter = transmitter;
    }

    public static RadioSignal ofEmpty(@Nullable RadioTransmitter transmitter) {
        return new RadioSignal(CODE_EMPTY, transmitter);
    }

    public static RadioSignal ofPower(byte power, @Nullable RadioTransmitter transmitter) {
        return new RadioSignal((byte) Math.max(0, Math.min(power, 15)), transmitter);
    }

    public static RadioSignal ofCode(byte code, @Nullable RadioTransmitter transmitter) {
        return new RadioSignal(code, transmitter);
    }

    public RadioSignal initSeed() {
        if (code != CODE_EMPTY) seed = random.nextLong();
        return this;
    }

    public boolean notEmpty() {
        return code != CODE_EMPTY;
    }

    public byte getCode() {
        return code;
    }

    public byte getPower() {
        return (byte) Math.max(0, Math.min(code, 15));
    }

    @Nullable
    public RadioTransmitter getTransmitter() {
        return transmitter;
    }

    public long getSeed() {
        return seed;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RadioSignal{").append("Code: ").append(code).append(", Transmitter: ");
        if (transmitter == null) {
            builder.append("null");
        } else {
            Vec2 pos = transmitter.getPos();
            builder.append(transmitter.getClass().getSimpleName()).append(" at (").append(pos.x).append(", ").append(pos.y).append(")");
        }
        builder.append('}');
        return builder.toString();
    }
}
