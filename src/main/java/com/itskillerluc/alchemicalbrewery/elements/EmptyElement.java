package com.itskillerluc.alchemicalbrewery.elements;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

@SuppressWarnings("SpellCheckingInspection")
public class EmptyElement extends Element{


    public EmptyElement(String Displayname) {
        super(Displayname, null, null, 0);
    }

    @Override
    void elementFunction(Direction dir, BlockPos pos, Level level, LivingEntity user, InteractionHand hand, boolean consume, CompoundTag extraData) {}
}
