package com.styenvy.egsoulharvest.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;

import javax.annotation.Nullable;

/**
 * Utility class for interacting with spawner blocks.
 *
 * This helper is intentionally read-only and safe to use on the logical server.
 * It focuses on:
 *  - Detecting spawners at positions
 *  - Getting the {@link SpawnerBlockEntity}
 *  - Reading the entity type a spawner is configured to spawn
 */
public class SpawnerHelper {

    /**
     * Check if there is a vanilla monster spawner block at the given position.
     */
    public static boolean isSpawnerAt(Level level, BlockPos pos) {
        return level.getBlockState(pos).is(Blocks.SPAWNER);
    }

    /**
     * Get the {@link SpawnerBlockEntity} at the given position, or {@code null} if
     * there is no spawner or the block entity is the wrong type.
     */
    @Nullable
    public static SpawnerBlockEntity getSpawnerEntity(Level level, BlockPos pos) {
        if (!isSpawnerAt(level, pos)) {
            return null;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof SpawnerBlockEntity spawnerBE) {
            return spawnerBE;
        }

        return null;
    }

    /**
     * Get the entity type being spawned by the spawner at the given position.
     * <p>
     * This uses the spawner's internal NBT ({@link BaseSpawner#SPAWN_DATA_TAG})
     * instead of calling any private methods like getOrCreateNextSpawnData.
     *
     * @return the configured {@link EntityType}, or {@code null} if not set / invalid
     */
    @Nullable
    public static EntityType<?> getSpawnerEntityType(Level level, BlockPos pos) {
        SpawnerBlockEntity spawnerBE = getSpawnerEntity(level, pos);
        if (spawnerBE == null) {
            return null;
        }

        BaseSpawner spawner = spawnerBE.getSpawner();

        // Dump the BaseSpawner's data into a tag so we can read SpawnData -> entity -> id
        CompoundTag spawnerTag = spawner.save(new CompoundTag());

        if (!spawnerTag.contains(BaseSpawner.SPAWN_DATA_TAG, Tag.TAG_COMPOUND)) {
            return null;
        }

        CompoundTag spawnDataTag = spawnerTag.getCompound(BaseSpawner.SPAWN_DATA_TAG);

        // In modern versions SpawnData has an "entity" compound with the entity's ID.
        if (!spawnDataTag.contains("entity", Tag.TAG_COMPOUND)) {
            return null;
        }

        CompoundTag entityTag = spawnDataTag.getCompound("entity");
        String entityId = entityTag.getString("id");
        if (entityId.isEmpty()) {
            return null;
        }

        // Reuse our helper for registry lookup
        return getEntityTypeByName(entityId);
    }

    /**
     * Check if the spawner at the given position spawns the specified entity type.
     */
    public static boolean spawnerMatchesType(Level level, BlockPos pos, EntityType<?> expectedType) {
        EntityType<?> actualType = getSpawnerEntityType(level, pos);
        return actualType != null && actualType.equals(expectedType);
    }

    /**
     * Get the entity type from its registry name (e.g. "minecraft:zombie").
     */
    @Nullable
    public static EntityType<?> getEntityTypeByName(String name) {
        ResourceLocation location = ResourceLocation.tryParse(name);
        if (location == null) {
            return null;
        }
        return BuiltInRegistries.ENTITY_TYPE.get(location);
    }
}
