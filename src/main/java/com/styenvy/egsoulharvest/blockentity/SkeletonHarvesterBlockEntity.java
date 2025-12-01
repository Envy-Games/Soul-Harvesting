package com.styenvy.egsoulharvest.blockentity;

import com.styenvy.egsoulharvest.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class SkeletonHarvesterBlockEntity extends BaseSoulHarvesterBlockEntity {

    public SkeletonHarvesterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SKELETON_HARVESTER.get(), pos, state);
    }

    @Override
    public EntityType<?> getTargetEntityType() {
        return EntityType.SKELETON;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.egsoulharvest.skeleton_harvester");
    }
}
