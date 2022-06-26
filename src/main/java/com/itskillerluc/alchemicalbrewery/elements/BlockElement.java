package com.itskillerluc.alchemicalbrewery.elements;

import com.itskillerluc.alchemicalbrewery.util.Utilities;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.Objects;

@SuppressWarnings("SpellCheckingInspection")
public class BlockElement extends Element {

    public BlockElement(String Displayname) {
        super(Displayname, Util.make(() -> {
            CompoundTag tag = new CompoundTag();
            tag.put("block", new CompoundTag());
            new ItemStack(Items.AIR).save(tag.getCompound("block"));
            return tag;
        }), null, 6525687);
    }

    @Override
    void elementFunction(Direction dir, BlockPos pos, Level level, LivingEntity user, InteractionHand hand, boolean consume, CompoundTag extraData) {
        ItemStack itemStack = ItemStack.of(extraData.getCompound("block"));

        Block block = Block.byItem(itemStack.getItem());
        BlockPos newpos;
        newpos = switch (dir) {
            case UP -> pos.above();
            case DOWN -> pos.below();
            case EAST -> pos.east();
            case WEST -> pos.west();
            case NORTH -> pos.north();
            case SOUTH -> pos.south();
        };
        if (block.defaultBlockState().getMaterial().isReplaceable() && dir.equals(Direction.UP)) {
            if (level.getBlockState(newpos.below()).getMaterial().isReplaceable()) {
                newpos = newpos.below();
            }
        }
        if (!level.isClientSide() && level.getBlockState(newpos).getMaterial().isReplaceable()) {
            if (block.defaultBlockState().getMaterial().isReplaceable()) {
                if (!level.getBlockState(newpos.below()).isAir()) {
                    level.setBlock(newpos, block.defaultBlockState(), 3);
                }
            } else {
                level.setBlock(newpos, block.defaultBlockState(), 3);
                if (user != null && consume) {
                    if (!Objects.requireNonNull(user.getItemInHand(hand).getTag()).getBoolean("Creative")) {
                        user.setItemInHand(hand, Utilities.DecodeStackTags(new ItemStack(user.getItemInHand(hand).getItem(), user.getItemInHand(hand).getCount() - 1, user.getItemInHand(hand).getTag())));
                    }
                }
            }
        }
    }
}
