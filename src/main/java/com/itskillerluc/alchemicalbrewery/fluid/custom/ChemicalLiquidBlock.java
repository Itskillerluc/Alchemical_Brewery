package com.itskillerluc.alchemicalbrewery.fluid.custom;

import com.itskillerluc.alchemicalbrewery.data.recipes.ChemicalLiquidRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ChemicalLiquidBlock extends LiquidBlock {

    public ChemicalLiquidBlock(Supplier<? extends FlowingFluid> pFluid, Properties pProperties) {
        super(pFluid, pProperties);
    }

    /**
     * @return returns if there is a recipe or not
     */
    public static boolean hasRecipe(Level level) {
        SimpleContainer inventory = new SimpleContainer();

        Optional<ChemicalLiquidRecipe> match = level.getRecipeManager()
                .getRecipeFor(ChemicalLiquidRecipe.Type.INSTANCE, inventory, level);

        return match.isPresent();
    }

    /**
     * checks what is inside, if it's an item it should convert it based on the recipe
     * @param pState blockState of this block
     * @param pLevel level the block is in
     * @param pPos blockPosition
     * @param pEntity entity that's inside the block
     */
    @Override
    @SuppressWarnings("deprecation")
    public void entityInside(@NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos, @NotNull Entity pEntity) {
        SimpleContainer inventory = new SimpleContainer();

        List<ChemicalLiquidRecipe> match = pLevel.getRecipeManager()
                    .getRecipesFor(ChemicalLiquidRecipe.Type.INSTANCE, inventory, pLevel);


        if(pEntity instanceof LivingEntity entity&&(!pLevel.isClientSide)){
            entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 20, 5));
            entity.addEffect(new MobEffectInstance(MobEffects.HUNGER, 20, 5));
            entity.addEffect(new MobEffectInstance(MobEffects.POISON, 20, 5));
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 3));
        }
        if (!pLevel.isClientSide && hasRecipe(pLevel) && pEntity instanceof ItemEntity item) {
            match.forEach((Match) -> {
                if (item.getItem().is(Match.getInput().getItem())) {
                    int amount = item.getItem().getCount();
                    item.kill();
                    pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(), 2);
                    for (int i = 0; i < amount; i++) {
                        pEntity.spawnAtLocation(Match.getResultItem());
                    }
                }
            });
        }
        super.entityInside(pState, pLevel, pPos, pEntity);
    }
}
