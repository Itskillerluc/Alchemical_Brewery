package com.itskillerluc.alchemicalbrewery.util;

import com.itskillerluc.alchemicalbrewery.data.ChargeLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.Map;

public class ChargeCalculator {
    private static final Map<Ingredient, Integer> charges = Map.copyOf(ChargeLoader.chargeValues);

    public static int getCharge(Ingredient item, Level level){
        if (item.getItems().length < 1) {
            return 0;
        }
        RecipeManager manager = level.getRecipeManager();
        var recipes = manager.getAllRecipesFor(RecipeType.CRAFTING);

        int charge = 0;

        var chargeValue = charges.entrySet().stream().filter(entry -> Arrays.stream(item.getItems()).anyMatch(key -> entry.getKey().test(key))).findFirst().map(Map.Entry::getValue).orElse(null);
        if (chargeValue != null){
            return chargeValue;
        }

        var matches = recipes.stream().filter(recipe -> recipe.getResultItem().is(item.getItems()[0].getItem())).toList();

        for (CraftingRecipe craftingRecipe : matches) {
            for (Ingredient ingredient : craftingRecipe.getIngredients()) {
                charge += getCharge(ingredient, level);
            }
        }
        if (matches.size() > 0){
            charge /= matches.size();
        }

        return charge;
    }
}
