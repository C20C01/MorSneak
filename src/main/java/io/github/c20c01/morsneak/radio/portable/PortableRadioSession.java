package io.github.c20c01.morsneak.radio.portable;

import io.github.c20c01.morsneak.MorSneak;
import io.github.c20c01.morsneak.item.PortableRadio;
import io.github.c20c01.morsneak.radio.*;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;

import javax.annotation.Nullable;

public class PortableRadioSession implements RadioHolder, RadioTransmitter, RadioReceiver {
    private final Radio radio;
    private final PortableRadioSessionSyncer syncer;
    private final RadioSignal emptySignal;
    protected final ServerPlayer player;

    private RadioSignal defaultSignal;
    private RadioSignal lastTransmitSignal;

    private boolean inventoryTicked = false;

    public PortableRadioSession(ServerPlayer player) {
        this.radio = new Radio(this);
        this.player = player;
        this.syncer = new PortableRadioSessionSyncer(this, player.connection);
        this.emptySignal = RadioSignal.ofEmpty(this);
        this.defaultSignal = emptySignal;
        this.lastTransmitSignal = emptySignal;
    }

    /**
     * Tick every {@link PortableRadio} in the player's inventory, and each player can only have one active radio at a time.
     * If there are multiple radios, the first one will be used and the others will be unset.
     */
    public void inventoryTick(ItemStack radio) {
        GlobalPos frequency = radio.get(MorSneak.SELECTED_FREQUENCY);
        if (frequency == null) return;

        if (inventoryTicked) {
            radio.remove(MorSneak.SELECTED_FREQUENCY);
            return;
        }

        inventoryTicked = true;
        setCode(radio.getOrDefault(MorSneak.SIGNAL_CODE, RadioSignal.CODE_EMPTY));
        this.radio.setFrequency(frequency);
    }

    /**
     * Tick after all player's PortableRadio have been ticked and transmit the signal if the code has changed.
     * If the player doesn't have any {@link PortableRadio} with frequency, the session will be removed.
     *
     * @return {@code true} if the session should be removed.
     */
    public boolean serverPostTick() {
        if (!inventoryTicked) {
            radio.unregister();
            return true;
        }

        inventoryTicked = false;
        RadioSignal signal = getSignal();
        if (signal != this.lastTransmitSignal) {
            radio.transmit(signal.initSeed());
            this.lastTransmitSignal = signal;
            return false;
        }

        syncer.sync(radio.getSignal());
        return false;
    }

    private void setCode(byte code) {
        if (defaultSignal.getCode() != code) defaultSignal = RadioSignal.ofCode(code, this);
    }

    @Override
    public Radio getRadio() {
        return radio;
    }

    @Override
    public @Nullable RadioChannel registerRadio(RadioManager manager, GlobalPos frequency) {
        manager.registerReceiver(frequency, this);
        return manager.registerTransmitter(frequency, this);
    }

    @Override
    public void unregisterRadio(RadioManager manager, GlobalPos frequency) {
        manager.unregisterReceiver(frequency, this);
        manager.unregisterTransmitter(frequency, this);
    }

    @Override
    public void receive(RadioSignal signal) {
        syncer.sync(signal);
    }

    @Override
    public @Nullable ResourceKey<Level> getDimension() {
        return player.level().dimension();
    }

    @Override
    public Vec2 getPos() {
        return new Vec2((float) player.getX(), (float) player.getZ());
    }

    @Override
    public RadioSignal getSignal() {
        return player.isShiftKeyDown() ? defaultSignal : emptySignal;
    }
}
