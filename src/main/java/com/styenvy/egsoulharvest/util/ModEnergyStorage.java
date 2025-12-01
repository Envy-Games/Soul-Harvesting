package com.styenvy.egsoulharvest.util;

import net.neoforged.neoforge.energy.EnergyStorage;

/**
 * Custom energy storage implementation that:
 * - Fires a callback whenever the stored energy changes (for marking block entities dirty, etc.).
 * - Provides "internal" methods that can bypass external maxReceive/maxExtract limits while still
 *   respecting capacity and clamping
 * Typical usage from a BlockEntity:
 *   private final ModEnergyStorage energy = new ModEnergyStorage(10_000, 100, this::setChanged);
 */
public class ModEnergyStorage extends EnergyStorage {

    /**
     * Called whenever the stored energy or configuration changes.
     * Usually this will be BlockEntity#setChanged on the server.
     */
    private final Runnable onChanged;

    /**
     * Convenience constructor where maxReceive == maxExtract == maxTransfer.
     */
    public ModEnergyStorage(int capacity, int maxTransfer, Runnable onChanged) {
        this(capacity, maxTransfer, maxTransfer, onChanged);
    }

    /**
     * Full constructor with independent maxReceive/maxExtract.
     */
    public ModEnergyStorage(int capacity, int maxReceive, int maxExtract, Runnable onChanged) {
        super(capacity, maxReceive, maxExtract);
        this.onChanged = (onChanged != null) ? onChanged : () -> {};
    }

    // ---------------------------------------------------------------------
    // IEnergyStorage API – used by cables / other blocks
    // ---------------------------------------------------------------------

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive() || maxReceive <= 0) {
            return 0;
        }

        int received = super.receiveEnergy(maxReceive, simulate);
        if (!simulate && received > 0) {
            onChanged.run();
        }
        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract() || maxExtract <= 0) {
            return 0;
        }

        int extracted = super.extractEnergy(maxExtract, simulate);
        if (!simulate && extracted > 0) {
            onChanged.run();
        }
        return extracted;
    }

    // ---------------------------------------------------------------------
    // Internal helpers – ignore external maxReceive/maxExtract caps
    // ---------------------------------------------------------------------

    /**
     * Add energy ignoring {@code maxReceive}, but still clamping to capacity.
     *
     * @param amount   energy to try to add
     * @param simulate if true, do not modify stored energy
     * @return the amount of energy that would be / was actually added
     */
    public int internalReceiveEnergy(int amount, boolean simulate) {
        if (amount <= 0) {
            return 0;
        }

        int space = capacity - energy;
        if (space <= 0) {
            return 0;
        }

        int toReceive = Math.min(amount, space);
        if (!simulate) {
            energy += toReceive;
            onChanged.run();
        }
        return toReceive;
    }

    /**
     * Extract energy ignoring {@code maxExtract}, but still clamping to available energy.
     *
     * @param amount   energy to try to extract
     * @param simulate if true, do not modify stored energy
     * @return the amount of energy that would be / was actually removed
     */
    public int internalExtractEnergy(int amount, boolean simulate) {
        if (amount <= 0) {
            return 0;
        }

        int available = energy;
        if (available <= 0) {
            return 0;
        }

        int toExtract = Math.min(amount, available);
        if (!simulate) {
            energy -= toExtract;
            onChanged.run();
        }
        return toExtract;
    }

    /**
     * Directly set the stored energy, clamped between 0 and capacity,
     * firing the change callback if it actually changes.
     */
    public void setEnergy(int energy) {
        int clamped = Math.max(0, Math.min(energy, capacity));
        if (clamped != this.energy) {
            this.energy = clamped;
            onChanged.run();
        }
    }

    /**
     * Add a fixed amount of energy, clamped to capacity.
     */
    public void addEnergy(int amount) {
        if (amount <= 0) {
            return;
        }
        setEnergy(this.energy + amount);
    }

    /**
     * Consume a fixed amount of energy, clamped at 0.
     */
    public void consumeEnergy(int amount) {
        if (amount <= 0) {
            return;
        }
        setEnergy(this.energy - amount);
    }

    /**
     * Set a new capacity. If the current energy exceeds it, it will be clamped.
     */
    public void setCapacity(int capacity) {
        int newCapacity = Math.max(0, capacity);
        if (this.capacity != newCapacity) {
            this.capacity = newCapacity;
            if (energy > this.capacity) {
                energy = this.capacity;
            }
            onChanged.run();
        }
    }

    /**
     * Dynamically change the maximum external receive rate.
     */
    public void setMaxReceive(int maxReceive) {
        int newMax = Math.max(0, maxReceive);
        if (this.maxReceive != newMax) {
            this.maxReceive = newMax;
            onChanged.run();
        }
    }

    /**
     * Dynamically change the maximum external extract rate.
     */
    public void setMaxExtract(int maxExtract) {
        int newMax = Math.max(0, maxExtract);
        if (this.maxExtract != newMax) {
            this.maxExtract = newMax;
            onChanged.run();
        }
    }

    /**
     * Clear all stored energy.
     */
    public void clear() {
        if (energy > 0) {
            energy = 0;
            onChanged.run();
        }
    }

    // ---------------------------------------------------------------------
    // Convenience helpers / predicates
    // ---------------------------------------------------------------------

    /**
     * Check if the storage has room for more energy.
     */
    public boolean hasCapacity() {
        return energy < capacity;
    }

    /**
     * Check if the storage has any energy.
     */
    public boolean hasEnergy() {
        return energy > 0;
    }

    /**
     * @return true if storage is completely full.
     */
    public boolean isFull() {
        return energy >= capacity && capacity > 0;
    }

    /**
     * @return true if storage is completely empty.
     */
    public boolean isEmpty() {
        return energy <= 0;
    }

    /**
     * @return fill ratio between 0.0 and 1.0
     */
    public float getFillRatio() {
        if (capacity <= 0) {
            return 0.0f;
        }
        return (float) energy / (float) capacity;
    }
}
