package com.styenvy.egsoulharvest.block;

import com.mojang.serialization.MapCodec;
import com.styenvy.egsoulharvest.blockentity.BaseSoulHarvesterBlockEntity;
import com.styenvy.egsoulharvest.blockentity.SpiderHarvesterBlockEntity;
import com.styenvy.egsoulharvest.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * Concrete Soul Harvester block for Spiders.
 */
public class SpiderHarvesterBlock extends BaseSoulHarvesterBlock {

    public static final MapCodec<SpiderHarvesterBlock> CODEC = simpleCodec(SpiderHarvesterBlock::new);

    public SpiderHarvesterBlock(Properties properties) {
        super(properties, () -> EntityType.SPIDER);
    }

    @Override
    protected @NotNull MapCodec<? extends BaseSoulHarvesterBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new SpiderHarvesterBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level,
                                                                  @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return createTickerHelper(
                type,
                ModBlockEntities.SPIDER_HARVESTER.get(),
                BaseSoulHarvesterBlockEntity::serverTick
        );
    }
}
