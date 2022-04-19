package com.itskillerluc.alchemicalbrewery.item.custom.elements;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public interface ElementArgs {
    String[] arg(UseOnContext pContext);
}
