package io.github.c20c01.cc_ms.radio;

import net.minecraft.core.GlobalPos;

import javax.annotation.Nullable;

public interface RadioHolder {
    Radio getRadio();

    /**
     * Should check {@code level != null} before calling this method.
     * Sometimes the level may not be loaded when the radio is registered,
     * that will cause the receiver receives code but can't act on it.
     */
    @Nullable
    RadioChannel registerRadio(RadioManager manager, GlobalPos frequency);

    void unregisterRadio(RadioManager manager, GlobalPos frequency);
}
