package io.github.c20c01.cc_ms.block;

import io.github.c20c01.cc_ms.MorSneak;
import io.github.c20c01.cc_ms.block.entity.AbstractRadioBlockEntity;
import io.github.c20c01.cc_ms.radio.Radio;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

@SuppressWarnings("NullableProblems")
public abstract class AbstractRadioBlock extends Block implements EntityBlock {
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    protected AbstractRadioBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(POWERED, false).setValue(ENABLED, false));
    }

    abstract protected void onPoweredChange(Level level, BlockPos pos, AbstractRadioBlockEntity radio, byte newPower);

    abstract protected byte getNewPower(Level level, BlockPos pos, AbstractRadioBlockEntity radio);

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity by, ItemStack itemStack) {
        if (level.getBlockEntity(pos) instanceof AbstractRadioBlockEntity radio) {
            radio.power = 0;
            if (radio.getRadio().getFrequency().isPresent()) {
                level.setBlock(pos, state.setValue(ENABLED, true), Block.UPDATE_ALL);
                level.scheduleTick(pos, state.getBlock(), 1);
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED, ENABLED);
    }

    @Override
    protected boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof AbstractRadioBlockEntity radio) {
            byte oldPower = radio.power;
            byte newPower = getNewPower(level, pos, radio);
            radio.power = newPower;
            if (oldPower != newPower) {
                boolean powered = newPower > 0;
                if (state.getValue(POWERED) != powered) {
                    level.setBlock(pos, state.setValue(POWERED, powered), Block.UPDATE_ALL);
                }

                onPoweredChange(level, pos, radio, newPower);
            }
        }
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof AbstractRadioBlockEntity radioBlock)) {
            return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
        }

        ItemStack item = player.getItemInHand(hand);
        GlobalPos frequency;
        LodestoneTracker tracker = item.get(DataComponents.LODESTONE_TRACKER);
        if (tracker != null) {
            frequency = tracker.target().orElse(null);
        } else {
            frequency = item.get(MorSneak.SELECTED_FREQUENCY);
        }

        if (frequency == null) {
            return super.useItemOn(itemStack, state, level, pos, player, hand, hitResult);
        }

        Radio radio = radioBlock.getRadio();
        if (frequency.equals(radio.getFrequency().orElse(null))) {
            radio.setFrequency(null);
            level.playSound(null, pos, SoundEvents.GRINDSTONE_USE, SoundSource.BLOCKS);
            level.setBlock(pos, state.setValue(ENABLED, false), Block.UPDATE_ALL);
        } else {
            radio.setFrequency(frequency);
            level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.BLOCKS);
            level.setBlock(pos, state.setValue(ENABLED, true), Block.UPDATE_ALL);
        }

        return InteractionResult.SUCCESS;
    }
}
