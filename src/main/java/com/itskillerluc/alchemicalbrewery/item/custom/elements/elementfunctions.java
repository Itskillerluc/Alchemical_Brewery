package com.itskillerluc.alchemicalbrewery.item.custom.elements;
//TODO
import com.itskillerluc.alchemicalbrewery.util.Utilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Contains the functions that can be ran for element types.
 */
public class elementfunctions {
    public static void Ender(Direction dir, BlockPos pos, Level level, LivingEntity user, InteractionHand hand, boolean consume, String[] args){
        BlockPos newpos;
        newpos = switch (dir) {
            case UP -> pos.above();
            case DOWN -> pos.below();
            case EAST -> pos.east();
            case WEST -> pos.west();
            case NORTH -> pos.north();
            case SOUTH -> pos.south();
        };
        if (!level.isClientSide()) {
            user.teleportToWithTicket(newpos.getX(), newpos.getY(), newpos.getZ());
            if (user != null && consume) {
                if (user.getItemInHand(hand).hasTag()) {
                    if (!user.getItemInHand(hand).getTag().getBoolean("Creative")) {
                        user.setItemInHand(hand, Utilities.DecodeStackTags(new ItemStack(user.getItemInHand(hand).getItem(), user.getItemInHand(hand).getCount() - 1, user.getItemInHand(hand).getTag())));
                    }
                }
            }
        }
    }
    public static void lava(Direction dir, BlockPos pos, Level level, LivingEntity user, InteractionHand hand, boolean consume, String[] args) {
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
                    if (!user.getItemInHand(hand).getTag().getBoolean("Creative")) {
                        user.setItemInHand(hand, Utilities.DecodeStackTags(new ItemStack(user.getItemInHand(hand).getItem(), user.getItemInHand(hand).getCount() - 1, user.getItemInHand(hand).getTag())));
                    }
                }
            }
        }
    }

    public static void water(Direction dir, BlockPos pos, Level level, LivingEntity user, InteractionHand hand, boolean consume, String[] args) {
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
            level.setBlock(newpos, Blocks.WATER.defaultBlockState(), 2);
            if (user != null && consume) {
                if (!user.getItemInHand(hand).getTag().getBoolean("Creative")) {
                    user.setItemInHand(hand, Utilities.DecodeStackTags(new ItemStack(user.getItemInHand(hand).getItem(), user.getItemInHand(hand).getCount() - 1, user.getItemInHand(hand).getTag())));
                }
            }
        }
    }

    /**
     * @param args the block that should be placed
     */
    public static void block(Direction dir, BlockPos pos, Level level, LivingEntity user, InteractionHand hand, boolean consume, String[] args) {
        Block block;
        if (ForgeRegistries.BLOCKS.getValue(new ResourceLocation(args[0])) != null) {
            block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(args[0]));
        }else{
            block = Blocks.AIR;
        }
        BlockPos newpos;
        newpos = switch (dir) {
            case UP -> pos.above();
            case DOWN -> pos.below();
            case EAST -> pos.east();
            case WEST -> pos.west();
            case NORTH -> pos.north();
            case SOUTH -> pos.south();
        };
        if(block.defaultBlockState().getMaterial().isReplaceable()&&dir.equals(Direction.UP)){
            if(level.getBlockState(newpos.below()).getMaterial().isReplaceable()){
                newpos = newpos.below();
            }
        }
        if (!level.isClientSide() && level.getBlockState(newpos).getMaterial().isReplaceable()) {
            if(block.defaultBlockState().getMaterial().isReplaceable()){
                if(!level.getBlockState(newpos.below()).isAir()){
                    level.setBlock(newpos, block.defaultBlockState(), 3);
                }
            }else {
                level.setBlock(newpos, block.defaultBlockState(), 3);
                if (user != null && consume) {
                    if (!user.getItemInHand(hand).getTag().getBoolean("Creative")) {
                        user.setItemInHand(hand, Utilities.DecodeStackTags(new ItemStack(user.getItemInHand(hand).getItem(), user.getItemInHand(hand).getCount() - 1, user.getItemInHand(hand).getTag())));
                    }
                }
            }
        }
    }
}
