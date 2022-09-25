package com.itskillerluc.alchemicalbrewery.elements;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class WaterElement extends Element{

    public WaterElement(String displayName) {
        super(displayName, null, null, 6525687, 6525687);
    }

    void elementFunction(Direction dir, BlockPos pos, Level level, LivingEntity user, InteractionHand hand, boolean consume, CompoundTag extraData) {
        BlockPos newPos = pos.relative(dir);
        if (level.isClientSide() || !level.getBlockState(newPos).getMaterial().isReplaceable()) {
            return;
        }
        level.setBlock(newPos, Blocks.WATER.defaultBlockState(), 2);
        LavaElement.bucketFunction(user, hand, consume);
    }
}
