package com.itskillerluc.alchemicalbrewery.block.custom;
//TODO
import com.itskillerluc.alchemicalbrewery.container.ElementalExtractorContainer;
import com.itskillerluc.alchemicalbrewery.tileentity.ElementalExtractorTile;
import com.itskillerluc.alchemicalbrewery.tileentity.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
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
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.stream.Stream;
public class ElementalExtractorBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;


    public ElementalExtractorBlock(Properties p_49224_) {
        super(p_49224_);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH).setValue(LIT, Boolean.valueOf(false)));
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
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        switch (pState.getValue(FACING)) {
            case NORTH:
                return SHAPE_N;
            case SOUTH:
                return SHAPE_S;
            case WEST:
                return SHAPE_W;
            case EAST:
                return SHAPE_E;
            default:
                return SHAPE_N;
        }
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection().getOpposite());
    }
    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
    }
    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING, LIT);
    }
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos,
                                 Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide()) {
            BlockEntity entity = pLevel.getBlockEntity(pPos);
            if(entity instanceof ElementalExtractorTile) {
                NetworkHooks.openGui(((ServerPlayer)pPlayer), (ElementalExtractorTile)entity, pPos);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ElementalExtractorTile(pPos, pState);
    }
    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof ElementalExtractorTile) {
                ((ElementalExtractorTile) blockEntity).drops();
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }

    //create cosmetic particals and sound
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide ? null : createTickerHelper(pBlockEntityType, ModTileEntities.ELEMENTALEXTRACTORTILE.get(), ElementalExtractorTile::tick);
    }

    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, Random pRand) {
        if (pState.getValue(LIT)) {
            double d0 = (double)pPos.getX() + 0.5D;
            double d1 = (double)pPos.getY();
            double d2 = (double)pPos.getZ() + 0.5D;
            if (pRand.nextDouble() < 0.1D) {
                pLevel.playLocalSound(d0, d1, d2, SoundEvents.SOUL_ESCAPE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }

            Direction direction = pState.getValue(FACING);
            Direction.Axis direction$axis = direction.getAxis();
            double d3 = 0.52D;
            double d4 = pRand.nextDouble() * 0.6D - 0.3D;
            double d5 = direction$axis == Direction.Axis.X ? (double)direction.getStepX() * 0.52D : d4;
            double d6 = pRand.nextDouble() * 6.0D / 16.0D;
            double d7 = direction$axis == Direction.Axis.Z ? (double)direction.getStepZ() * 0.52D : d4;
            pLevel.addParticle(ParticleTypes.SOUL_FIRE_FLAME, d0, d1, d2, 0.0D, 0.2D, 0.0D);
            pLevel.addParticle(ParticleTypes.SOUL, d0, d1+0.5D, d2, 0.0D, 0.2D, 0.0D);
        }
    }
}