package io.github.c20c01.cc_ms.block.entity;

import io.github.c20c01.cc_ms.MorSneak;
import io.github.c20c01.cc_ms.radio.RadioChannel;
import io.github.c20c01.cc_ms.radio.RadioManager;
import io.github.c20c01.cc_ms.radio.RadioSignal;
import io.github.c20c01.cc_ms.radio.RadioTransmitter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;

import javax.annotation.Nullable;

public class RadioTransmitterBlockEntity extends AbstractRadioBlockEntity implements RadioTransmitter {
    public RadioTransmitterBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(MorSneak.RADIO_TRANSMITTER_BLOCK_ENTITY.get(), worldPosition, blockState);
    }

    @Override
    public @Nullable ResourceKey<Level> getDimension() {
        return level == null ? null : level.dimension();
    }

    @Override
    public Vec2 getPos() {
        return new Vec2(worldPosition.getX() + 0.5f, worldPosition.getZ() + 0.5f);
    }

    @Override
    public RadioSignal getSignalToTransmit() {
        return RadioSignal.ofPower(power, this);
    }

    @Override
    public @Nullable RadioChannel registerRadio(RadioManager manager, GlobalPos frequency) {
        if (level == null || level.isClientSide()) return null;
        return manager.registerTransmitter(frequency, this);
    }

    @Override
    public void unregisterRadio(RadioManager manager, GlobalPos frequency) {
        manager.unregisterTransmitter(frequency, this);
    }
}
