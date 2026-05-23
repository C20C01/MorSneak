package io.github.c20c01.cc_ms.radio;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;

import javax.annotation.Nullable;

public interface RadioTransmitter {
    @Nullable
    ResourceKey<Level> getDimension();

    Vec2 getPos();

    /**
     * For getting the signal that going to be transmitted when the transmitter is just registered.
     *
     * @return The signal to be transmitted, must have a non-null transmitter!
     */
    RadioSignal getSignal();
}
