package io.github.c20c01.cc_ms.radio.portable;

import net.minecraft.world.phys.Vec2;

/**
 * The offset that will add to the sound position, ignoring the Y axis (height).
 *
 * @param x X (X in Vec2)
 * @param z Z (Y in Vec2)
 */
public record RadioSignalOffset(byte x, byte z) {
    public static final int MAX_OFFSET = 8;
    public static final float BYTE_SCALE = (float) (MAX_OFFSET / 127.0);

    public static final RadioSignalOffset ZERO = new RadioSignalOffset((byte) 0, (byte) 0);

    public RadioSignalOffset(Vec2 offset) {
        Vec2 clamped = clampOffset(offset);
        this(mapToByte(clamped.x), mapToByte(clamped.y));
    }

    public Vec2 toVec2() {
        return new Vec2(mapToFloat(x), mapToFloat(z));
    }

    public boolean equals(RadioSignalOffset other) {
        return this.x == other.x && this.z == other.z;
    }

    private static Vec2 clampOffset(Vec2 offset) {
        double distance = offset.length();
        return distance <= MAX_OFFSET ? offset : offset.normalized().scale(MAX_OFFSET);
    }

    private static byte mapToByte(double value) {
        return (byte) (value / BYTE_SCALE);
    }

    private static float mapToFloat(byte value) {
        return value * BYTE_SCALE;
    }
}
