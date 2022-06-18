package com.itskillerluc.alchemicalbrewery.elements;

import com.itskillerluc.alchemicalbrewery.util.Utilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public class BlockElement extends Element{
    public Block block;

    @Override
    public CompoundTag getDataCompound() {
        return null;
    }

    @Override
    public void setDataCompound(CompoundTag dataCompound) {

    }

    public BlockElement(String Displayname) {
        super(Displayname);
        this.color = 6525687;
    }

    @Override
    void elementFunction(Direction dir, BlockPos pos, Level level, LivingEntity user, InteractionHand hand, boolean consume, CompoundTag compound) {


        Block block;
        if (ForgeRegistries.BLOCKS.getValue(new ResourceLocation(compound.getString("Block"))) != null) {
            block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(compound.getString("Block")));
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
