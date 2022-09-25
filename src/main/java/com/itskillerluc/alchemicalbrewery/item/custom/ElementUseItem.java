package com.itskillerluc.alchemicalbrewery.item.custom;

import com.itskillerluc.alchemicalbrewery.elements.Element;
import com.itskillerluc.alchemicalbrewery.elements.ElementData;
import com.itskillerluc.alchemicalbrewery.elements.ModElements;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

public class ElementUseItem extends ElementBasic {
    public static final Logger LOGGER = LogManager.getLogger();
    public ElementUseItem(Properties pProperties) {
        super(pProperties);
    }

    /**
     * create a dynamic name
     */
    @Override
    public @NotNull Component getName(@NotNull ItemStack pStack) {
        String name = ElementCrafting.getDynamicName(pStack);
        return name != null ? new TranslatableComponent(getDescriptionId(), "\u00A7a(" + name + ")") : new TranslatableComponent("item.alchemicalbrewery.element_use");
    }

    @Override
    public @NotNull Rarity getRarity(@NotNull ItemStack pStack) {
        if (pStack.getTag() != null) {
            return pStack.getTag().getBoolean("Creative") ? Rarity.EPIC : Rarity.COMMON;
        }
        return Rarity.COMMON;
    }
    @Override
    public @NotNull InteractionResult useOn(UseOnContext pContext) {
        ElementData element;
        CompoundTag itemTag = pContext.getItemInHand().getTag();
        Element empty = ModElements.EMPTY.get();

        if (itemTag != null) {
            IForgeRegistry<Element> elementsRegistry = ModElements.ELEMENTS.get();
            CompoundTag elementCompound = itemTag.getCompound("element");
            Element value = null;
            try {
                 value = elementsRegistry.getValue(ResourceLocation.tryParse(elementCompound.getString("type")));
            }catch (ResourceLocationException exception){
                LOGGER.error(exception.getMessage());
            }

            if (value != null) {
                element = value.fromTagSafe(elementCompound);
            } else {
                LOGGER.error(elementCompound.getString("type") + " is not a valid Element type");
                element = empty.fromTagSafe(elementCompound);
            }
        } else {
            LOGGER.error("No Element type was found.");
            element = new ElementData(empty);
        }

        Player player = pContext.getPlayer();
        if (player == null || player.isCrouching()) {
            return InteractionResult.FAIL;
        }
        if (element != null && element.elementType != empty) {
            element.run(pContext, (itemTag != null && itemTag.contains("Creative") && !itemTag.getBoolean("Creative")) || !pContext.getPlayer().isCreative());
            return InteractionResult.SUCCESS;
        }
        LOGGER.error("there is no valid Element that can be used");
        return InteractionResult.FAIL;
    }

    @Override
    public boolean isFoil(@NotNull ItemStack pStack) {
        return true;
    }

    /**
     * Sets the color to the nbt that is provided
     */
    public static class ColorHandler implements ItemColor {
        @Override
        public int getColor(@NotNull ItemStack pStack, int pTintIndex) {
            return ElementCrafting.ColorHandler.getDynamicColor(pStack, pTintIndex);
        }
    }
}
