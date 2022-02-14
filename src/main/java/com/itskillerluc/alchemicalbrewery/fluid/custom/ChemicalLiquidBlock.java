package com.itskillerluc.alchemicalbrewery.fluid.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

import java.util.function.Supplier;

public class ChemicalLiquidBlock extends LiquidBlock {

    public ChemicalLiquidBlock(Supplier<? extends FlowingFluid> pFluid, Properties pProperties) {
        super(pFluid, pProperties);
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if(pEntity instanceof Player player&&(!pLevel.isClientSide)){
            player.addEffect(new MobEffectInstance(MobEffects.WITHER, 20, 5));
            player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 20, 5));
            player.addEffect(new MobEffectInstance(MobEffects.POISON, 20, 5));
        }
        if(pEntity instanceof ItemEntity item &&(!pLevel.isClientSide)){
            if (item.getItem().is(Items.IRON_INGOT)){
                item.kill();
                pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(), 2);
                pEntity.spawnAtLocation(Items.DIAMOND);
            }
            else{
                item.kill();
            }
        }
        super.entityInside(pState, pLevel, pPos, pEntity);
    }


}
