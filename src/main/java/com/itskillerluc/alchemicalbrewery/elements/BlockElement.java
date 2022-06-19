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

    public BlockElement(BlockElement element) {
        super(element);
        this.block = element.block;
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("color", this.color);
        tag.putString("block", this.block.getRegistryName().toString());
        return tag;
    }

    @Override
    public BlockElement instanciate() {
        return new BlockElement(this);
    }

    @Override
    public Element fromTag(CompoundTag tag) {
        this.color = tag.getInt("color");
        this.block = ForgeRegistries.BLOCKS.containsKey(ResourceLocation.tryParse(tag.getString("block"))) ? ForgeRegistries.BLOCKS.getValue(ResourceLocation.tryParse(tag.getString("block"))) : Blocks.AIR;
        return this;
    }

    public BlockElement(String Displayname) {
        super(Displayname);
        this.color = 6525687;
    }

    @Override
    void elementFunction(Direction dir, BlockPos pos, Level level, LivingEntity user, InteractionHand hand, boolean consume) {


        Block block = this.block;
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
