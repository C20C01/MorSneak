package io.github.c20c01.morsneak.client.gui;

import net.minecraft.network.chat.Component;

public class ArrowButton extends NoFocusButton {
    private static final Component LEFT_LABEL = Component.literal("◀");
    private static final Component RIGHT_LABEL = Component.literal("▶");

    public ArrowButton(int x, int y, int width, int height, OnPress onPress, boolean isLeft) {
        super(x, y, width, height, isLeft ? LEFT_LABEL : RIGHT_LABEL, onPress);
    }
}
