package com.itskillerluc.alchemicalbrewery.data.recipes;
//TODO
import com.google.gson.JsonObject;
import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import com.itskillerluc.alchemicalbrewery.data.ChargeLoader;
import com.itskillerluc.alchemicalbrewery.elements.Element;
import com.itskillerluc.alchemicalbrewery.elements.ModElements;
import com.itskillerluc.alchemicalbrewery.item.custom.Element_Basic;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ElementalInjectorRecipe implements Recipe<SimpleContainer> {

    private final ResourceLocation id;
    private final ItemStack output;
    private final ItemStack recipeItems;
    private final int outputcount;
    private final Element element;
    public ElementalInjectorRecipe(ResourceLocation id, ItemStack output, ItemStack recipeItems, int outputcount, Element element) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
        this.outputcount = outputcount;
        this.element = element;
    }

    public ItemStack getRecipeItems() {
        return recipeItems;
    }

    @Override
    public boolean matches(@NotNull SimpleContainer pContainer, @NotNull Level pLevel) {
        return pContainer.getItem(1).getItem() instanceof Element_Basic && Objects.equals(Element_Basic.getElement(pContainer.getItem(1)).getRegistryName(), element.getRegistryName()) || chargematches(pContainer);
    }

    public static boolean chargematches(SimpleContainer pContainer){
        return ChargeLoader.contains(pContainer.getItem(0));
    }

    public int getCharge(SimpleContainer container){
        return ChargeLoader.getCharge(container.getItem(0));
    }
    @Override
    public @NotNull ItemStack assemble(@NotNull SimpleContainer pContainer) {
        if (element.injectorRecipeHelper(pContainer.getItem(1).getTag()) != null){
            return new ItemStack(element.injectorRecipeHelper(pContainer.getItem(1).getTag()));
        }
        return output;
    }

    public Element getElement(){
        return element;
    }


    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem() {
        return output.copy();
    }

    public ItemStack getResult(SimpleContainer container){
        if (element.injectorRecipeHelper(container.getItem(1).getTag()) != null){
            return new ItemStack(element.injectorRecipeHelper(container.getItem(1).getTag()));
        }
        return output;
    }

    public int getOutputcount(){
        return outputcount;
    }

    @Override
    public @NotNull ResourceLocation getId() {
        return id;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<ElementalInjectorRecipe> {
        public Type() { }
        public static Type INSTANCE = new Type();
        public static final String ID = "elemental_injecting";
    }

    public static class Serializer implements RecipeSerializer<ElementalInjectorRecipe>{
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(AlchemicalBrewery.MOD_ID,"elemental_injecting");

        @Override
        public @NotNull ElementalInjectorRecipe fromJson(@NotNull ResourceLocation pRecipeId, @NotNull JsonObject pSerializedRecipe) {
            ResourceLocation elementResourceLocation = ResourceLocation.tryParse(GsonHelper.getAsString(pSerializedRecipe, "element"));


            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));

            Element element = ModElements.ELEMENTS.get().getValue(elementResourceLocation);

            int count = GsonHelper.getAsInt(pSerializedRecipe, "count");

            ItemStack input = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "input"));


            return new ElementalInjectorRecipe(pRecipeId, output, input, count, element);
        }

        @Nullable
        @Override
        public ElementalInjectorRecipe fromNetwork(@NotNull ResourceLocation pRecipeId, FriendlyByteBuf buf) {

            int count = buf.readInt();
            ItemStack output = buf.readItem();
            ItemStack input = buf.readItem();
            ResourceLocation elementRescourceLocation = buf.readResourceLocation();
            Element element = ModElements.ELEMENTS.get().getValue(elementRescourceLocation);

            return new ElementalInjectorRecipe(pRecipeId, output, input, count, element);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, ElementalInjectorRecipe recipe) {
            buf.writeInt(recipe.getOutputcount());
            buf.writeItemStack(recipe.getResultItem(), false);
            buf.writeItemStack(recipe.recipeItems, false);
            buf.writeResourceLocation(Objects.requireNonNull(recipe.element.getRegistryName()));
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
            return Serializer.castClass();
        }

        @SuppressWarnings("unchecked") // Need this wrapper, because generics
        private static <G> Class<G> castClass() {
            return (Class<G>) RecipeSerializer.class;
        }
    }
}
