package com.itskillerluc.alchemicalbrewery.util;

import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ModTags {
    public static class Items {
        public static final TagKey<Item> EXTRACTION_BLOCK_EXCEPTIONS = tag("extract_exceptions/block");
        public static final TagKey<Item> EXTRACTION_ITEM_EXCEPTIONS = tag("extract_exceptions/item");

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(new ResourceLocation(AlchemicalBrewery.MOD_ID, name));
        }
    }
}
