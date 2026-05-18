package io.github.c20c01.morsneak.block;

import io.github.c20c01.morsneak.MorSneak;
import io.github.c20c01.morsneak.block.entity.AbstractRadioBlockEntity;
import io.github.c20c01.morsneak.block.entity.RadioTransmitterBlockEntity;
import io.github.c20c01.morsneak.radio.RadioSignal;
import io.github.c20c01.morsneak.radio.RadioTransmitter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.ticks.TickPriority;

import javax.annotation.Nullable;

@SuppressWarnings("NullableProblems")
public class RadioTransmitterBlock extends AbstractRadioBlock {
    public RadioTransmitterBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
        return new RadioTransmitterBlockEntity(worldPosition, blockState);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston) {
        if (level.isClientSide()) return;
        if (level.getBlockTicks().willTickThisTick(pos, block)) return;
        if (!(level.getBlockEntity(pos) instanceof AbstractRadioBlockEntity radio)) return;

        if (radio.power != level.getBestNeighborSignal(pos)) {
            level.scheduleTick(pos, MorSneak.RADIO_TRANSMITTER_BLOCK.get(), 2, TickPriority.EXTREMELY_HIGH);
        }
    }

    @Override
    protected void onPoweredChange(Level level, BlockPos pos, AbstractRadioBlockEntity radio, byte newPower) {
        radio.getRadio().transmit(RadioSignal.ofPower(newPower, (RadioTransmitter) radio).initSeed());
    }

    @Override
    protected byte getNewPower(Level level, BlockPos pos, AbstractRadioBlockEntity radio) {
        return (byte) level.getBestNeighborSignal(pos);
    }
}
