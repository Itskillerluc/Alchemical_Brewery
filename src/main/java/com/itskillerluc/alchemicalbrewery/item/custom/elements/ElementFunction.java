package com.itskillerluc.alchemicalbrewery.item.custom.elements;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public interface ElementFunction{
    void run(Direction dir, BlockPos pos, Level level, LivingEntity user, InteractionHand hand, boolean consume, String[] args);
}
