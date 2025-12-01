package com.styenvy.egsoulharvest.block;

import com.mojang.serialization.MapCodec;
import com.styenvy.egsoulharvest.blockentity.SoulRecyclerBlockEntity;
import com.styenvy.egsoulharvest.init.ModBlockEntities;
import com.styenvy.egsoulharvest.init.ModTags;
import com.styenvy.egsoulharvest.util.SpawnerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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

public class SoulRecyclerBlock extends BaseEntityBlock {
    public static final MapCodec<SoulRecyclerBlock> CODEC = simpleCodec(SoulRecyclerBlock::new);

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public SoulRecyclerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false));
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
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

    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        Direction facing = state.getValue(FACING);
        Rotation rotation = mirror.getRotation(facing);

        return switch (rotation) {
            case NONE -> state;
            case CLOCKWISE_90 -> state.setValue(FACING, facing.getClockWise());
            case COUNTERCLOCKWISE_90 -> state.setValue(FACING, facing.getCounterClockWise());
            case CLOCKWISE_180 -> state.setValue(FACING, facing.getOpposite());
        };
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new SoulRecyclerBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level,
                                                                  @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (level.isClientSide()) {
            return null;
        }
        return createTickerHelper(type, ModBlockEntities.SOUL_RECYCLER.get(), SoulRecyclerBlockEntity::serverTick);
    }

    /**
     * Right-click handler – shows energy + active state.
     */
    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state,
                                                        Level level,
                                                        @NotNull BlockPos pos,
                                                        @NotNull Player player,
                                                        @NotNull BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SoulRecyclerBlockEntity recycler) {
                int energy = recycler.getEnergyStored();
                int maxEnergy = recycler.getMaxEnergyStored();
                boolean active = recycler.isActive();

                player.displayClientMessage(
                        Component.literal(String.format(
                                "Soul Recycler: %d / %d FE (%s)",
                                energy,
                                maxEnergy,
                                active ? "Active" : "Inactive"
                        )),
                        true
                );
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    /**
     * Only items in #egsoulharvest:soul_recycler_tools can actually make mining progress.
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
        if (!held.is(ModTags.Items.SOUL_RECYCLER_TOOLS)) {
            // 0 = no mining progress, block never breaks from mining
            return 0.0F;
        }

        // Correct tool (in tag) -> normal mining speed
        return super.getDestroyProgress(state, player, level, pos);
    }

    /**
     * Must sit directly on top of a spawner.
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
            // If the spawner below is gone, drop this block.
            if (!SpawnerHelper.isSpawnerAt(level, pos.below())) {
                level.destroyBlock(pos, true);
            }
        }
    }

    private void updatePoweredState(Level level, BlockPos pos, BlockState state) {
        boolean hasSpawner = SpawnerHelper.isSpawnerAt(level, pos.below());
        if (state.getValue(POWERED) != hasSpawner) {
            level.setBlock(pos, state.setValue(POWERED, hasSpawner), 3);
        }
    }
}
