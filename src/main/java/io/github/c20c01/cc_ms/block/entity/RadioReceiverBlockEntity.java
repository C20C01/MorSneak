package io.github.c20c01.cc_ms.block.entity;

import io.github.c20c01.cc_ms.MorSneak;
import io.github.c20c01.cc_ms.radio.RadioChannel;
import io.github.c20c01.cc_ms.radio.RadioManager;
import io.github.c20c01.cc_ms.radio.RadioReceiver;
import io.github.c20c01.cc_ms.radio.RadioSignal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.TickPriority;

import javax.annotation.Nullable;

public class RadioReceiverBlockEntity extends AbstractRadioBlockEntity implements RadioReceiver {
    public RadioReceiverBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(MorSneak.RADIO_RECEIVER_BLOCK_ENTITY.get(), worldPosition, blockState);
    }

    @Override
    public void receive(RadioSignal signal) {
        if (level == null) return;
        if (level.getBlockTicks().willTickThisTick(worldPosition, MorSneak.RADIO_RECEIVER_BLOCK.get())) return;

        if (power != signal.getPower()){
            level.scheduleTick(worldPosition, MorSneak.RADIO_RECEIVER_BLOCK.get(), 2, TickPriority.EXTREMELY_HIGH);
        }
    }

    @Override
    public @Nullable RadioChannel registerRadio(RadioManager manager, GlobalPos frequency) {
        return hasLevel() ? manager.registerReceiver(frequency, this) : null;
    }

    @Override
    public void unregisterRadio(RadioManager manager, GlobalPos frequency) {
        manager.unregisterReceiver(frequency, this);
    }
}
