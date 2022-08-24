package com.itskillerluc.alchemicalbrewery.data.recipes;
//TODO
import com.google.gson.*;
import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import com.itskillerluc.alchemicalbrewery.elements.Element;
import com.itskillerluc.alchemicalbrewery.elements.ElementData;
import com.itskillerluc.alchemicalbrewery.elements.ModElements;
import com.itskillerluc.alchemicalbrewery.item.ModItems;
import com.itskillerluc.alchemicalbrewery.item.custom.Element_Basic;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.logging.Logger;

public class ElementalExtractorRecipe implements Recipe<SimpleContainer> {

    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> recipeItems;
    private final int outputcount;
    private final boolean capsule;
    private final ElementData element;

    public ElementalExtractorRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> recipeItems, int outputcount, boolean hascapsule, ElementData element) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
        this.outputcount = outputcount;
        this.capsule = hascapsule;
        this.element = element;
    }


    @Override
    public boolean matches(@NotNull SimpleContainer pContainer, @NotNull Level pLevel) {
        for (Element elementType : ModElements.ELEMENTS.get().getValues()) {
            if(elementType.extractorRecipeHelper(pContainer.getItem(0)) != null && recipeItems.get(2).test(pContainer.getItem(2))){
                return (capsule) ? pContainer.getItem(1).is(ModItems.CAPSULE_MEDIUM.get())||pContainer.getItem(1).is(ModItems.CAPSULE_LARGE.get())||pContainer.getItem(1).is(ModItems.CAPSULE_SMALL.get()) : recipeItems.get(1).test(pContainer.getItem(1));
            }
        }
        if(recipeItems.get(0).test(pContainer.getItem(0))&&recipeItems.get(2).test(pContainer.getItem(2))){
            return (capsule) ? pContainer.getItem(1).is(ModItems.CAPSULE_MEDIUM.get())||pContainer.getItem(1).is(ModItems.CAPSULE_LARGE.get())||pContainer.getItem(1).is(ModItems.CAPSULE_SMALL.get()) : recipeItems.get(1).test(pContainer.getItem(1));
        }
        return false;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull SimpleContainer pContainer) {
        return output;
    }

    public ElementData getElement(){
        return element;
    }


    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem() {
        return output.is(ModItems.ELEMENT_BASIC.get()) ? Element_Basic.fromData(element) : output;

    }
    public ItemStack getResultItemAdvanced(SimpleContainer container){
        for (Element elementType : ModElements.ELEMENTS.get().getValues()) {
            if(elementType.extractorRecipeHelper(container.getItem(0)) != null){
                String subName = container.getItem(0).getDisplayName().getString();
                return Element_Basic.fromData(new ElementData(subName.substring(1, subName.length()-1), null, elementType.getDynamicColor().apply(container.getItem(0)).intValue(), (new Color(elementType.getDynamicColor().apply(container.getItem(0)))).darker().getRGB(), elementType.extractorRecipeHelper(container.getItem(0)), elementType));
            }
        }
        return getResultItem();
    }

    public int getOutputcount(){
        return outputcount;
    }

    public boolean getIfCapsule(){return capsule;}

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

    public static class Type implements RecipeType<ElementalExtractorRecipe> {
        public Type() { }
        public static Type INSTANCE = new Type();
        public static final String ID = "elemental_extracting";
    }


    public static class Serializer implements RecipeSerializer<ElementalExtractorRecipe>{
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(AlchemicalBrewery.MOD_ID,"elemental_extracting");
        private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

        @Override
        public @NotNull ElementalExtractorRecipe fromJson(@NotNull ResourceLocation pRecipeId, @NotNull JsonObject pSerializedRecipe) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));

            ResourceLocation elementRescourceLocation = ResourceLocation.tryParse(GsonHelper.getAsString(pSerializedRecipe, "element"));

            Element element = ModElements.ELEMENTS.get().getValue(elementRescourceLocation);

            int count = GsonHelper.getAsInt(pSerializedRecipe, "count", 1);

            boolean capsule = GsonHelper.getAsBoolean(pSerializedRecipe, "capsule");

            int color = GsonHelper.getAsInt(pSerializedRecipe, "itemcolor", element.defaultColor);

            int seccolor = GsonHelper.getAsInt(pSerializedRecipe, "secitemcolor", element.defaultSecColor);

            String displayName = GsonHelper.getAsString(pSerializedRecipe, "displayname", element.defaultDisplayName);

            CompoundTag extraData = element.defaultAdditionalData;

            if(pSerializedRecipe.has("extradata")) {
                try {
                    JsonElement jsonElement = pSerializedRecipe.get("extradata");
                    CompoundTag nbt;
                    if (jsonElement.isJsonObject())
                        nbt = TagParser.parseTag(GSON.toJson(jsonElement));
                    else
                        nbt = TagParser.parseTag(GsonHelper.convertToString(jsonElement, "extradata"));

                    extraData = nbt;
                } catch (CommandSyntaxException e) {
                    throw new JsonSyntaxException("Invalid NBT Entry: " + e);
                }
            }

            JsonArray ingredients = GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients");

            ItemStack displayItem = GsonHelper.getAsJsonObject(pSerializedRecipe, "displayitem", null) != null ? ShapedRecipe.itemStackFromJson(Objects.requireNonNull(GsonHelper.getAsJsonObject(pSerializedRecipe, "displayitem", null))) : element.defualtItemModel;

            NonNullList<Ingredient> inputs = NonNullList.withSize(3, Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            return new ElementalExtractorRecipe(pRecipeId, output, inputs, count, capsule, new ElementData(displayName, displayItem, color, seccolor, extraData, element));
        }

        @Nullable
        @Override
        public ElementalExtractorRecipe fromNetwork(@NotNull ResourceLocation pRecipeId, FriendlyByteBuf buf) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(buf.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(buf));
            }
            ItemStack output = buf.readItem();
            ResourceLocation elementRescourceLocation = buf.readResourceLocation();
            Element element = ModElements.ELEMENTS.get().getValue(elementRescourceLocation);
            int count = buf.readInt();
            boolean capsule = buf.readBoolean();
            int color = buf.readInt();
            int seccolor = buf.readInt();
            String displayName = buf.readUtf();
            CompoundTag extraData = buf.readNbt();
            ItemStack displayItem = buf.readItem();

            return new ElementalExtractorRecipe(pRecipeId, output, inputs, count, capsule, new ElementData(displayName, displayItem, color, seccolor, extraData, element));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, ElementalExtractorRecipe recipe) {
            buf.writeInt(recipe.getIngredients().size());
            for (Ingredient ing : recipe.getIngredients()) {
                ing.toNetwork(buf);
            }
            buf.writeItemStack(recipe.output, false);
            buf.writeResourceLocation(Objects.requireNonNull(recipe.element.elementType.getRegistryName()));
            buf.writeInt(recipe.outputcount);
            buf.writeBoolean(recipe.capsule);
            buf.writeInt(recipe.element.color);
            buf.writeInt(recipe.element.secColor);
            buf.writeUtf(recipe.element.displayName);
            buf.writeNbt(recipe.element.additionalData);
            buf.writeItemStack(recipe.element.itemModel, false);
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
