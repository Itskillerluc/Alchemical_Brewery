package com.itskillerluc.alchemicalbrewery.elements;

import com.itskillerluc.alchemicalbrewery.util.Utilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class EnderElement extends Element{

    public EnderElement(String displayName) {
        super(displayName, null, new ItemStack(Items.ENDER_PEARL), 2458227, 2458227);
    }

    void elementFunction(Direction dir, BlockPos pos, Level level, LivingEntity user, InteractionHand hand, boolean consume, CompoundTag extraData) {
        BlockPos newPos = pos.relative(dir);
        if (user == null || level.isClientSide()) {
            return;
        }
        user.teleportToWithTicket(newPos.getX(), newPos.getY(), newPos.getZ());
        ItemStack itemInHand = user.getItemInHand(hand);
        if (consume && itemInHand.getTag() != null && !itemInHand.getTag().getBoolean("Creative")) {
            user.setItemInHand(hand, Utilities.DecodeStackTags(new ItemStack(itemInHand.getItem(), itemInHand.getCount() - 1, itemInHand.getTag())));
        }
    }
}
