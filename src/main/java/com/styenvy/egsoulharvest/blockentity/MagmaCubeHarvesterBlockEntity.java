package com.styenvy.egsoulharvest.blockentity;

import com.styenvy.egsoulharvest.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class MagmaCubeHarvesterBlockEntity extends BaseSoulHarvesterBlockEntity {

    public MagmaCubeHarvesterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MAGMA_CUBE_HARVESTER.get(), pos, state);
    }

    @Override
    public EntityType<?> getTargetEntityType() {
        return EntityType.MAGMA_CUBE;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.egsoulharvest.magma_cube_harvester");
    }
}
