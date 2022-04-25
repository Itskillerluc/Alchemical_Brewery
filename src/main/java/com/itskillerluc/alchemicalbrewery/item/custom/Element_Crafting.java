package com.itskillerluc.alchemicalbrewery.item.custom;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;

public class Element_Crafting extends Element_Basic{
    public Element_Crafting(Properties pProperties) {
        super(pProperties);
    }

    /**
     * create a dynamic name
     */
    @Override
    public Component getName(ItemStack pStack) {
        String Name = null;
        if(pStack.hasTag()){
            String Element = pStack.getTag().getString("Element");
            Name = Element;
            if (Element != null) {
                if (Element.contains("-")) {
                    Name = Element.substring(0, Element.indexOf('-'));
                }
            }
        }
        return pStack.hasTag() ? new TranslatableComponent(getDescriptionId(), "\u00A7a(" + Name + ")") : new TranslatableComponent("item.alchemicalbrewery.element_crafting");
    }

    public static class ColorHandler implements ItemColor {
        @Override
        public int getColor(ItemStack pStack, int pTintIndex) {
            if(pStack.hasTag()){
                switch (pTintIndex){
                    case 0 -> {
                        assert pStack.getTag() != null;
                        return pStack.getTag().getInt("SecItemColor");}
                    case 1 -> {
                        assert pStack.getTag() != null;
                        return pStack.getTag().getInt("ItemColor");}
                    case 2 ->{
                        return -1;
                    }
                    default -> {return 15869935;}
                }
            }else{
                if (pTintIndex == 0) {
                    return 15869935;
                }else return -1;
            }
        }
    }
}
