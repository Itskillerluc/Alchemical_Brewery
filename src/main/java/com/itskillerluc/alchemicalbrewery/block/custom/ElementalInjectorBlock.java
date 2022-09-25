package com.itskillerluc.alchemicalbrewery.block.custom;

import com.itskillerluc.alchemicalbrewery.tileentity.ElementalInjectorTile;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

import static com.itskillerluc.alchemicalbrewery.block.custom.ElementalExtractorBlock.getVoxelShape;

@SuppressWarnings("deprecation")
public class ElementalInjectorBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    private static final VoxelShape SHAPE_N = java.util.Optional.of(Block.box(0, 0, 5.7, 15.25, 2.75, 10.25)).get();
    private static final VoxelShape SHAPE_S = java.util.Optional.of(Block.box(0, 0, 5.7, 15.25, 2.75, 10.25)).get();
    private static final VoxelShape SHAPE_E = java.util.Optional.of(Block.box(5.350000000000001, 0, 0.34999999999999964, 9.9, 2.75, 15.6)).get();
    private static final VoxelShape SHAPE_W = java.util.Optional.of(Block.box(5.350000000000001, 0, 0.34999999999999964, 9.9, 2.75, 15.6)).get();

    public ElementalInjectorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(LIT, Boolean.FALSE));
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return getVoxelShape(pState, FACING, SHAPE_N, SHAPE_S, SHAPE_W, SHAPE_E);
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
            return InteractionResult.sidedSuccess(pLevel.isClientSide());
        }

        BlockEntity entity = pLevel.getBlockEntity(pPos);

        if(entity instanceof ElementalInjectorTile injectorTile) {
            NetworkHooks.openGui(((ServerPlayer)pPlayer), injectorTile, pPos);
            return InteractionResult.CONSUME;
        }
        throw new IllegalStateException("Our Container provider is missing!");
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new ElementalInjectorTile(pPos, pState);
    }

    @Override
    public void onRemove(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof ElementalInjectorTile injectorTile) {
                injectorTile.drops();
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide ? null : createTickerHelper(pBlockEntityType, ModTileEntities.ELEMENTAL_INJECTOR_TILE.get(), ElementalInjectorTile::tick);
    }

    public void animateTick(BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull Random pRand) {
        if (pState.getValue(LIT)) {
            double d0 = pPos.getX() + 0.5D;
            double d1 = pPos.getY();
            double d2 = pPos.getZ() + 0.5D;
            if (!(pRand.nextDouble() < 0.1D)) {
                return;
            }
            pLevel.playLocalSound(d0, d1, d2, SoundEvents.SLIME_HURT_SMALL, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            pLevel.addParticle(ParticleTypes.DRIPPING_HONEY, d0, d1, d2, 0.0D, -0.01D, 0.0D);
            pLevel.addParticle(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, d0, d1, d2, 0.0D, -0.01D, 0.0D);
        }
    }
}