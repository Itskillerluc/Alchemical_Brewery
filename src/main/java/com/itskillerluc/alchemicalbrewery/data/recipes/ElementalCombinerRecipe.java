package com.itskillerluc.alchemicalbrewery.data.recipes;

import com.google.common.collect.Lists;
import com.google.gson.*;
import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import com.itskillerluc.alchemicalbrewery.elements.Element;
import com.itskillerluc.alchemicalbrewery.elements.ElementData;
import com.itskillerluc.alchemicalbrewery.elements.ModElements;
import com.itskillerluc.alchemicalbrewery.item.ModItems;
import com.itskillerluc.alchemicalbrewery.item.custom.ElementBasic;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ResourceLocationException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
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
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class ElementalCombinerRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final List<ItemStack> ingredientList;
    private final List<ElementData> elementList;
    private final List<Integer> countList;
    private final ElementData element;
    public ElementalCombinerRecipe(ResourceLocation id, ItemStack output, List<ItemStack> ingredientList, List<ElementData> elementList, List<Integer> countList, ElementData element) {
        this.id = id;
        this.output = output;
        this.ingredientList = ingredientList;
        this.elementList = elementList;
        this.countList = countList;
        this.element = element;
    }

    public int size(){
        return elementList.size();
    }

    public ElementData getElement() {
        return element;
    }

    @Override
    public boolean matches(@NotNull SimpleContainer pContainer, @NotNull Level pLevel) {
        if (ingredientList.stream().anyMatch(itemStack -> itemStack.is(ModItems.ELEMENT_CRAFTING.get()))) {
            AtomicInteger iteration = new AtomicInteger(0);
            return elementList.stream().allMatch(element -> pContainer.getItem(iteration.get()).is(ModItems.ELEMENT_CRAFTING.get()) ? element.matches(ElementData.of(Objects.requireNonNull(pContainer.getItem(iteration.getAndIncrement()).getTag()).getCompound("element"))) : ItemStack.isSameItemSameTags(ingredientList.get(iteration.get()), pContainer.getItem(iteration.get())));
        }
        return ingredientList.stream().allMatch(ingredient -> ItemStack.isSameItemSameTags(ingredient, pContainer.getItem(ingredientList.indexOf(ingredient))));
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull SimpleContainer pContainer) {
        return output.is(ModItems.ELEMENT_BASIC.get()) ? ElementBasic.fromData(element) : output;
    }

    @SuppressWarnings("unused")
    public ElementData getElement(int index){
        return elementList.get(index);
    }

    public int getCount(int index){
        return countList.get(index);
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem() {
        return ItemStack.EMPTY;
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

    public static class Type implements RecipeType<ElementalCombinerRecipe> {
        public Type() { }
        public static Type INSTANCE = new Type();
        public static final String ID = "elemental_combining";
    }

    public static class Serializer implements RecipeSerializer<ElementalCombinerRecipe>{
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(AlchemicalBrewery.MOD_ID,"elemental_combining");
        private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

        @Override
        public @NotNull ElementalCombinerRecipe fromJson(@NotNull ResourceLocation pRecipeId, @NotNull JsonObject pSerializedRecipe) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));

            ResourceLocation elementResourceLocation = ResourceLocation.tryParse(GsonHelper.getAsString(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"), "element"));

            Element element = ModElements.ELEMENTS.get().getValue(elementResourceLocation);

            if (element == null){
                LogManager.getLogger().warn(pRecipeId + ": Element with resource location " + elementResourceLocation + " doesn't exist.");
                throw new ResourceLocationException(pRecipeId + ": Element with resource location " + elementResourceLocation + " doesn't exist.");
            }

            Integer itemColor = GsonHelper.getAsInt(GsonHelper.getAsJsonObject(pSerializedRecipe, "output") , "itemColor", -1);
            Integer secItemColor = GsonHelper.getAsInt(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"), "secItemColor", -1);

            if (itemColor == -1){
                itemColor = null;
            }

            if (secItemColor == -1){
                secItemColor = null;
            }

            String displayName = GsonHelper.getAsString(pSerializedRecipe, "displayName", null);

            ItemStack displayItem = GsonHelper.getAsJsonObject(pSerializedRecipe, "displayItem", null) != null ? ShapedRecipe.itemStackFromJson(Objects.requireNonNull(GsonHelper.getAsJsonObject(pSerializedRecipe, "displayItem", null))) : element.defaultItemModel;

            JsonArray ingredients = GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients");

            List<ItemStack> ingredientList = new ArrayList<>();
            List<Element> elementList = Lists.newArrayList();
            List<CompoundTag> extraDataList = new ArrayList<>();
            List<Integer> counts = Lists.newArrayList();
            List<ItemStack> ingredientListTemp = new ArrayList<>();

            ingredients.forEach((ele)->{
                ingredientList.add(ShapedRecipe.itemStackFromJson(ele.getAsJsonObject()));
                ResourceLocation location = ResourceLocation.tryParse(ele.getAsJsonObject().get("element").getAsString());
                elementList.add(ModElements.ELEMENTS.get().getValue(location));
                extraDataList.add(generateAdditionalData(ele.getAsJsonObject(), element));
                counts.add(ele.getAsJsonObject().get("count").getAsInt());
            });

            CompoundTag extraData = generateAdditionalData(pSerializedRecipe.getAsJsonObject("output"), element);

            for(int i = 0; i<8-ingredientList.size();i++){
                ingredientListTemp.add(ItemStack.EMPTY);
            }

            ingredientList.addAll(ingredientListTemp);

            List<ElementData> dataList = new ArrayList<>();

            for (int i = 0, elementsListSize = elementList.size(); i < elementsListSize; i++) {
                com.itskillerluc.alchemicalbrewery.elements.Element element1 = elementList.get(i);
                dataList.add(new ElementData(null, null, null, null, extraDataList.get(i), element1));
            }

            return new ElementalCombinerRecipe(pRecipeId, output, ingredientList, dataList, counts, new ElementData(displayName, displayItem, itemColor, secItemColor, extraData, element));
        }

        private static CompoundTag generateAdditionalData(JsonObject parent, com.itskillerluc.alchemicalbrewery.elements.Element element) {
            return getAdditionalData(parent, element, GSON);
        }

        static CompoundTag getAdditionalData(JsonObject parent, Element element, Gson gson) {
            CompoundTag extraData = element.defaultAdditionalData;
            if(parent.has("extraData")) {
                try {
                    JsonElement jsonElement = parent.get("extraData");
                    CompoundTag nbt;
                    if (jsonElement.isJsonObject())
                        nbt = TagParser.parseTag(gson.toJson(jsonElement));
                    else
                        nbt = TagParser.parseTag(GsonHelper.convertToString(jsonElement, "extraData"));
                    extraData = nbt;
                } catch (CommandSyntaxException e) {
                    throw new JsonSyntaxException("Invalid NBT Entry: " + e);
                }
            }
            return extraData;
        }

        @Nullable
        @Override
        public ElementalCombinerRecipe fromNetwork(@NotNull ResourceLocation pRecipeId, FriendlyByteBuf buf) {
            ItemStack output = buf.readItem();
            List<ItemStack> ingredientList = buf.readList(FriendlyByteBuf::readItem);
            List<ElementData> elementList = buf.readList(FriendlyByteBuf::readNbt).stream().map(ElementData::of).toList();
            List<Integer> counts = buf.readList(FriendlyByteBuf::readInt);
            ElementData element = ElementData.of(Objects.requireNonNull(buf.readNbt()));

            return new ElementalCombinerRecipe(pRecipeId, output, ingredientList, elementList, counts, element);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, ElementalCombinerRecipe recipe) {
            buf.writeItemStack(recipe.getResultItem(), false);
            buf.writeCollection(recipe.ingredientList, FriendlyByteBuf::writeItem);
            buf.writeCollection(recipe.elementList, (friendlyByteBuf, elementData) -> friendlyByteBuf.writeNbt(elementData.toTag()));
            buf.writeCollection(recipe.countList, FriendlyByteBuf::writeInt);
            buf.writeNbt(recipe.element.toTag());
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

        @SuppressWarnings("unchecked")
        private static <G> Class<G> castClass() {
            return (Class<G>) RecipeSerializer.class;
        }
    }
}
