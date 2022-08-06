package com.itskillerluc.alchemicalbrewery.item.custom;

import com.itskillerluc.alchemicalbrewery.elements.Element;
import com.itskillerluc.alchemicalbrewery.elements.ElementData;
import com.itskillerluc.alchemicalbrewery.elements.ModElements;
import com.itskillerluc.alchemicalbrewery.item.ModItems;
import com.itskillerluc.alchemicalbrewery.util.Utilities;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.ColorHandlerEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Element_Basic extends Item {
    public Element_Basic(Properties pProperties) {
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
        return (name != null) ? new TranslatableComponent(getDescriptionId(), "\u00A7a("+name+")") : new TranslatableComponent("item.alchemicalbrewery.element_basic");
    }
    public static Element getElement(ItemStack stack){
        return stack.getTag() != null ? ModElements.ELEMENTS.get().getValue(ResourceLocation.tryParse(stack.getTag().getCompound("element").getString("type"))) : ModElements.EMPTY.get();
    }

    public static ItemStack fromData(ElementData data){
        ItemStack stack = new ItemStack(ModItems.ELEMENT_BASIC.get(), 1);
        stack.getOrCreateTag().put("element", data.toTag());
        return stack;
    }

    /**
     * converts element_basic into element_use
     * @param entity the item that should be converted
     * @param level the level that the item is in
     */
    public static void convert(ItemEntity entity, Level level){
        if(entity.getItem().getTag() != null &&ModElements.ELEMENTS.get().getValue(new ResourceLocation(entity.getItem().getTag().getCompound("element").getString("type"))) != null && !getElement(entity.getItem()).matches(new ElementData(ModElements.EMPTY.get()), new ElementData(getElement(entity.getItem()))) || !getElement(entity.getItem()).matches(new ElementData(ModElements.ITEM
                .get()), new ElementData(getElement(entity.getItem())))) {
            level.addFreshEntity(new ItemEntity(level, entity.getBlockX(), 255, entity.getBlockZ(), Utilities.DecodeStackTags(new ItemStack(ModItems.ELEMENT_USE.get(), entity.getItem().getCount(), entity.getItem().getTag()))));
        }
    }


    /**
     * Sets the color to the nbt that is provided
     */
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
