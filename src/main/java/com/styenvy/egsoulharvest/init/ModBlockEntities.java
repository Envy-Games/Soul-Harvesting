package com.styenvy.egsoulharvest.init;

import com.styenvy.egsoulharvest.EGSoulHarvest;
import com.styenvy.egsoulharvest.blockentity.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = 
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, EGSoulHarvest.MODID);

    public static final Supplier<BlockEntityType<SoulRecyclerBlockEntity>> SOUL_RECYCLER = BLOCK_ENTITIES.register("soul_recycler",
            () -> BlockEntityType.Builder.of(SoulRecyclerBlockEntity::new, ModBlocks.SOUL_RECYCLER.get()).build(null));

    public static final Supplier<BlockEntityType<CaveSpiderHarvesterBlockEntity>> CAVE_SPIDER_HARVESTER = BLOCK_ENTITIES.register("cave_spider_harvester",
            () -> BlockEntityType.Builder.of(CaveSpiderHarvesterBlockEntity::new, ModBlocks.CAVE_SPIDER_HARVESTER.get()).build(null));

    public static final Supplier<BlockEntityType<SpiderHarvesterBlockEntity>> SPIDER_HARVESTER = BLOCK_ENTITIES.register("spider_harvester",
            () -> BlockEntityType.Builder.of(SpiderHarvesterBlockEntity::new, ModBlocks.SPIDER_HARVESTER.get()).build(null));

    public static final Supplier<BlockEntityType<ZombieHarvesterBlockEntity>> ZOMBIE_HARVESTER = BLOCK_ENTITIES.register("zombie_harvester",
            () -> BlockEntityType.Builder.of(ZombieHarvesterBlockEntity::new, ModBlocks.ZOMBIE_HARVESTER.get()).build(null));

    public static final Supplier<BlockEntityType<SkeletonHarvesterBlockEntity>> SKELETON_HARVESTER = BLOCK_ENTITIES.register("skeleton_harvester",
            () -> BlockEntityType.Builder.of(SkeletonHarvesterBlockEntity::new, ModBlocks.SKELETON_HARVESTER.get()).build(null));

    public static final Supplier<BlockEntityType<BlazeHarvesterBlockEntity>> BLAZE_HARVESTER = BLOCK_ENTITIES.register("blaze_harvester",
            () -> BlockEntityType.Builder.of(BlazeHarvesterBlockEntity::new, ModBlocks.BLAZE_HARVESTER.get()).build(null));

    public static final Supplier<BlockEntityType<MagmaCubeHarvesterBlockEntity>> MAGMA_CUBE_HARVESTER = BLOCK_ENTITIES.register("magma_cube_harvester",
            () -> BlockEntityType.Builder.of(MagmaCubeHarvesterBlockEntity::new, ModBlocks.MAGMA_CUBE_HARVESTER.get()).build(null));
}
