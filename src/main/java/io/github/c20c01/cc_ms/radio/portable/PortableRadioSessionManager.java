package io.github.c20c01.cc_ms.radio.portable;

import io.github.c20c01.cc_ms.MorSneak;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton manager that handles the sessions of portable radios.
 * <p>
 * {@link #inventoryTick} will update the frequency and code.
 * <p>
 * {@link #serverPostTick} will remove the unused sessions and transmit the signal if the code has changed.
 */
@EventBusSubscriber(modid = MorSneak.ID)
public class PortableRadioSessionManager {
    private static PortableRadioSessionManager instance;

    private final Map<ServerPlayer, PortableRadioSession> sessions;

    private PortableRadioSessionManager() {
        this.sessions = new HashMap<>();
    }

    public static PortableRadioSessionManager getInstance() {
        if (instance == null) instance = new PortableRadioSessionManager();
        return instance;
    }

    public void inventoryTick(ServerPlayer player, ItemStack radio) {
        sessions.computeIfAbsent(player, PortableRadioSession::new).inventoryTick(radio);
    }

    public void serverPostTick() {
        sessions.entrySet().removeIf(entry -> entry.getValue().serverPostTick());
    }

    @SubscribeEvent
    public static void serverPostTick(ServerTickEvent.Post event) {
        PortableRadioSessionManager.getInstance().serverPostTick();
    }
}
