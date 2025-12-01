package com.styenvy.egsoulharvest.client;

import com.styenvy.egsoulharvest.EGSoulHarvest;
import com.styenvy.egsoulharvest.init.ModMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = EGSoulHarvest.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.HARVESTER_MENU.get(), HarvesterScreen::new);
    }
}
