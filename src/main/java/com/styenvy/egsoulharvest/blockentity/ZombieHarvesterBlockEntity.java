package com.styenvy.egsoulharvest.blockentity;

import com.styenvy.egsoulharvest.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ZombieHarvesterBlockEntity extends BaseSoulHarvesterBlockEntity {

    public ZombieHarvesterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ZOMBIE_HARVESTER.get(), pos, state);
    }

    @Override
    public EntityType<?> getTargetEntityType() {
        return EntityType.ZOMBIE;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.egsoulharvest.zombie_harvester");
    }
}
