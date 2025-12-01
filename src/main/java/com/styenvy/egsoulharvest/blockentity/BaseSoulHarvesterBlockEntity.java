package com.styenvy.egsoulharvest.blockentity;

import com.styenvy.egsoulharvest.block.BaseSoulHarvesterBlock;
import com.styenvy.egsoulharvest.config.ModConfig;
import com.styenvy.egsoulharvest.menu.HarvesterMenu;
import com.styenvy.egsoulharvest.util.SpawnerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public abstract class BaseSoulHarvesterBlockEntity extends BlockEntity implements Container, net.minecraft.world.MenuProvider {

    private static final String ITEMS_TAG = "Items";
    private static final String TICK_COUNTER_TAG = "TickCounter";

    protected final ItemStackHandler inventory;
    private int tickCounter = 0;
    private boolean isActive = false;

    public BaseSoulHarvesterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.inventory = createInventory();
    }

    protected ItemStackHandler createInventory() {
        return new ItemStackHandler(ModConfig.HARVESTER_INVENTORY_SIZE.get()) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
    }

    /**
     * Get the entity type this harvester targets
     */
    public abstract EntityType<?> getTargetEntityType();

    /**
     * Get the display name for this harvester
     */
    public abstract @NotNull Component getDisplayName();

    public static void serverTick(Level level, BlockPos pos, BlockState state, BaseSoulHarvesterBlockEntity blockEntity) {
        blockEntity.tick(level, pos, state);
    }

    private void tick(Level level, BlockPos pos, BlockState state) {
        if (level == null || level.isClientSide()) {
            return;
        }

        // Check if there's a valid spawner below
        BlockPos spawnerPos = pos.below();

        // Update active state
        isActive = SpawnerHelper.isSpawnerAt(level, spawnerPos) &&
                SpawnerHelper.spawnerMatchesType(level, spawnerPos, getTargetEntityType());

        // Update block state if needed
        if (state.getValue(BaseSoulHarvesterBlock.POWERED) != isActive) {
            level.setBlock(pos, state.setValue(BaseSoulHarvesterBlock.POWERED, isActive), 3);
        }

        if (!isActive) {
            return;
        }

        // NOTE:
        // Actual spawn *prevention* is handled by BaseSpawnerMixin, which cancels BaseSpawner#serverTick
        // when a Soul Harvester / Soul Recycler is above the spawner. We don't touch spawnDelay here.

        // Increment tick counter and generate loot
        tickCounter++;
        if (tickCounter >= ModConfig.HARVESTER_LOOT_INTERVAL.get()) {
            tickCounter = 0;
            generateLoot((ServerLevel) level, pos);
        }
    }

    /**
     * Generate loot based on the target mob's loot table
     */
    private void generateLoot(ServerLevel level, BlockPos pos) {
        EntityType<?> entityType = getTargetEntityType();
        ResourceLocation entityId = EntityType.getKey(entityType);

        // Construct loot table location: minecraft:entities/zombie, etc.
        ResourceLocation lootTableId = ResourceLocation.fromNamespaceAndPath(
                entityId.getNamespace(),
                "entities/" + entityId.getPath()
        );
        ResourceKey<LootTable> lootTableKey = ResourceKey.create(Registries.LOOT_TABLE, lootTableId);

        LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(lootTableKey);

        // Create a dummy entity instance so we can satisfy LootContextParamSets.ENTITY requirements
        Entity dummyEntity = entityType.create(level);
        if (dummyEntity == null) {
            return; // Some entity types can't be instantiated without extra data
        }
        dummyEntity.setPos(Vec3.atCenterOf(pos));

        // Create loot context (ENTITY param set requires THIS_ENTITY, ORIGIN, DAMAGE_SOURCE)
        LootParams.Builder paramsBuilder = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, dummyEntity)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().generic());

        // Generate loot
        List<ItemStack> loot = lootTable.getRandomItems(paramsBuilder.create(LootContextParamSets.ENTITY));

        // Add loot to inventory
        for (ItemStack stack : loot) {
            insertItem(stack);
        }
    }

    /**
     * Try to insert an item into the inventory
     */
    private void insertItem(ItemStack stack) {
        for (int i = 0; i < inventory.getSlots() && !stack.isEmpty(); i++) {
            stack = inventory.insertItem(i, stack, false);
        }
        // If we couldn't insert everything, it's just lost (inventory full)
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put(ITEMS_TAG, inventory.serializeNBT(registries));
        tag.putInt(TICK_COUNTER_TAG, tickCounter);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(ITEMS_TAG)) {
            inventory.deserializeNBT(registries, tag.getCompound(ITEMS_TAG));
        }
        tickCounter = tag.getInt(TICK_COUNTER_TAG);
    }

    // IItemHandler access for capability
    public IItemHandler getItemHandler(@Nullable Direction side) {
        return inventory;
    }

    public boolean isActive() {
        return isActive;
    }

    // Container implementation for vanilla compatibility
    @Override
    public int getContainerSize() {
        return inventory.getSlots();
    }

    @Override
    public boolean isEmpty() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            if (!inventory.getStackInSlot(i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        return inventory.getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        return inventory.extractItem(slot, amount, false);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = inventory.getStackInSlot(slot);
        inventory.setStackInSlot(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        inventory.setStackInSlot(slot, stack);
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    // MenuProvider implementation
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new HarvesterMenu(containerId, playerInventory, this);
    }
}
