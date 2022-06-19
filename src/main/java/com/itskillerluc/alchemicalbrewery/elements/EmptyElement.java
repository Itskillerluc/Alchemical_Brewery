package com.itskillerluc.alchemicalbrewery.elements;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class EmptyElement extends Element{
    public EmptyElement(String Displayname) {
        super(Displayname);
    }

    public EmptyElement(EmptyElement element) {
        super(element);
    }

    @Override
    public EmptyElement instanciate() {
        return new EmptyElement(this);
    }

    @Override
    void elementFunction(Direction dir, BlockPos pos, Level level, LivingEntity user, InteractionHand hand, boolean consume) {}
}
