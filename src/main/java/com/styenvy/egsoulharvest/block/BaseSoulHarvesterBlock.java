package com.styenvy.egsoulharvest.block;

import com.mojang.serialization.MapCodec;
import com.styenvy.egsoulharvest.blockentity.BaseSoulHarvesterBlockEntity;
import com.styenvy.egsoulharvest.init.ModTags;
import com.styenvy.egsoulharvest.util.SpawnerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public abstract class BaseSoulHarvesterBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    private final Supplier<EntityType<?>> targetEntityType;

    public BaseSoulHarvesterBlock(Properties properties, Supplier<EntityType<?>> targetEntityType) {
        super(properties);
        this.targetEntityType = targetEntityType;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false));
    }

    @Override
    protected abstract @NotNull MapCodec<? extends BaseSoulHarvesterBlock> codec();

    public EntityType<?> getTargetEntityType() {
        return targetEntityType.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(POWERED, false);
    }

    /**
     * Vanilla-style rotation (no level context).
     * This is still valid; NeoForge deprecated the *BlockStateBase.rotate* call,
     * not this override. We only touch the FACING property here.
     */
    @Override
    public @NotNull BlockState rotate(@NotNull BlockState state, @NotNull Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    /**
     * New NeoForge / 1.21-style rotation with level context.
     * We just delegate to the simple version because we don't need extra info.
     */
    @Override
    public @NotNull BlockState rotate(@NotNull BlockState state,
                                      @NotNull LevelAccessor level,
                                      @NotNull BlockPos pos,
                                      @NotNull Rotation rotation) {
        return this.rotate(state, rotation);
    }

    /**
     * Mirror without calling the deprecated BlockStateBase.rotate(Rotation).
     * We compute the mirrored direction directly.
     */
    @Override
    public @NotNull BlockState mirror(@NotNull BlockState state, @NotNull Mirror mirror) {
        Direction facing = state.getValue(FACING);
        Direction mirrored = mirror.mirror(facing);
        return state.setValue(FACING, mirrored);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public abstract BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state);

    @Nullable
    @Override
    public abstract <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level,
                                                                           @NotNull BlockState state,
                                                                           @NotNull BlockEntityType<T> type);

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state,
                                                        Level level,
                                                        @NotNull BlockPos pos,
                                                        @NotNull Player player,
                                                        @NotNull BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BaseSoulHarvesterBlockEntity harvester && player instanceof ServerPlayer serverPlayer) {
                serverPlayer.openMenu(harvester, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    /**
     * Only items in #egsoulharvest:harvester_tools can actually make mining progress.
     * Everyone else (non-tag tools, fists, etc.) gets 0 progress.
     */
    @Override
    protected float getDestroyProgress(@NotNull BlockState state,
                                       @NotNull Player player,
                                       @NotNull BlockGetter level,
                                       @NotNull BlockPos pos) {
        // Creative always allowed
        if (player.isCreative()) {
            return super.getDestroyProgress(state, player, level, pos);
        }

        ItemStack held = player.getMainHandItem();
        if (!held.is(ModTags.Items.HARVESTER_TOOLS)) {
            // 0 = no mining progress, block never breaks from mining
            return 0.0F;
        }

        // Correct tool (in tag) -> normal mining speed
        return super.getDestroyProgress(state, player, level, pos);
    }

    /**
     * Placement check: must sit directly on top of *some* spawner.
     * We only check the block type here; full entity-type validation happens in onPlace()
     * where we have a full Level instead of just a LevelReader.
     */
    @Override
    public boolean canSurvive(@NotNull BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        return level.getBlockState(below).is(Blocks.SPAWNER);
    }

    @Override
    public void onPlace(@NotNull BlockState state,
                        @NotNull Level level,
                        @NotNull BlockPos pos,
                        @NotNull BlockState oldState,
                        boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);

        if (!level.isClientSide() && !oldState.is(this)) {
            // Validate spawner type
            if (!isValidSpawnerBelow(level, pos)) {
                // Wrong or missing spawner type -> break and drop this block
                level.destroyBlock(pos, true);
                return;
            }
            updatePoweredState(level, pos, state);
        }
    }

    @Override
    public void neighborChanged(@NotNull BlockState state,
                                @NotNull Level level,
                                @NotNull BlockPos pos,
                                @NotNull Block block,
                                @NotNull BlockPos fromPos,
                                boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);

        if (!level.isClientSide()) {
            if (!isValidSpawnerBelow(level, pos)) {
                // Spawner removed or changed to an incompatible type
                level.destroyBlock(pos, true);
            } else {
                // Keep POWERED in sync with current validity
                updatePoweredState(level, pos, state);
            }
        }
    }

    @Override
    public void onRemove(BlockState state,
                         @NotNull Level level,
                         @NotNull BlockPos pos,
                         BlockState newState,
                         boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof BaseSoulHarvesterBlockEntity harvester) {
                Containers.dropContents(level, pos, harvester);
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    /**
     * Uses SpawnerHelper to check that there is a spawner below AND that
     * its current spawn entity matches this harvester's configured target type.
     */
    private boolean isValidSpawnerBelow(Level level, BlockPos pos) {
        BlockPos spawnerPos = pos.below();
        if (!SpawnerHelper.isSpawnerAt(level, spawnerPos)) {
            return false;
        }
        return SpawnerHelper.spawnerMatchesType(level, spawnerPos, getTargetEntityType());
    }

    /**
     * POWERED = "is everything set up correctly?".
     * This lets your blockstate/model/logic react when the spawner becomes
     * valid/invalid without having to re-place the block.
     */
    private void updatePoweredState(Level level, BlockPos pos, BlockState state) {
        boolean hasValidSpawner = isValidSpawnerBelow(level, pos);
        if (state.getValue(POWERED) != hasValidSpawner) {
            level.setBlock(pos, state.setValue(POWERED, hasValidSpawner), Block.UPDATE_ALL);
        }
    }
}
