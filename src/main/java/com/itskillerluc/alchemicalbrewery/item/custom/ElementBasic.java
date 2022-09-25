package com.itskillerluc.alchemicalbrewery.item.custom;

import com.itskillerluc.alchemicalbrewery.elements.Element;
import com.itskillerluc.alchemicalbrewery.elements.ElementData;
import com.itskillerluc.alchemicalbrewery.elements.ModElements;
import com.itskillerluc.alchemicalbrewery.item.ModItems;
import com.itskillerluc.alchemicalbrewery.util.Utilities;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import static com.itskillerluc.alchemicalbrewery.item.custom.ElementCrafting.ColorHandler.getDynamicColor;

public class ElementBasic extends Item {
    public ElementBasic(Properties pProperties) {
        super(pProperties);
    }

    /**
     * create a dynamic name
     */
    @Override
    public @NotNull Component getName(@NotNull ItemStack pStack) {
        String name = ElementCrafting.getDynamicName(pStack);
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
        if ((entity.getItem().getTag() == null ||
                ModElements.ELEMENTS.get().getValue(new ResourceLocation(entity.getItem().getTag().getCompound("element").getString("type"))) == null ||
                getElement(entity.getItem()).matches(new ElementData(ModElements.EMPTY.get()), new ElementData(getElement(entity.getItem())))) ||
                getElement(entity.getItem()).matches(new ElementData(ModElements.ITEM.get()), new ElementData(getElement(entity.getItem())))) {
            return;
        }
        level.addFreshEntity(new ItemEntity(level, entity.getBlockX(), 255, entity.getBlockZ(), Utilities.DecodeStackTags(new ItemStack(ModItems.ELEMENT_USE.get(), entity.getItem().getCount(), entity.getItem().getTag()))));
    }

    /**
     * Sets the color to the nbt that is provided
     */
    public static class ColorHandler implements ItemColor {
        @Override
        public int getColor(@NotNull ItemStack pStack, int pTintIndex) {
            return getDynamicColor(pStack, pTintIndex);
        }
    }
}
