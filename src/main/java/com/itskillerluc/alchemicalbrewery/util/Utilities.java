package com.itskillerluc.alchemicalbrewery.util;
//TODO
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class Utilities {
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
}