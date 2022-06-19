package com.itskillerluc.alchemicalbrewery.elements;

import com.itskillerluc.alchemicalbrewery.entity.custom.ElementProjectileEntity;
import com.itskillerluc.alchemicalbrewery.item.ModItems;
import com.itskillerluc.alchemicalbrewery.util.Utilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;


import java.util.ArrayList;
import java.util.Objects;

public class LavaElement extends Element{


    @Override
    public LavaElement instanciate() {
        return new LavaElement(this);
    }

    public LavaElement(String Displayname) {
        super(Displayname);
        this.color = 16734006;
    }

    public LavaElement(LavaElement element) {
        super(element);
    }

    void elementFunction(Direction dir, BlockPos pos, Level level, LivingEntity user, InteractionHand hand, boolean consume) {
        BlockPos newpos;
        newpos = switch (dir) {
            case UP -> pos.above();
            case DOWN -> pos.below();
            case EAST -> pos.east();
            case WEST -> pos.west();
            case NORTH -> pos.north();
            case SOUTH -> pos.south();
        };
        if (!level.isClientSide() && level.getBlockState(newpos).getMaterial().isReplaceable()) {
            level.setBlock(newpos, Blocks.LAVA.defaultBlockState(), 2);
            if (user != null && consume) {
                if (user.getItemInHand(hand).hasTag()) {
                    if (!Objects.requireNonNull(user.getItemInHand(hand).getTag()).getBoolean("Creative")) {
                        user.setItemInHand(hand, Utilities.DecodeStackTags(new ItemStack(user.getItemInHand(hand).getItem(), user.getItemInHand(hand).getCount() - 1, user.getItemInHand(hand).getTag())));
                    }
                }
            }
        }
    }
}
