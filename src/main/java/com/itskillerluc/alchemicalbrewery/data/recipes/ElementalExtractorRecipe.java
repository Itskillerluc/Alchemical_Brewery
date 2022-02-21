package com.itskillerluc.alchemicalbrewery.data.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import com.itskillerluc.alchemicalbrewery.item.ModItems;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.NBTIngredient;
import net.minecraftforge.common.util.JsonUtils;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class ElementalExtractorRecipe implements Recipe<SimpleContainer> {

    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> recipeItems;
    private final int outputcount;

    public ElementalExtractorRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> recipeItems, int outputcount) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
        this.outputcount = outputcount;
    }


    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        if(recipeItems.get(0).test(pContainer.getItem(0))&&recipeItems.get(1).test(pContainer.getItem(1))){
            return recipeItems.get(2).test(pContainer.getItem(2));
        }
        return false;
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer) {
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

    public int getOutputcount(){
        return outputcount;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<ElementalExtractorRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "elemental_extracting";
    }


    public static class Serializer implements RecipeSerializer<ElementalExtractorRecipe>{
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(AlchemicalBrewery.MOD_ID,"elemental_extracting");

        @Override
        public ElementalExtractorRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));
            int count = GsonHelper.getAsInt(pSerializedRecipe, "count");

            JsonArray ingredients = GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(3, Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            return new ElementalExtractorRecipe(pRecipeId, output, inputs, count);
        }

        @Nullable
        @Override
        public ElementalExtractorRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf buf) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(buf.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(buf));
            }
            int count = buf.readInt();
            ItemStack output = buf.readItem();
            return new ElementalExtractorRecipe(pRecipeId, output, inputs, count);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, ElementalExtractorRecipe recipe) {
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
            return Serializer.castClass(RecipeSerializer.class);
        }

        @SuppressWarnings("unchecked") // Need this wrapper, because generics
        private static <G> Class<G> castClass(Class<?> cls) {
            return (Class<G>)cls;
        }
    }
}
