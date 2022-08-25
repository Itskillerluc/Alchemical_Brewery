package com.itskillerluc.alchemicalbrewery.util;
//TODO
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Function4;
import net.minecraft.client.gui.Font;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class Utilities {
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    /**
     * @param itemstack Itemstack targeted
     * @return return the new itemstack with deserialized nbt (its deserialzied into normal tags from ForgeCaps)
     */
    public static ItemStack DecodeStackTags(ItemStack itemstack){
        ItemStack result = new ItemStack(itemstack.getItem(), itemstack.getCount());
        itemstack.serializeNBT().getCompound("ForgeCaps").getAllKeys().forEach((ele)->
                result.getOrCreateTag().put(ele, Objects.requireNonNull(itemstack.serializeNBT().getCompound("ForgeCaps").get(ele))));
        return result;
    }

    public static int drawStringNoShadow(Font pFont, PoseStack pPoseStack, String pText, float pX, float pY, int pColor){
        return pFont.draw(pPoseStack, pText, (float)pX, (float)pY, pColor);
    }

    @NotNull
    public static ItemStack moveStack(Player playerIn, int index, AbstractContainerMenu menu, Function4<ItemStack, Integer, Integer, Boolean, Boolean> moveItemStack, int inventorySlotCount) {
        Slot sourceSlot = menu.slots.get(index);
        if (!sourceSlot.hasItem()) return ItemStack.EMPTY;
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();
        if (index < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            if (!moveItemStack.apply(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX + inventorySlotCount, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < TE_INVENTORY_FIRST_SLOT_INDEX + inventorySlotCount) {
            if (!moveItemStack.apply(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + index);
            return ItemStack.EMPTY;
        }
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }
}