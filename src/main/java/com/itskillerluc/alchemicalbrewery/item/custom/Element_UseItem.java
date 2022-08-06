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
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;


public class Element_UseItem extends Element_Basic {

    public static final Logger LOGGER = LogManager.getLogger();

    public Element_UseItem(Properties pProperties) {
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
        return name != null ? new TranslatableComponent(getDescriptionId(), "\u00A7a(" + name + ")") : new TranslatableComponent("item.alchemicalbrewery.element_use");
    }

    @Override
    public @NotNull Rarity getRarity(@NotNull ItemStack pStack) {
        if (pStack.getTag() != null) {
            return pStack.getTag().getBoolean("Creative") ? Rarity.EPIC : Rarity.COMMON;
        } else {
            return Rarity.COMMON;
        }
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
                element = value.fromTag(elementCompound);

            } else {
                LOGGER.error(elementCompound.getString("type") + " is not a valid Element type");
                element = empty.fromTag(elementCompound);
            }

        } else {
            LOGGER.error("No Element type was found.");
            element = new ElementData(empty);
        }

        Player player = pContext.getPlayer();
        if (player != null && !player.isCrouching()) {
            if (element != null && element.elementType != empty) {
                element.run(pContext, (itemTag != null && itemTag.contains("Creative") && !itemTag.getBoolean("Creative")) || !pContext.getPlayer().isCreative());
                return InteractionResult.SUCCESS;
            } else {
                LOGGER.error("there is no valid Element that can be used");
                return InteractionResult.FAIL;
            }
        }
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
        public int getColor(ItemStack pStack, int pTintIndex) {
            if (pStack.getTag() != null) {
                final CompoundTag element = pStack.getTag().getCompound("element");
                Element elementType = null;
                try{
                    elementType = ModElements.ELEMENTS.get().getValue(new ResourceLocation(element.getString("type")));
                }catch (ResourceLocationException exception){
                    LOGGER.error(exception.getMessage());
                }
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
