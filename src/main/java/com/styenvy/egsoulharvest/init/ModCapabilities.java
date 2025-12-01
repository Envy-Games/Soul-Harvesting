package com.styenvy.egsoulharvest.init;

import com.styenvy.egsoulharvest.EGSoulHarvest;
import com.styenvy.egsoulharvest.blockentity.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

@EventBusSubscriber(modid = EGSoulHarvest.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModCapabilities {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Register energy capability for Soul Recycler
        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ModBlockEntities.SOUL_RECYCLER.get(),
                SoulRecyclerBlockEntity::getEnergyStorage
        );

        // Register item handler capabilities for all harvesters
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.CAVE_SPIDER_HARVESTER.get(),
                BaseSoulHarvesterBlockEntity::getItemHandler
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.SPIDER_HARVESTER.get(),
                BaseSoulHarvesterBlockEntity::getItemHandler
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.ZOMBIE_HARVESTER.get(),
                BaseSoulHarvesterBlockEntity::getItemHandler
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.SKELETON_HARVESTER.get(),
                BaseSoulHarvesterBlockEntity::getItemHandler
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.BLAZE_HARVESTER.get(),
                BaseSoulHarvesterBlockEntity::getItemHandler
        );

        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ModBlockEntities.MAGMA_CUBE_HARVESTER.get(),
                BaseSoulHarvesterBlockEntity::getItemHandler
        );
    }
}
