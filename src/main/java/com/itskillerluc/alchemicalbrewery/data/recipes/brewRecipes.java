package com.itskillerluc.alchemicalbrewery.data.recipes;


import com.itskillerluc.alchemicalbrewery.item.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;

public class brewRecipes {
    public void registerRecipes(){
        BrewingRecipeRegistry.addRecipe(new BrewingRecipe(Ingredient.of(PotionUtils.setPotion(new ItemStack(Items.POTION),Potions.WATER)), Ingredient.of(new ItemStack(ModItems.SULPHUR.get())), new ItemStack(ModItems.ACID.get())));
    }
}
