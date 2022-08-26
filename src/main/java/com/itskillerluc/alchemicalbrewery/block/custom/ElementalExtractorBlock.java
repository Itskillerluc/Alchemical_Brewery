package com.itskillerluc.alchemicalbrewery.block.custom;

import com.itskillerluc.alchemicalbrewery.tileentity.ElementalExtractorTile;
import com.itskillerluc.alchemicalbrewery.tileentity.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public class ElementalExtractorBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public ElementalExtractorBlock(Properties properties) {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(LIT, Boolean.FALSE));
    }

    private static final VoxelShape SHAPE_W = Stream.of(
            Block.box(6, 8.75, 0, 10, 9.5, 4),
            Block.box(6.5, 8.75, 0.5, 9.5, 10.25, 3.5),
            Block.box(6, 1.5, 0, 10, 8.75, 11),
            Block.box(4.5, 3.375, 11.225, 11.5, 10.625, 16.225)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SHAPE_N = Stream.of(
            Block.box(11.806250000000002, 8.75, 6.056250000000002, 15.806249999999999, 9.5, 10.056250000000002),
            Block.box(12.306249999999999, 8.75, 6.556250000000002, 15.306249999999999, 10.25, 9.556250000000002),
            Block.box(4.806250000000002, 1.5000000000000018, 6.056250000000002, 15.806249999999999, 8.75, 10.056250000000002),
            Block.box(-0.4187499999999993, 3.375, 4.556250000000002, 4.5812500000000025, 10.625, 11.556250000000002)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SHAPE_E = Stream.of(
            Block.box(6, 8.75, 12.112500000000004, 10, 9.5, 16.1125),
            Block.box(6.5, 8.75, 12.6125, 9.5, 10.25, 15.6125),
            Block.box(6, 1.5000000000000018, 5.112500000000004, 10, 8.75, 16.1125),
            Block.box(4.5, 3.375, -0.11249999999999716, 11.5, 10.625, 4.887500000000005)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SHAPE_S = Stream.of(
            Block.box(-0.05624999999999858, 8.75, 6.056250000000002, 3.943749999999998, 9.5, 10.056250000000002),
            Block.box(0.4437500000000014, 8.75, 6.556250000000002, 3.4437500000000014, 10.25, 9.556250000000002),
            Block.box(-0.05624999999999858, 1.5000000000000018, 6.056250000000002, 10.943749999999998, 8.75, 10.056250000000002),
            Block.box(11.168749999999998, 3.375, 4.556250000000002, 16.16875, 10.625, 11.556250000000002)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return getVoxelShape(pState, FACING, SHAPE_N, SHAPE_S, SHAPE_W, SHAPE_E);
    }

    static VoxelShape getVoxelShape(BlockState pState, DirectionProperty facing, VoxelShape shapeN, VoxelShape shapeS, VoxelShape shapeW, VoxelShape shapeE) {
        return switch (pState.getValue(facing)) {
            case SOUTH -> shapeS;
            case WEST -> shapeW;
            case EAST -> shapeE;
            default -> shapeN;
        };
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public @NotNull BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, LIT);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos, @NotNull Player pPlayer, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHit) {
        if (pLevel.isClientSide()) {
            return InteractionResult.PASS;
        }

        BlockEntity entity = pLevel.getBlockEntity(pPos);

        if (entity instanceof ElementalExtractorTile extractorTile) {
            NetworkHooks.openGui((ServerPlayer) pPlayer, extractorTile, pPos);
            return InteractionResult.CONSUME;
        }
        throw new IllegalStateException("Our Container provider is missing!");
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new ElementalExtractorTile(pPos, pState);
    }

    @Override
    public void onRemove(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof ElementalExtractorTile extractorTile) {
                extractorTile.drops();
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide ? null : createTickerHelper(pBlockEntityType, ModTileEntities.ELEMENTALEXTRACTORTILE.get(), ElementalExtractorTile::tick);
    }

    public void animateTick(BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull Random pRand) {
        if (!pState.getValue(LIT)) {
            return;
        }

        double d0 = pPos.getX() + 0.5D;
        double d1 = pPos.getY();
        double d2 = pPos.getZ() + 0.5D;

        if (pRand.nextDouble() < 0.1D) {
            pLevel.playLocalSound(d0, d1, d2, SoundEvents.SOUL_ESCAPE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
        }

        pLevel.addParticle(ParticleTypes.SOUL_FIRE_FLAME, d0, d1, d2, 0.0D, 0.2D, 0.0D);
        pLevel.addParticle(ParticleTypes.SOUL, d0, d1 + 0.5D, d2, 0.0D, 0.2D, 0.0D);
    }
}