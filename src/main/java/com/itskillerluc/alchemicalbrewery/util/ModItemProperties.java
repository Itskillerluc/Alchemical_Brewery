package com.itskillerluc.alchemicalbrewery.util;

import com.itskillerluc.alchemicalbrewery.item.ModItems;
import com.itskillerluc.alchemicalbrewery.item.custom.WandItem;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ModItemProperties {
    public static void addCustomItemProperties(){
        makeWand(ModItems.WAND_ITEM.get());
    }

    /**
     * assigns the model depending on if there's an element loaded
     * @param item item class
     */
    private static void makeWand(Item item){
        ItemProperties.register(item, new ResourceLocation("loaded"), (p_174625_, p_174626_, p_174627_, p_174628_) -> (WandItem.hasElement(p_174625_) ? 1.0f : 0.0f));
    }
}
