package com.itskillerluc.alchemicalbrewery.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModCreativeTab {
    public static final CreativeModeTab ALCHEMICALBREWERY_TAB = new CreativeModeTab("alchemicalbrewerytab") {
        @Override
        //TODO: change the icon of the creative tab to a permenant one instead of the temperary one. Can be done once the main item is implemented.
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.ACID.get());
        }
    };
}
