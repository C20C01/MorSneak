package io.github.c20c01.cc_ms.block;

import io.github.c20c01.cc_ms.block.entity.AbstractRadioBlockEntity;
import io.github.c20c01.cc_ms.block.entity.RadioReceiverBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

@SuppressWarnings("NullableProblems")
public class RadioReceiverBlock extends AbstractRadioBlock {
    public RadioReceiverBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new RadioReceiverBlockEntity(worldPosition, blockState);
    }

    @Override
    protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        if (!state.getValue(POWERED)) {
            return 0;
        } else {
            return level.getBlockEntity(pos) instanceof AbstractRadioBlockEntity radio ? radio.power : 0;
        }
    }

    @Override
    protected void onPoweredChange(Level level, BlockPos pos, AbstractRadioBlockEntity radio, byte newPower) {
        level.updateNeighborsAt(pos, this);
    }

    @Override
    protected byte getNewPower(Level level, BlockPos pos, AbstractRadioBlockEntity radio) {
        return radio.getRadio().getSignal().getPower();
    }
}
