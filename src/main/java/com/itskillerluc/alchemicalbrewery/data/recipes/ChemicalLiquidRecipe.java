package com.itskillerluc.alchemicalbrewery.data.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ChemicalLiquidRecipe implements Recipe<Inventory> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> recipeItems;

    public ChemicalLiquidRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> recipeItems) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
    }

    @Override
    public boolean matches(Inventory pContainer, Level pLevel) {
        return true;
    }

    public ItemStack getinput(){return recipeItems.get(0).getItems()[0];}

    @Override
    public ItemStack assemble(Inventory pContainer) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ChemicalLiquidRecipe.Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return ChemicalLiquidRecipe.Type.INSTANCE;
    }

    public static class Type implements RecipeType<ChemicalLiquidRecipe> {
        private Type() { }
        public static final ChemicalLiquidRecipe.Type INSTANCE = new ChemicalLiquidRecipe.Type();
        public static final String ID = "chemical_bathing";
    }


    public static class Serializer implements RecipeSerializer<ChemicalLiquidRecipe>{
        public static final ChemicalLiquidRecipe.Serializer INSTANCE = new ChemicalLiquidRecipe.Serializer();
        public static final ResourceLocation ID = new ResourceLocation(AlchemicalBrewery.MOD_ID,"chemical_bathing");

        @Override
        public ChemicalLiquidRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));

            JsonArray ingredients = GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(1, Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            return new ChemicalLiquidRecipe(pRecipeId, output, inputs);
        }

        @Nullable
        @Override
        public ChemicalLiquidRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf buf) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(buf.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(buf));
            }
            ItemStack output = buf.readItem();
            return new ChemicalLiquidRecipe(pRecipeId, output, inputs);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, ChemicalLiquidRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.toNetwork(buf);
            }
            buf.writeItemStack(recipe.getResultItem(), false);
        }

        @Override
        public RecipeSerializer<?> setRegistryName(ResourceLocation name) {
            return INSTANCE;
        }

        @Nullable
        @Override
        public ResourceLocation getRegistryName() {
            return ID;
        }

        @Override
        public Class<RecipeSerializer<?>> getRegistryType() {
            return ChemicalLiquidRecipe.Serializer.castClass(RecipeSerializer.class);
        }

        @SuppressWarnings("unchecked") // Need this wrapper, because generics
        private static <G> Class<G> castClass(Class<?> cls) {
            return (Class<G>)cls;
        }
    }
}
