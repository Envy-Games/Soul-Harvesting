package com.styenvy.egsoulharvest.mixin;

import com.styenvy.egsoulharvest.block.BaseSoulHarvesterBlock;
import com.styenvy.egsoulharvest.block.SoulRecyclerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Cancels vanilla spawner logic whenever a Soul Recycler or any Soul Harvester
 * is placed directly on top of the spawner.
 *
 * Notes:
 * - Dimension-agnostic: works in Nether, Overworld, custom dimensions.
 * - Entity-agnostic: we don't care what the spawner is configured to spawn.
 * - Loot and "active" state are handled entirely by the harvester block entity.
 */
@Mixin(BaseSpawner.class)
public abstract class BaseSpawnerMixin {

    /**
     * Inject at the start of BaseSpawner#serverTick(ServerLevel, BlockPos)
     * and cancel completely when any of our blocks sit on top.
     */
    @Inject(method = "serverTick", at = @At("HEAD"), cancellable = true)
    private void egsoulharvest$cancelVanillaSpawnsWhenHarvesterAbove(ServerLevel level,
                                                                     BlockPos spawnerPos,
                                                                     CallbackInfo ci) {
        // Safety: this is always a ServerLevel in this method signature,
        // but we keep the check to document intent.
        if (level == null || level.isClientSide()) {
            return;
        }

        // Check the block directly above the spawner
        BlockPos abovePos = spawnerPos.above();
        BlockState aboveState = level.getBlockState(abovePos);

        // Fast path: no block above -> let vanilla spawner run normally
        if (aboveState.isAir()) {
            return;
        }

        Block blockAbove = aboveState.getBlock();

        // If it's a Soul Recycler OR ANY Soul Harvester variant, kill this tick.
        if (blockAbove instanceof SoulRecyclerBlock || blockAbove instanceof BaseSoulHarvesterBlock) {
            // No vanilla spawns at all – our block entities handle the "virtual" harvesting & loot.
            ci.cancel();
        }
    }
}
