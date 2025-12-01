package com.styenvy.egsoulharvest.blockentity;

import com.styenvy.egsoulharvest.block.SoulRecyclerBlock;
import com.styenvy.egsoulharvest.config.ModConfig;
import com.styenvy.egsoulharvest.init.ModBlockEntities;
import com.styenvy.egsoulharvest.util.ModEnergyStorage;
import com.styenvy.egsoulharvest.util.SpawnerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

public class SoulRecyclerBlockEntity extends BlockEntity {

    private static final String ENERGY_TAG = "Energy";

    private final ModEnergyStorage energyStorage;
    private boolean isActive = false;

    public SoulRecyclerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SOUL_RECYCLER.get(), pos, state);
        this.energyStorage = new ModEnergyStorage(
                ModConfig.SOUL_RECYCLER_MAX_STORAGE.get(),
                ModConfig.SOUL_RECYCLER_MAX_TRANSFER.get(),
                this::setChanged
        );
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SoulRecyclerBlockEntity blockEntity) {
        blockEntity.tick(level, pos, state);
    }

    private void tick(Level level, BlockPos pos, BlockState state) {
        if (level == null || level.isClientSide()) {
            return;
        }

        // Check if there's a spawner below
        BlockPos spawnerPos = pos.below();
        boolean hasSpawner = SpawnerHelper.isSpawnerAt(level, spawnerPos);

        // Update active state
        boolean wasActive = isActive;
        isActive = hasSpawner && energyStorage.hasCapacity();

        // Sync block state
        if (state.getValue(SoulRecyclerBlock.POWERED) != isActive) {
            level.setBlock(pos, state.setValue(SoulRecyclerBlock.POWERED, isActive), 3);
        }

        // Generate energy if active
        if (isActive) {
            int generated = energyStorage.internalReceiveEnergy(
                    ModConfig.SOUL_RECYCLER_FE_PER_TICK.get(),
                    false
            );
            if (generated > 0) {
                setChanged();
            }
        }

        // Push energy to adjacent blocks
        if (energyStorage.hasEnergy()) {
            pushEnergyToNeighbors(level, pos);
        }

        if (wasActive != isActive) {
            setChanged();
        }
    }

    /**
     * Try to push energy to all 6 neighboring blocks, up to the
     * configured max transfer per tick.
     */
    private void pushEnergyToNeighbors(Level level, BlockPos pos) {
        if (level == null || level.isClientSide()) {
            return;
        }

        for (Direction direction : Direction.values()) {
            if (!energyStorage.hasEnergy()) {
                break;
            }

            BlockPos neighborPos = pos.relative(direction);
            IEnergyStorage neighborEnergy = level.getCapability(
                    Capabilities.EnergyStorage.BLOCK,
                    neighborPos,
                    direction.getOpposite()
            );

            if (neighborEnergy != null && neighborEnergy.canReceive()) {
                int maxTransfer = Math.min(
                        energyStorage.getEnergyStored(),
                        ModConfig.SOUL_RECYCLER_MAX_TRANSFER.get()
                );
                int accepted = neighborEnergy.receiveEnergy(maxTransfer, false);
                if (accepted > 0) {
                    energyStorage.internalExtractEnergy(accepted, false);
                }
            }
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(ENERGY_TAG, energyStorage.getEnergyStored());
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(ENERGY_TAG)) {
            energyStorage.setEnergy(tag.getInt(ENERGY_TAG));
        }
    }

    // Public getters for capability registration
    public IEnergyStorage getEnergyStorage(Direction side) {
        return energyStorage;
    }

    public int getEnergyStored() {
        return energyStorage.getEnergyStored();
    }

    public int getMaxEnergyStored() {
        return energyStorage.getMaxEnergyStored();
    }

    public boolean isActive() {
        return isActive;
    }
}
