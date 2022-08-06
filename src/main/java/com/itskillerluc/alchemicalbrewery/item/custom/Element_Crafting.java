package com.itskillerluc.alchemicalbrewery.item.custom;

import com.itskillerluc.alchemicalbrewery.elements.Element;
import com.itskillerluc.alchemicalbrewery.elements.ModElements;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class Element_Crafting extends Element_Basic{
    public Element_Crafting(Properties pProperties) {
        super(pProperties);
    }

    /**
     * create a dynamic name
     */
    @Override
    public @NotNull Component getName(ItemStack pStack) {
        String name = null;
        if (pStack.getTag() != null) {
            name = pStack.getTag().getCompound("element").getString("displayName");
            Element elementType = ModElements.ELEMENTS.get().getValue(ResourceLocation.tryParse(pStack.getTag().getCompound("element").getString("type")));
            if (name.equals("") && elementType != null) {
                name = elementType.defaultDisplayName;
            }
        }
        return name != null ? new TranslatableComponent(getDescriptionId(), "\u00A7a(" + name + ")") : new TranslatableComponent("item.alchemicalbrewery.element_crafting");
    }

    public static class ColorHandler implements ItemColor {
        @Override
        public int getColor(ItemStack pStack, int pTintIndex) {
            if (pStack.getTag() != null) {
                final CompoundTag element = pStack.getTag().getCompound("element");
                final Element elementType = ModElements.ELEMENTS.get().getValue(new ResourceLocation(element.getString("type")));
                switch (pTintIndex) {
                    case 0 -> {
                        if (!element.contains("secColor")) {
                            if (elementType != null) {
                                return elementType.defaultSecColor;
                            } else {
                                return 15869935;
                            }
                        } else {
                            return element.getInt("secColor");
                        }
                    }
                    case 1 -> {
                        if (!element.contains("color")) {
                            if (elementType != null) {
                                return elementType.defaultColor;
                            } else {
                                return -1;
                            }
                        } else {
                            return element.getInt("color");
                        }
                    }
                    default -> {
                        return 15869935;
                    }
                }
            } else {
                return pTintIndex == 0 ? 15869935 : -1;
            }
        }
    }
}
