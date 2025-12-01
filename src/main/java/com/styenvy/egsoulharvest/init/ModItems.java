package com.styenvy.egsoulharvest.init;

import com.styenvy.egsoulharvest.EGSoulHarvest;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(EGSoulHarvest.MODID);

    // Block Items
    public static final DeferredItem<BlockItem> SOUL_RECYCLER = ITEMS.registerSimpleBlockItem("soul_recycler", ModBlocks.SOUL_RECYCLER);

    public static final DeferredItem<BlockItem> CAVE_SPIDER_HARVESTER = ITEMS.registerSimpleBlockItem("cave_spider_harvester", ModBlocks.CAVE_SPIDER_HARVESTER);
    public static final DeferredItem<BlockItem> SPIDER_HARVESTER = ITEMS.registerSimpleBlockItem("spider_harvester", ModBlocks.SPIDER_HARVESTER);
    public static final DeferredItem<BlockItem> ZOMBIE_HARVESTER = ITEMS.registerSimpleBlockItem("zombie_harvester", ModBlocks.ZOMBIE_HARVESTER);
    public static final DeferredItem<BlockItem> SKELETON_HARVESTER = ITEMS.registerSimpleBlockItem("skeleton_harvester", ModBlocks.SKELETON_HARVESTER);
    public static final DeferredItem<BlockItem> BLAZE_HARVESTER = ITEMS.registerSimpleBlockItem("blaze_harvester", ModBlocks.BLAZE_HARVESTER);
    public static final DeferredItem<BlockItem> MAGMA_CUBE_HARVESTER = ITEMS.registerSimpleBlockItem("magma_cube_harvester", ModBlocks.MAGMA_CUBE_HARVESTER);
}
