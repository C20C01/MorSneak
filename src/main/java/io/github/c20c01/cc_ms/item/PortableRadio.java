package io.github.c20c01.cc_ms.item;

import io.github.c20c01.cc_ms.MorSneak;
import io.github.c20c01.cc_ms.client.gui.PortableRadioScreen;
import io.github.c20c01.cc_ms.radio.portable.PortableRadioSessionManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("NullableProblems")
public class PortableRadio extends Item {
    public static final byte MAX_FREQUENCIES = 16;

    private static final Component FULL_MESSAGE = Component.translatable(MorSneak.TEXT_KEY_PORTABLE_RADIO_FULL);

    public PortableRadio(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            ItemStack radio = player.getItemInHand(hand);
            byte slot = hand == InteractionHand.MAIN_HAND ? (byte) player.getInventory().getSelectedSlot() : Inventory.SLOT_OFFHAND;
            PortableRadioScreen.open(player, radio, slot);
        }
        return super.use(level, player, hand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) return super.useOn(context);

        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        if (!level.getBlockState(blockPos).is(Blocks.LODESTONE)) return super.useOn(context);

        if (addFrequency(context.getItemInHand(), GlobalPos.of(level.dimension(), blockPos), player)) {
            level.playSound(null, blockPos, SoundEvents.LODESTONE_COMPASS_LOCK, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack self, ItemStack other, Slot slot, ClickAction clickAction, Player player, SlotAccess carriedItem) {
        LodestoneTracker tracker = other.get(DataComponents.LODESTONE_TRACKER);
        if (tracker == null || tracker.target().isEmpty()) {
            return super.overrideOtherStackedOnMe(self, other, slot, clickAction, player, carriedItem);
        }

        if (clickAction != ClickAction.SECONDARY) {
            return super.overrideOtherStackedOnMe(self, other, slot, clickAction, player, carriedItem);
        }

        if (addFrequency(self, tracker.target().get(), player)) {
            player.playSound(SoundEvents.LODESTONE_COMPASS_LOCK);
        }

        return true;
    }

    private static boolean addFrequency(ItemStack radio, GlobalPos frequency, Player player) {
        List<GlobalPos> frequencies = radio.getOrDefault(MorSneak.FREQUENCIES, List.of());
        if (frequencies.contains(frequency)) {
            radio.set(MorSneak.SELECTED_FREQUENCY, frequency);
            return true;
        }

        if (frequencies.size() >= MAX_FREQUENCIES) {
            if (player.isLocalPlayer()) {
                player.sendOverlayMessage(FULL_MESSAGE);
                player.playSound(SoundEvents.VILLAGER_NO);
            }
            return false;
        }

        ArrayList<GlobalPos> newFrequencies = new ArrayList<>(frequencies);
        newFrequencies.add(frequency);
        radio.set(MorSneak.FREQUENCIES, newFrequencies);
        return true;
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        if (owner instanceof ServerPlayer player) {
            PortableRadioSessionManager.getInstance().inventoryTick(player, itemStack);
        }
    }

    public static void removeFrequency(ItemStack radio, byte index) {
        List<GlobalPos> frequencies = radio.get(MorSneak.FREQUENCIES);
        if (frequencies == null || index < 0 || index >= frequencies.size()) {
            radio.set(MorSneak.SELECTED_FREQUENCY, null);
            return;
        }

        ArrayList<GlobalPos> newFrequencies = new ArrayList<>(frequencies);
        newFrequencies.remove(index);
        radio.set(MorSneak.FREQUENCIES, newFrequencies);
    }

    /**
     * @param index the index of the frequency to select, or -1 to deselect the current frequency
     */
    public static void selectFrequency(ItemStack radio, byte index) {
        List<GlobalPos> frequencies = radio.get(MorSneak.FREQUENCIES);
        if (frequencies == null || index < 0 || index >= frequencies.size()) {
            radio.set(MorSneak.SELECTED_FREQUENCY, null);
            return;
        }

        radio.set(MorSneak.SELECTED_FREQUENCY, frequencies.get(index));
    }

    public static void updateSignalCode(ItemStack radio, byte signalCode) {
        radio.set(MorSneak.SIGNAL_CODE, signalCode);
    }

    public static void updateSoundCode(ItemStack radio, byte soundCode) {
        radio.set(MorSneak.SOUND_CODE, soundCode);
    }
}
