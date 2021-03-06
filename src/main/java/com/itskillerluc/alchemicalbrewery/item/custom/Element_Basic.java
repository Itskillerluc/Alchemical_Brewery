package com.itskillerluc.alchemicalbrewery.item.custom;

import com.itskillerluc.alchemicalbrewery.item.ModItems;
import com.itskillerluc.alchemicalbrewery.util.Utilities;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class Element_Basic extends Item {
    public Element_Basic(Properties pProperties) {
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
        return pStack.hasTag() ? new TranslatableComponent(getDescriptionId(), "\u00A7a("+Name+")") : new TranslatableComponent("item.alchemicalbrewery.element_basic");
    }


    /**
     * converts element_basic into element_use
     * @param entity the item that should be converted
     * @param level the level that the item is in
     */
    public static void convert(ItemEntity entity, Level level){
        level.addFreshEntity(new ItemEntity(level, entity.getBlockX(), 255, entity.getBlockZ(), Utilities.DecodeStackTags(new ItemStack(ModItems.ELEMENT_USE.get(), entity.getItem().getCount(), entity.getItem().getTag()))));
    }


    /**
     * Sets the color to the nbt that is provided
     */
    public static class ColorHandler implements ItemColor{
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
