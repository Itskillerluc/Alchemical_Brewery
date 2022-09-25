package com.itskillerluc.alchemicalbrewery.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ModCreativeTab {
    @SuppressWarnings("SpellCheckingInspection")
    public static final CreativeModeTab ALCHEMICALBREWERY_TAB = new CreativeModeTab("alchemicalbrewerytab") {
        @Override
        public @NotNull ItemStack makeIcon() {
            return new ItemStack(ModItems.CAPSULE_LARGE.get());
        }
    };
}
