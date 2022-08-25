package com.itskillerluc.alchemicalbrewery.container;

import com.itskillerluc.alchemicalbrewery.block.ModBlocks;
import com.itskillerluc.alchemicalbrewery.container.slot.ModResultSlot;
import com.itskillerluc.alchemicalbrewery.tileentity.ElementalInjectorTile;
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
public class ElementalInjectorContainer extends AbstractContainerMenu {
    private final ElementalInjectorTile tileEntity;
    private final ContainerData data;
    private final Level level;

    public ElementalInjectorContainer(int windowId, Inventory inv, FriendlyByteBuf extraData) {
        this(windowId, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(4));
    }

    public ElementalInjectorContainer(int windowId, Inventory playerInventory, BlockEntity entity, ContainerData data) {
        super(ModContainers.ELEMENTALINJECTORCONTAINER.get(), windowId);
        checkContainerSize(playerInventory, 3);
        this.tileEntity = ((ElementalInjectorTile) entity);
        this.level = playerInventory.player.level;
        this.data = data;

        addPlayerHotbar(playerInventory);
        addPlayerInventory(playerInventory);

        tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
            this.addSlot(new SlotItemHandler(handler, 0, 11, 35));
            this.addSlot(new SlotItemHandler(handler, 1, 55, 35));
            this.addSlot(new ModResultSlot(handler, 2, 144, 35));
        });
        this.addDataSlots(data);
    }

    public int getCharge(){return data.get(3);}

    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level,tileEntity.getBlockPos()), pPlayer, ModBlocks.ELEMENTALINJECTOR.get());
    }

    public int getScaledProgress() {
        int progress = this.data.get(1);
        int maxProgress = this.data.get(2);
        int progressArrowSize = 15;

        return (maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0);
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player playerIn, int index) {
        return Utilities.moveStack(playerIn, index, this, this::moveItemStack, 3);
    }

    boolean moveItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection){
        return moveItemStackTo(stack, startIndex, endIndex, reverseDirection);
    }
}