package com.styenvy.egsoulharvest.mixin;

import com.styenvy.egsoulharvest.block.BaseSoulHarvesterBlock;
import com.styenvy.egsoulharvest.block.SoulRecyclerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BaseSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin into {@link BaseSpawner} to suppress actual mob spawning when a Soul
 * Recycler or Soul Harvester is placed directly above the spawner.
 * - We inject into {@code serverTick(ServerLevel, BlockPos)} on the logical server.
 * - When our blocks are present above the spawner, we cancel the tick, which
 *   prevents spawning but leaves client-side visual spinning intact (handled by
 *   the client tick on the block entity).
 */
@Mixin(BaseSpawner.class)
public abstract class BaseSpawnerMixin {

    /**
     * Inject at the start of {@link BaseSpawner#serverTick(ServerLevel, BlockPos)}
     * to cancel spawning if one of our blocks is directly above the spawner.
     * Method descriptor is specified explicitly to be robust against future overloads:
     *   (Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;)V
     */
    @Inject(
            method = "serverTick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void egsoulharvest$onServerTick(ServerLevel level, BlockPos pos, CallbackInfo ci) {
        // Position directly above this spawner
        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);

        // If our Soul Recycler or any Soul Harvester block is above, we prevent
        // the server-side spawning logic from running. Our blocks will instead
        // handle FE generation / loot production on their own ticks.
        if (aboveState.getBlock() instanceof SoulRecyclerBlock
                || aboveState.getBlock() instanceof BaseSoulHarvesterBlock) {
            ci.cancel();
        }
    }
}
