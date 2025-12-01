package com.styenvy.egsoulharvest.init;

import com.styenvy.egsoulharvest.EGSoulHarvest;
import com.styenvy.egsoulharvest.block.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(EGSoulHarvest.MODID);

    // Soul Recycler
    public static final DeferredBlock<SoulRecyclerBlock> SOUL_RECYCLER = BLOCKS.register("soul_recycler",
            () -> new SoulRecyclerBlock(BlockBehaviour.Properties.of()
                    .strength(3.5f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)
                    .lightLevel(state -> state.getValue(SoulRecyclerBlock.POWERED) ? 7 : 0)));

    // Soul Harvesters
    public static final DeferredBlock<CaveSpiderHarvesterBlock> CAVE_SPIDER_HARVESTER = BLOCKS.register("cave_spider_harvester",
            () -> new CaveSpiderHarvesterBlock(harvesterProperties()));

    public static final DeferredBlock<SpiderHarvesterBlock> SPIDER_HARVESTER = BLOCKS.register("spider_harvester",
            () -> new SpiderHarvesterBlock(harvesterProperties()));

    public static final DeferredBlock<ZombieHarvesterBlock> ZOMBIE_HARVESTER = BLOCKS.register("zombie_harvester",
            () -> new ZombieHarvesterBlock(harvesterProperties()));

    public static final DeferredBlock<SkeletonHarvesterBlock> SKELETON_HARVESTER = BLOCKS.register("skeleton_harvester",
            () -> new SkeletonHarvesterBlock(harvesterProperties()));

    public static final DeferredBlock<BlazeHarvesterBlock> BLAZE_HARVESTER = BLOCKS.register("blaze_harvester",
            () -> new BlazeHarvesterBlock(harvesterProperties()));

    public static final DeferredBlock<MagmaCubeHarvesterBlock> MAGMA_CUBE_HARVESTER = BLOCKS.register("magma_cube_harvester",
            () -> new MagmaCubeHarvesterBlock(harvesterProperties()));

    private static BlockBehaviour.Properties harvesterProperties() {
        return BlockBehaviour.Properties.of()
                .strength(3.5f)
                .requiresCorrectToolForDrops()
                .sound(SoundType.METAL)
                .lightLevel(state -> state.getValue(BaseSoulHarvesterBlock.POWERED) ? 5 : 0);
    }
}
