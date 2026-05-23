package io.github.c20c01.cc_ms.datagen;

import io.github.c20c01.cc_ms.MorSneak;
import io.github.c20c01.cc_ms.block.AbstractRadioBlock;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@SuppressWarnings("NullableProblems")
@EventBusSubscriber(modid = MorSneak.ID)
public class MyModelProvider extends ModelProvider {
    public MyModelProvider(PackOutput output) {
        super(output, MorSneak.ID);
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        event.createProvider(MyModelProvider::new);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        generatePortableRadioItem(itemModels, MorSneak.PORTABLE_RADIO.get());
        creatRadioBlock(blockModels, MorSneak.RADIO_RECEIVER_BLOCK.get());
        creatRadioBlock(blockModels, MorSneak.RADIO_TRANSMITTER_BLOCK.get());
    }

    private static void generatePortableRadioItem(ItemModelGenerators itemModels, Item item) {
        ItemModel.Unbaked off = ItemModelUtils.plainModel(itemModels.createFlatItemModel(item, ModelTemplates.FLAT_ITEM));
        ItemModel.Unbaked on = ItemModelUtils.plainModel(itemModels.createFlatItemModel(item, "_on", ModelTemplates.FLAT_ITEM));
        itemModels.generateBooleanDispatch(item, ItemModelUtils.hasComponent(MorSneak.SELECTED_FREQUENCY.get()), on, off);
    }

    private static void creatRadioBlock(BlockModelGenerators blockModels, Block block) {
        final String POWERED = "_powered";
        final String ENABLED = "_enabled";

        MultiVariant base = BlockModelGenerators.plainVariant(TexturedModel.COLUMN.create(block, blockModels.modelOutput));
        MultiVariant powered = BlockModelGenerators.plainVariant(
                TexturedModel.COLUMN
                        .updateTexture(m -> m
                                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side" + POWERED))
                                .put(TextureSlot.END, TextureMapping.getBlockTexture(block, "_top" + POWERED)))
                        .createWithSuffix(block, POWERED, blockModels.modelOutput)
        );
        MultiVariant enabled = BlockModelGenerators.plainVariant(
                TexturedModel.COLUMN
                        .updateTexture(m -> m
                                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side" + ENABLED))
                                .put(TextureSlot.END, TextureMapping.getBlockTexture(block, "_top")))
                        .createWithSuffix(block, ENABLED, blockModels.modelOutput)
        );
        MultiVariant poweredEnabled = BlockModelGenerators.plainVariant(
                TexturedModel.COLUMN
                        .updateTexture(m -> m
                                .put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side" + POWERED + ENABLED))
                                .put(TextureSlot.END, TextureMapping.getBlockTexture(block, "_top" + POWERED)))
                        .createWithSuffix(block, POWERED + ENABLED, blockModels.modelOutput)
        );
        blockModels.blockStateOutput
                .accept(
                        MultiVariantGenerator.dispatch(block)
                                .with(
                                        PropertyDispatch.initial(AbstractRadioBlock.POWERED, AbstractRadioBlock.ENABLED)
                                                .select(false, false, base)
                                                .select(true, false, powered)
                                                .select(false, true, enabled)
                                                .select(true, true, poweredEnabled)
                                )
                );
    }
}
