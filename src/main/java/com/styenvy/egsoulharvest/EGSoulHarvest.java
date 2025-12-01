package com.styenvy.egsoulharvest;

import com.styenvy.egsoulharvest.config.ModConfig;
import com.styenvy.egsoulharvest.init.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(EGSoulHarvest.MODID)
public class EGSoulHarvest {
    public static final String MODID = "egsoulharvest";
    public static final Logger LOGGER = LoggerFactory.getLogger(EGSoulHarvest.class);

    public EGSoulHarvest(IEventBus modEventBus, ModContainer modContainer) {
        // Register deferred registers
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModMenuTypes.MENU_TYPES.register(modEventBus);
        ModCreativeTabs.CREATIVE_TABS.register(modEventBus);

        // Register config
        modContainer.registerConfig(Type.COMMON, ModConfig.SPEC);

        LOGGER.info("EG Soul Harvester mod initialized!");
    }
}
