package com.itskillerluc.alchemicalbrewery.block.custom;

import com.itskillerluc.alchemicalbrewery.tileentity.ElementalCombinerTile;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("deprecation")
public class ElementalCombinerBlock extends BaseEntityBlock {
    public ElementalCombinerBlock(Properties properties) {
        super(properties);
    }

    private static final VoxelShape Model = java.util.Optional.of(Block.box(0, 0, 0, 16, 5.25, 16)).get();

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        return Model;
    }

    public @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState pState, Level pLevel, BlockPos pPos, @NotNull Player pPlayer, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHit) {
        double d0 = pPos.getX();
        double d1 = pPos.getY();
        double d2 = pPos.getZ();

        BlockEntity tileEntity = pLevel.getBlockEntity(pPos);

        if (!(tileEntity instanceof ElementalCombinerTile elementalCombinerTile)) {
            return InteractionResult.PASS;
        }

        if (pPlayer.isCrouching()) {
            if (ElementalCombinerTile.hasRecipe(elementalCombinerTile)) {
                //if recipe is correct, craft the item
                ElementalCombinerTile.craftItem(elementalCombinerTile);
                pLevel.playLocalSound(d0, d1, d2, SoundEvents.ANVIL_FALL, SoundSource.BLOCKS, 1.0F, 1.0F, false);
                return InteractionResult.sidedSuccess(pLevel.isClientSide());
            }
            //if recipe is incorrect send message
            if (pLevel.isClientSide) {
                pPlayer.sendMessage(new TextComponent("Invalid Recipe"), pPlayer.getUUID());
            }
            return InteractionResult.sidedSuccess(pLevel.isClientSide());
        }

        if (!pPlayer.getOffhandItem().isEmpty()) {
            //if player has item in offhand print a list of items inside the block
            elementalCombinerTile.getItems(pPlayer, elementalCombinerTile);
            return InteractionResult.sidedSuccess(pLevel.isClientSide());
        }

        if (pPlayer.getMainHandItem().isEmpty()) {
            //if the main hand is empty take item out
            elementalCombinerTile.extractItem(pPlayer, elementalCombinerTile);
            pLevel.playLocalSound(d0, d1, d2, SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            return InteractionResult.sidedSuccess(pLevel.isClientSide());
        }
        //if there is an item in your main hand put it inside
        elementalCombinerTile.insertItem(pPlayer.getMainHandItem().getItem(), pPlayer.getMainHandItem().getCount(), pPlayer.getMainHandItem().getTag(), elementalCombinerTile, pPlayer);
        pLevel.playLocalSound(d0, d1, d2, SoundEvents.BEEHIVE_ENTER, SoundSource.BLOCKS, 1.0F, 1.0F, false);

        return InteractionResult.sidedSuccess(pLevel.isClientSide());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new ElementalCombinerTile(pPos, pState);
    }

    @Override
    public void onRemove(@NotNull BlockState pState, @NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof ElementalCombinerTile combinerTile) {
                combinerTile.drops();
            }
            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }
}