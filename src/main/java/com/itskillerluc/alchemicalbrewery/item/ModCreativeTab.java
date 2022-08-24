package com.itskillerluc.alchemicalbrewery.item;
//TODO
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class ModCreativeTab {
    public static final CreativeModeTab ALCHEMICALBREWERY_TAB = new CreativeModeTab("alchemicalbrewerytab") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.CAPSULE_LARGE.get());
        }
    };
}
