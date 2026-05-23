package io.github.c20c01.cc_ms;

import com.mojang.serialization.Codec;
import io.github.c20c01.cc_ms.block.RadioReceiverBlock;
import io.github.c20c01.cc_ms.block.RadioTransmitterBlock;
import io.github.c20c01.cc_ms.block.entity.RadioReceiverBlockEntity;
import io.github.c20c01.cc_ms.block.entity.RadioTransmitterBlockEntity;
import io.github.c20c01.cc_ms.config.MorSneakConfig;
import io.github.c20c01.cc_ms.item.PortableRadio;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Optional;

@Mod(MorSneak.ID)
public class MorSneak {
    public static final String ID = "cc_ms";

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ID);
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, ID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ID);
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, ID);
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, ID);

    public static final DeferredBlock<Block> RADIO_RECEIVER_BLOCK = BLOCKS.registerBlock("radio_receiver_block", RadioReceiverBlock::new, p -> p.mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RadioReceiverBlockEntity>> RADIO_RECEIVER_BLOCK_ENTITY = BLOCK_ENTITIES.register("radio_receiver_block_entity", () -> new BlockEntityType<>(RadioReceiverBlockEntity::new, RADIO_RECEIVER_BLOCK.get()));
    public static final DeferredItem<BlockItem> RADIO_RECEIVER_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("radio_receiver_block", RADIO_RECEIVER_BLOCK);

    public static final DeferredBlock<Block> RADIO_TRANSMITTER_BLOCK = BLOCKS.registerBlock("radio_transmitter_block", RadioTransmitterBlock::new, p -> p.mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.0F));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<RadioTransmitterBlockEntity>> RADIO_TRANSMITTER_BLOCK_ENTITY = BLOCK_ENTITIES.register("radio_transmitter_block_entity", () -> new BlockEntityType<>(RadioTransmitterBlockEntity::new, RADIO_TRANSMITTER_BLOCK.get()));
    public static final DeferredItem<BlockItem> RADIO_TRANSMITTER_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("radio_transmitter_block", RADIO_TRANSMITTER_BLOCK);

    public static final DeferredItem<Item> PORTABLE_RADIO = ITEMS.registerItem("portable_radio", PortableRadio::new, p -> p.stacksTo(1));

    public static final Holder<SoundEvent> BEEP_SOUND = SOUND_EVENTS.register("beep", SoundEvent::createVariableRangeEvent);
    public static final Holder<SoundEvent> FA_SOUND = SOUND_EVENTS.register("fa", SoundEvent::createVariableRangeEvent);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<GlobalPos>> SELECTED_FREQUENCY = DATA_COMPONENTS.registerComponentType("selected_frequency", b -> b.persistent(GlobalPos.CODEC).networkSynchronized(GlobalPos.STREAM_CODEC));
    /**
     * Copy to ArrayList before modify the list to avoid {@link UnsupportedOperationException}.
     */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<GlobalPos>>> FREQUENCIES = DATA_COMPONENTS.registerComponentType("frequencies", b -> b.persistent(GlobalPos.CODEC.listOf()).networkSynchronized(GlobalPos.STREAM_CODEC.apply(ByteBufCodecs.list())));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Byte>> SIGNAL_CODE = DATA_COMPONENTS.registerComponentType("signal_code", b -> b.persistent(Codec.BYTE).networkSynchronized(ByteBufCodecs.BYTE));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Byte>> SOUND_CODE = DATA_COMPONENTS.registerComponentType("sound_code", b -> b.persistent(Codec.BYTE).networkSynchronized(ByteBufCodecs.BYTE));

    public static final String TEXT_KEY_TAB_TITLE = "tab_title";

    public static final String TEXT_KEY_PORTABLE_RADIO_FULL = "portable_radio.full";

    public static final String TEXT_KEY_PORTABLE_RADIO_TITLE = "portable_radio.title";
    public static final String TEXT_KEY_PORTABLE_RADIO_SELECT_MODE = "portable_radio.select_mode";
    public static final String TEXT_KEY_PORTABLE_RADIO_DELETE_MODE = "portable_radio.delete_mode";
    public static final String TEXT_KEY_PORTABLE_RADIO_SOUND_MODE = "portable_radio.sound_mode";
    public static final String TEXT_KEY_PORTABLE_RADIO_MIC = "portable_radio.mic";
    public static final String TEXT_KEY_PORTABLE_RADIO_SELECT = "portable_radio.select";
    public static final String TEXT_KEY_PORTABLE_RADIO_UNSELECT = "portable_radio.unselect";
    public static final String TEXT_KEY_PORTABLE_RADIO_DELETE = "portable_radio.delete";
    public static final String TEXT_KEY_PORTABLE_RADIO_OTHER_OPEN = "portable_radio.other_open";

    static {
        CREATIVE_MODE_TABS.register("tab", () -> CreativeModeTab.builder()
                .title(Component.translatable(TEXT_KEY_TAB_TITLE))
                .withTabsBefore(CreativeModeTabs.COMBAT)
                .icon(() -> PORTABLE_RADIO.get().getDefaultInstance())
                .displayItems((_, output) -> {
                    output.accept(PORTABLE_RADIO.get());
                    output.accept(RADIO_RECEIVER_BLOCK_ITEM.get());
                    output.accept(RADIO_TRANSMITTER_BLOCK_ITEM.get());
                    output.accept(Items.LODESTONE);
                    output.accept(Items.COMPASS);
                    ItemStack testCompass = new ItemStack(Items.COMPASS);
                    testCompass.set(DataComponents.CUSTOM_NAME, Component.literal("[0, 0, 0]").withStyle(ChatFormatting.GOLD));
                    testCompass.set(DataComponents.LODESTONE_TRACKER, new LodestoneTracker(Optional.of(GlobalPos.of(Level.OVERWORLD, BlockPos.ZERO)), false));
                    output.accept(testCompass);
                }).build());
    }

    public MorSneak(IEventBus modEventBus, ModContainer modContainer) {
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);
        DATA_COMPONENTS.register(modEventBus);
        SOUND_EVENTS.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.SERVER, MorSneakConfig.SPEC);
    }
}
