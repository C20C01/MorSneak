package io.github.c20c01.cc_ms.client.gui;

import net.minecraft.network.chat.Component;

/**
 * It will not automatically toggle when clicked, just show the current state.
 */
public class SwitchButton extends NoFocusButton {
    private final Component offLabel;
    private final Component onLabel;

    public SwitchButton(int x, int y, int width, int height, Component offLabel, Component onLabel, OnPress onPress) {
        super(x, y, width, height, offLabel, onPress);
        this.offLabel = offLabel;
        this.onLabel = onLabel;
    }

    public void setOn(boolean on) {
        setMessage(on ? onLabel : offLabel);
    }
}
