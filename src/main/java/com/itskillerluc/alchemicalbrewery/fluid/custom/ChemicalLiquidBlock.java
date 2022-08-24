package com.itskillerluc.alchemicalbrewery.fluid.custom;
//TODO
import com.itskillerluc.alchemicalbrewery.data.recipes.ChemicalLiquidRecipe;
import com.itskillerluc.alchemicalbrewery.data.recipes.ElementalCombinerRecipe;
import com.itskillerluc.alchemicalbrewery.data.recipes.ElementalExtractorRecipe;
import com.itskillerluc.alchemicalbrewery.data.recipes.ModRecipeTypes;
import com.itskillerluc.alchemicalbrewery.item.ModItems;
import com.itskillerluc.alchemicalbrewery.tileentity.ElementalCombinerTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class ChemicalLiquidBlock extends LiquidBlock {


    public ChemicalLiquidBlock(Supplier<? extends FlowingFluid> pFluid, Properties pProperties) {
        super(pFluid, pProperties);
    }


    /**
     * @return returns if there is a recipe instanciated or not
     */
    public static boolean hasRecipe(Level level) {
        SimpleContainer inventory = null;

        Optional<ChemicalLiquidRecipe> match = level.getRecipeManager()
                .getRecipeFor(ChemicalLiquidRecipe.Type.INSTANCE, inventory, level);

        return match.isPresent();
    }

    /**
     * checks what is inside, if its a item it should convert it based on the recipe
     * @param pState blockstate of this block
     * @param pLevel level the block is in
     * @param pPos blockposition
     * @param pEntity entity that's inside of the block
     */
    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        SimpleContainer inventory = null;
        List<ChemicalLiquidRecipe> match = pLevel.getRecipeManager()
                    .getRecipesFor(ChemicalLiquidRecipe.Type.INSTANCE, inventory, pLevel);


        if(pEntity instanceof LivingEntity entity&&(!pLevel.isClientSide)){
            entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 20, 5));
            entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 20, 5));
            entity.addEffect(new MobEffectInstance(MobEffects.POISON, 20, 5));
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 3));
        }
        if(!pLevel.isClientSide){
            if(hasRecipe(pLevel)){
                if(pEntity instanceof ItemEntity item){
                    match.forEach((Match)->{
                        if(item.getItem().is(Match.getInput().getItem())){
                            int amount = item.getItem().getCount();
                            item.kill();
                            pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(), 2);
                            for(int i=0; i < amount; i++){
                                pEntity.spawnAtLocation(Match.getResultItem());
                            }
                        }
                    });
                }
            }
        }
        super.entityInside(pState, pLevel, pPos, pEntity);
    }


}
