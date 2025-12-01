package com.styenvy.egsoulharvest.blockentity;

import com.styenvy.egsoulharvest.init.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

public class BlazeHarvesterBlockEntity extends BaseSoulHarvesterBlockEntity {

    public BlazeHarvesterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BLAZE_HARVESTER.get(), pos, state);
    }

    @Override
    public EntityType<?> getTargetEntityType() {
        return EntityType.BLAZE;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.egsoulharvest.blaze_harvester");
    }

    @Override
    protected ResourceKey<LootTable> getHarvestLootTableKey() {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(
                "egsoulharvest",
                "harvesters/blaze_harvester"
        );
        return ResourceKey.create(Registries.LOOT_TABLE, id);
    }
}
