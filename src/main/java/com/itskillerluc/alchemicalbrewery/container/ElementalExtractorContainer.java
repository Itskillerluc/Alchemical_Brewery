package com.itskillerluc.alchemicalbrewery.container;

import com.itskillerluc.alchemicalbrewery.block.ModBlocks;
import com.itskillerluc.alchemicalbrewery.container.slot.ModResultSlot;
import com.itskillerluc.alchemicalbrewery.tileentity.ElementalExtractorTile;
import com.itskillerluc.alchemicalbrewery.util.Utilities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SpellCheckingInspection")
public class ElementalExtractorContainer extends AbstractContainerMenu {
    private final ElementalExtractorTile tileEntity;
    private final ContainerData data;
    private final Level level;

    public ElementalExtractorContainer(int windowId, Inventory inv, FriendlyByteBuf extraData) {
        this(windowId, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(3));
    }

    public ElementalExtractorContainer(int windowId, Inventory playerInventory, BlockEntity entity, ContainerData data) {
        super(ModContainers.ELEMENTALEXTRACTORCONTAINER.get(), windowId);

        checkContainerSize(playerInventory, 4);
        this.tileEntity = (ElementalExtractorTile) entity;
        this.level = playerInventory.player.level;
        this.data = data;

        addPlayerHotbar(playerInventory);
        addPlayerInventory(playerInventory);

        tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
            addSlot(new SlotItemHandler(handler, 0, 18, 34));
            addSlot(new SlotItemHandler(handler, 1, 69, 34));
            addSlot(new SlotItemHandler(handler, 2, 69, 66));
            addSlot(new ModResultSlot(handler, 3, 136, 34));
        });
        this.addDataSlots(data);
    }

    public boolean isBurning(){
        return data.get(0) != 0;
    }

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level,tileEntity.getBlockPos()), pPlayer, ModBlocks.ELEMENTALEXTRACTOR.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 9 + l * 18, 86 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 9 + i * 18, 144));
        }
    }

    public int getScaledProgress() {
        int progress = this.data.get(1);
        int maxProgress = this.data.get(2);
        int progressArrowSize = 14;

        return (maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0);
    }

    public int offset(){
         return 14-getScaledProgress();
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        return Utilities.moveStack(playerIn, index, this, this::moveItemStack, 4);
    }

    boolean moveItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection){
        return moveItemStackTo(stack, startIndex, endIndex, reverseDirection);
    }
}