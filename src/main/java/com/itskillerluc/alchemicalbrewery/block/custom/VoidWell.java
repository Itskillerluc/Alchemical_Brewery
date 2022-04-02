package com.itskillerluc.alchemicalbrewery.block.custom;

import com.itskillerluc.alchemicalbrewery.item.ModItems;
import com.itskillerluc.alchemicalbrewery.item.custom.Element_Basic;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class VoidWell extends Block {
    public VoidWell(Properties p_49795_) {
        super(p_49795_);
    }
    @Override
    public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
        if(!pLevel.isClientSide()){
            if(pEntity instanceof ItemEntity){
                if(((ItemEntity) pEntity).getItem().is(ModItems.ELEMENT_BASIC.get())){
                    Element_Basic.convert(((ItemEntity) pEntity), pLevel);
                    pEntity.kill();
                }else{
                    pEntity.kill();
                }
            }else{
                pEntity.hurt(DamageSource.OUT_OF_WORLD, 4);
            }
        }
    }
}