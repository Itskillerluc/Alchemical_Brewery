package com.itskillerluc.alchemicalbrewery.block.custom;

import com.itskillerluc.alchemicalbrewery.tileentity.ElementalExtractorTile;
import com.itskillerluc.alchemicalbrewery.tileentity.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class ElementalExtractorBlock extends BaseEntityBlock {


    protected ElementalExtractorBlock(Properties p_49224_) {
        super(p_49224_);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if(!pLevel.isClientSide()){
            BlockEntity tileEntity = pLevel.getBlockEntity(pPos);

            if(tileEntity instanceof ElementalExtractorTile){
                MenuProvider menuProvider = createContainerProvider(pLevel, pPos);
            }
        }

        return InteractionResult.SUCCESS;
    }

    private MenuProvider createContainerProvider(Level pLevel, BlockPos pPos) {
        return new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return new TranslatableComponent("screen.alchemicalbrewery.elementalextractor");
            }

            @Nullable
            @Override
            public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
                return null;
            }
        };
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return ModTileEntities.ELEMENTALEXTRACTORTILE.get().create(pPos, pState);
    }
    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pState.getBlock() != pNewState.getBlock()) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pPos);
            if (blockEntity instanceof ElementalExtractorTile) {
                ((ElementalExtractorTile) blockEntity).drops();
            }
        }
    }


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, ModTileEntities.ELEMENTALEXTRACTORTILE.get(),ElementalExtractorTile::tick);
    }


}
