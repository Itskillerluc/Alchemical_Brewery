package com.itskillerluc.alchemicalbrewery.util;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;

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
}