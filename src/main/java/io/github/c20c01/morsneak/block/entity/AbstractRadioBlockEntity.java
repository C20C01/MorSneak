package io.github.c20c01.morsneak.block.entity;

import io.github.c20c01.morsneak.radio.Radio;
import io.github.c20c01.morsneak.radio.RadioHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;

public abstract class AbstractRadioBlockEntity extends BlockEntity implements RadioHolder {
    private final Radio radio;
    public byte power = 0;

    public AbstractRadioBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
        this.radio = new Radio(this);
    }

    @Override
    public Radio getRadio() {
        return radio;
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        input.read("frequency", GlobalPos.CODEC).ifPresent(radio::setFrequency);
        this.power = input.getByteOr("power", (byte) 0);
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        super.saveAdditional(output);
        radio.getFrequency().ifPresent(frequency -> output.store("frequency", GlobalPos.CODEC, frequency));
        output.putByte("power", this.power);
    }

    @Override
    public void onLoad() {
        radio.register();
        super.onLoad();
    }

    @Override
    public void setRemoved() {
        radio.unregister();
        super.setRemoved();
    }

    /**
     * You may find the radio is not controlled by the redstone signal when you are far away from the block.
     * I guess this is because the redstone will stop working before the chunk is unloaded.
     * It may be annoying, but I'd like to keep this as a feature rather than a bug.
     */
    @Override
    public void onChunkUnloaded() {
        radio.unregister();
        super.onChunkUnloaded();
    }
}
