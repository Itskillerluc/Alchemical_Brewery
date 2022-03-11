package com.itskillerluc.alchemicalbrewery.item.custom;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class Element_Basic extends Item {
    public Element_Basic(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Component getName(ItemStack pStack) {
        return pStack.hasTag() ? new TranslatableComponent(getDescriptionId(), "\u00A7a(" + pStack.getTag().getString("Element") + ")") : new TranslatableComponent("item.alchemicalbrewery.element_basic");
    }

    public static class ColorHandler implements ItemColor{
        CompoundTag tag = new CompoundTag();

        @Override
        public int getColor(ItemStack pStack, int pTintIndex) {
            return pStack.hasTag() ? pStack.getTag().getInt("ItemColor") : -1;
        }
    }
}
