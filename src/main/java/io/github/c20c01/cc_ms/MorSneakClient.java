package io.github.c20c01.cc_ms;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = MorSneak.ID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = MorSneak.ID, value = Dist.CLIENT)
public class MorSneakClient {
    public MorSneakClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }
}
