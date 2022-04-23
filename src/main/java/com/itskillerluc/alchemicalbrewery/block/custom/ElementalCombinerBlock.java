package com.itskillerluc.alchemicalbrewery.block.custom;

import com.itskillerluc.alchemicalbrewery.tileentity.ElementalCombinerTile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextComponent;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class ElementalCombinerBlock extends BaseEntityBlock {

    public ElementalCombinerBlock(Properties p_49224_) {
        super(p_49224_);
    }

    private static final VoxelShape Model = Stream.of(
            Block.box(0, 0, 0, 16, 5.25, 16)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return Model;
    }

    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }


    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos,
                                 Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        double d0 = (double)pPos.getX();
        double d1 = (double)pPos.getY();
        double d2 = (double)pPos.getZ();
        if (!pLevel.isClientSide()) {
            BlockEntity tileEntity = pLevel.getBlockEntity(pPos);
            //test what method should be run
            if(pPlayer.isCrouching()&&((ElementalCombinerTile)tileEntity).hasRecipe((ElementalCombinerTile) tileEntity)){
                ((ElementalCombinerTile)tileEntity).craftItem((ElementalCombinerTile) tileEntity);
            }else if(pPlayer.isCrouching()&&!((ElementalCombinerTile)tileEntity).hasRecipe((ElementalCombinerTile)tileEntity)) {
                pPlayer.sendMessage(new TextComponent("Invalid Recipe"), pPlayer.getUUID());
            }else if(!pPlayer.getOffhandItem().isEmpty()) {
                ((ElementalCombinerTile) tileEntity).getItems(pPlayer, ((ElementalCombinerTile) tileEntity));
                pLevel.playLocalSound(d0, d1, d2, SoundEvents.ANVIL_FALL, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }else{
                if(pPlayer.getMainHandItem().isEmpty()){
                    ((ElementalCombinerTile) tileEntity).extractItem(pPlayer, ((ElementalCombinerTile) tileEntity));
                    pLevel.playLocalSound(d0, d1, d2, SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                }else if(!pPlayer.getMainHandItem().isEmpty()) {
                    ((ElementalCombinerTile) tileEntity).insertItem(pPlayer.getMainHandItem().getItem(), pPlayer.getMainHandItem().getCount(), pPlayer.getMainHandItem().getTag(), ((ElementalCombinerTile) tileEntity), pPlayer);
                    pLevel.playLocalSound(d0, d1, d2, SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                }
            }
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ElementalCombinerTile(pPos, pState);
    }
    //drop the items when broken
    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof ElementalCombinerTile) {
                ((ElementalCombinerTile) blockEntity).drops();
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }
}