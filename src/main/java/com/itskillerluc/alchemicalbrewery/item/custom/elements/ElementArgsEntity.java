package com.itskillerluc.alchemicalbrewery.item.custom.elements;

import com.itskillerluc.alchemicalbrewery.entity.custom.ElementProjectileEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface ElementArgsEntity {
    String[] arg(Level level, LivingEntity owner, ElementProjectileEntity entity);
}
