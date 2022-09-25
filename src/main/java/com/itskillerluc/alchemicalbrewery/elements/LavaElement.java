package com.itskillerluc.alchemicalbrewery.elements;

import com.itskillerluc.alchemicalbrewery.util.Utilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

import java.util.Objects;

public class LavaElement extends Element{

    public LavaElement(String displayName) {
        super(displayName, null, null, 16734006, 16759851);
    }

    void elementFunction(Direction dir, BlockPos pos, Level level, LivingEntity user, InteractionHand hand, boolean consume, CompoundTag extraData) {
        BlockPos newPos = pos.relative(dir);
        if (level.isClientSide() || !level.getBlockState(newPos).getMaterial().isReplaceable()) {
            return;
        }
        level.setBlock(newPos, Blocks.LAVA.defaultBlockState(), 2);
        bucketFunction(user, hand, consume);
    }

    static void bucketFunction(LivingEntity user, InteractionHand hand, boolean consume) {
        if (user != null && consume && user.getItemInHand(hand).hasTag() && !Objects.requireNonNull(user.getItemInHand(hand).getTag()).getBoolean("Creative")) {
            user.setItemInHand(hand, Utilities.DecodeStackTags(new ItemStack(user.getItemInHand(hand).getItem(), user.getItemInHand(hand).getCount() - 1, user.getItemInHand(hand).getTag())));
        }
    }
}
