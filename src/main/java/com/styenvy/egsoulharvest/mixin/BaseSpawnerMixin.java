package com.styenvy.egsoulharvest.mixin;

import com.styenvy.egsoulharvest.block.BaseSoulHarvesterBlock;
import com.styenvy.egsoulharvest.block.SoulRecyclerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Stops vanilla spawner logic whenever a Soul Recycler or any Soul Harvester
 * is placed directly on top of the spawner.
 * This is dimension-agnostic and entity-agnostic:
 * if our blocks are above the spawner, we cancel ALL vanilla spawns.
 */
@Mixin(BaseSpawner.class)
public abstract class BaseSpawnerMixin {

    /**
     * Inject at the start of BaseSpawner#serverTick(ServerLevel, BlockPos)
     * and cancel completely when our blocks are sitting on top.
     */
    @Inject(method = "serverTick", at = @At("HEAD"), cancellable = true)
    private void egsoulharvest$blockSpawnsWhenHarvesterAbove(ServerLevel level,
                                                             BlockPos pos,
                                                             CallbackInfo ci) {
        // Position directly above the spawner
        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);

        // Fast path: nothing above -> let vanilla run
        if (aboveState.isAir()) {
            return;
        }

        // If it's a Soul Recycler OR ANY Soul Harvester variant, kill the tick.
        if (aboveState.getBlock() instanceof SoulRecyclerBlock
                || aboveState.getBlock() instanceof BaseSoulHarvesterBlock) {
            // No vanilla spawns at all – our block entities handle the behavior.
            ci.cancel();
        }
    }
}
