package com.itskillerluc.alchemicalbrewery.fluid.custom;

import com.itskillerluc.alchemicalbrewery.data.recipes.ChemicalLiquidRecipe;
import com.itskillerluc.alchemicalbrewery.data.recipes.ElementalExtractorRecipe;
import com.itskillerluc.alchemicalbrewery.data.recipes.ModRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

import java.util.Optional;
import java.util.function.Supplier;

public class ChemicalLiquidBlock extends LiquidBlock {

    public ChemicalLiquidBlock(Supplier<? extends FlowingFluid> pFluid, Properties pProperties) {
        super(pFluid, pProperties);
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if(pEntity instanceof LivingEntity entity&&(!pLevel.isClientSide)){
            entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 20, 5));
            entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 20, 5));
            entity.addEffect(new MobEffectInstance(MobEffects.POISON, 20, 5));
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 3));
        }
        for (final Recipe<?> recipe : ModRecipeTypes.getRecipes(ChemicalLiquidRecipe.Type.INSTANCE, pLevel.getRecipeManager()).values()){
            final ChemicalLiquidRecipe chemicalLiquidRecipe = (ChemicalLiquidRecipe) recipe;
            if(pEntity instanceof  ItemEntity item &&(!pLevel.isClientSide)){
                if (item.getItem().is(chemicalLiquidRecipe.getinput().getItem())){
                    int amount = item.getItem().getCount();
                    item.kill();
                    pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(), 2);
                    for (int i = 0; i < amount; i++){
                        pEntity.spawnAtLocation(chemicalLiquidRecipe.getResultItem());
                    }
                }
            }
        }

        super.entityInside(pState, pLevel, pPos, pEntity);
    }


}
