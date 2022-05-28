package com.itskillerluc.alchemicalbrewery.data.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import com.itskillerluc.alchemicalbrewery.item.ModItems;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ElementalInjectorRecipe implements Recipe<SimpleContainer> {

    private final ResourceLocation id;
    private final ItemStack output;
    private final ItemStack recipeItems;
    private final int outputcount;
    private final String element;
    private final int charge;
    private final boolean onlyCharge;

    public ElementalInjectorRecipe(ResourceLocation id, ItemStack output, ItemStack recipeItems, int outputcount, String element, int charge, boolean onlyCharge) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
        this.outputcount = outputcount;
        this.element = element;
        this.charge = charge;
        this.onlyCharge = onlyCharge;
    }

    public ItemStack getRecipeItems() {
        return recipeItems;
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        return recipematches(pContainer, pLevel)||chargematches(pContainer, pLevel);
    }

    public boolean recipematches(SimpleContainer pContainer, Level pLevel){
        CompoundTag nbt = recipeItems.getOrCreateTag();
        nbt.putString("Element", getElement());
        try{
            if(pContainer.getItem(1).getTag().contains("ItemColor")) {
                nbt.putInt("ItemColor", pContainer.getItem(1).getTag().getInt("ItemColor"));
            }
            if(pContainer.getItem(1).getTag().contains("SecItemColor")) {
                nbt.putInt("SecItemColor", pContainer.getItem(1).getTag().getInt("SecItemColor"));
            }
        }catch (NullPointerException except){

        }
        return ItemStack.isSameItemSameTags(pContainer.getItem(1),recipeItems);
    }
    public boolean chargematches(SimpleContainer pContainer,Level pLevel){
        return output.is(pContainer.getItem(0).getItem());
    }
    public boolean isOnlyCharge(){
        return onlyCharge;
    }

    public int getCharge(){
        return charge;
    }
    @Override
    public ItemStack assemble(SimpleContainer pContainer) {
        return output;
    }

    public String getElement(){
        return element;
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

    public static class Type implements RecipeType<ElementalInjectorRecipe> {
        public Type() { }
        public static Type INSTANCE = new Type();
        public static final String ID = "elemental_injecting";
    }

    public static ItemStack itemStackFromJson(JsonObject pStackObject) {
        return net.minecraftforge.common.crafting.CraftingHelper.getItemStack(pStackObject, true, true);
    }


    public static class Serializer implements RecipeSerializer<ElementalInjectorRecipe>{
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(AlchemicalBrewery.MOD_ID,"elemental_injecting");

        @Override
        public ElementalInjectorRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));
            int count = GsonHelper.getAsInt(pSerializedRecipe, "count");
            String element = GsonHelper.getAsString(pSerializedRecipe, "element");
            int charge = GsonHelper.getAsInt(pSerializedRecipe, "charge");
            boolean onlyCharge = GsonHelper.getAsBoolean(pSerializedRecipe, "onlycharge");

            ItemStack input = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "input"));

            return new ElementalInjectorRecipe(pRecipeId, output, input, count, element,charge,onlyCharge);
        }

        @Nullable
        @Override
        public ElementalInjectorRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf buf) {

            int count = buf.readInt();
            ItemStack output = buf.readItem();
            ItemStack input = buf.readItem();
            String element = buf.readCharSequence(buf.readInt(), StandardCharsets.UTF_8).toString();
            int charge = buf.readInt();
            boolean onlyCharge = buf.readBoolean();

            return new ElementalInjectorRecipe(pRecipeId, output, input, count, element,charge,onlyCharge);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, ElementalInjectorRecipe recipe) {
            buf.writeInt(recipe.getOutputcount());
            buf.writeItemStack(recipe.getResultItem(), false);
            buf.writeItemStack(recipe.recipeItems, false);
            buf.writeCharSequence(recipe.element, StandardCharsets.UTF_8);
            buf.writeInt(recipe.element.length());
            buf.writeInt(recipe.charge);
            buf.writeBoolean(recipe.onlyCharge);
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
