package io.github.c20c01.cc_ms.radio;

import net.minecraft.core.GlobalPos;

import javax.annotation.Nullable;

public interface RadioHolder {
    Radio getRadio();

    /**
     * Should only register a radio when the level is not null and is server side.
     */
    @Nullable
    RadioChannel registerRadio(RadioManager manager, GlobalPos frequency);

    void unregisterRadio(RadioManager manager, GlobalPos frequency);
}
