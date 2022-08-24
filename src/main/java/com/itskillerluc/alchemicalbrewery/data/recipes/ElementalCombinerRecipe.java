package com.itskillerluc.alchemicalbrewery.data.recipes;
//TODO
import com.google.common.collect.Lists;
import com.google.gson.*;
import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import com.itskillerluc.alchemicalbrewery.elements.Element;
import com.itskillerluc.alchemicalbrewery.elements.ElementData;
import com.itskillerluc.alchemicalbrewery.elements.ModElements;
import com.itskillerluc.alchemicalbrewery.item.ModItems;
import com.itskillerluc.alchemicalbrewery.item.custom.Element_Basic;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class ElementalCombinerRecipe implements Recipe<SimpleContainer> {

    private final ResourceLocation id;
    private final ItemStack output;
    private final List<ItemStack> ingredientList;
    private final List<ElementData> elementslist;
    private final List<Integer> counts;
    private final ElementData Element;
    private final int itemcolor;
    private final int secitemcolor;

    public ElementalCombinerRecipe(ResourceLocation id, ItemStack output, List<ItemStack> ingredientList, List<ElementData> elementslist, List<Integer> counts, ElementData element, int itemcolor, int secitemcolor) {
        this.id = id;
        this.output = output;
        this.ingredientList = ingredientList;
        this.elementslist = elementslist;
        this.counts = counts;
        this.Element = element;
        this.itemcolor = itemcolor;
        this.secitemcolor = secitemcolor;
    }

    public int size(){
        return elementslist.size();
    }

    public ElementData getelement() {
        return Element;
    }

    public int getItemcolor() {
        return itemcolor;
    }
    public int getSecitemcolor(){return secitemcolor;}
    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        if (ingredientList.stream().anyMatch(itemStack -> itemStack.is(ModItems.ELEMENT_CRAFTING.get()))) {
            AtomicInteger iteration = new AtomicInteger(0);
            return elementslist.stream().allMatch(element -> pContainer.getItem(iteration.get()).is(ModItems.ELEMENT_CRAFTING.get()) ? element.matches(ElementData.of(Objects.requireNonNull(pContainer.getItem(iteration.getAndIncrement()).getTag()).getCompound("element"))) : ItemStack.isSameItemSameTags(ingredientList.get(iteration.get()), pContainer.getItem(ingredientList.indexOf(iteration.get()))));
        }
        return ingredientList.stream().allMatch(ingredient -> ItemStack.isSameItemSameTags(ingredient, pContainer.getItem(ingredientList.indexOf(ingredient))));
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer) {
        return output.is(ModItems.ELEMENT_BASIC.get()) ? Element_Basic.fromData(Element) : output;
    }

    public ElementData getElement(int index){
        return elementslist.get(index);
    }

    public int getCount(int index){
        return counts.get(index);
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
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<ElementalCombinerRecipe> {
        public Type() { }
        public static Type INSTANCE = new Type();
        public static final String ID = "elemental_combining";
    }

    public static ItemStack itemStackFromJson(JsonObject pStackObject) {
        return net.minecraftforge.common.crafting.CraftingHelper.getItemStack(pStackObject, true, true);
    }


    public static class Serializer implements RecipeSerializer<ElementalCombinerRecipe>{
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(AlchemicalBrewery.MOD_ID,"elemental_combining");
        private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

        @Override
        public ElementalCombinerRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));

            ResourceLocation elementRescourceLocation = ResourceLocation.tryParse(GsonHelper.getAsString(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"), "element"));

            Element element = ModElements.ELEMENTS.get().getValue(elementRescourceLocation);


            Integer itemcolor = GsonHelper.getAsInt(GsonHelper.getAsJsonObject(pSerializedRecipe, "output") , "itemcolor", -1);
            Integer secitemcolor = GsonHelper.getAsInt(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"), "secitemcolor", -1);

            if(itemcolor == -1){
                itemcolor = null;
            }
            if(secitemcolor == -1){
                secitemcolor = null;
            }

            String displayName = GsonHelper.getAsString(pSerializedRecipe, "displayname", null);

            ItemStack displayItem = GsonHelper.getAsJsonObject(pSerializedRecipe, "displayitem", null) != null ? ShapedRecipe.itemStackFromJson(Objects.requireNonNull(GsonHelper.getAsJsonObject(pSerializedRecipe, "displayitem", null))) : element.defualtItemModel;

            JsonArray ingredients = GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients");

            List<ItemStack> ingredientList = new ArrayList<>();
            List<Element> elementslist = Lists.newArrayList();
            List<CompoundTag> extraDataList = new ArrayList<>();
            List<Integer> counts = Lists.newArrayList();
            List<ItemStack> ingredientListTemp = new ArrayList<>();

            ingredients.forEach((ele)->{
                ingredientList.add(itemStackFromJson(ele.getAsJsonObject()));
                ResourceLocation location = ResourceLocation.tryParse(ele.getAsJsonObject().get("element").getAsString());
                elementslist.add(ModElements.ELEMENTS.get().getValue(location));
                extraDataList.add(generateAdditionalData(ele.getAsJsonObject(), element));
                counts.add(ele.getAsJsonObject().get("count").getAsInt());
            });

            CompoundTag extraData = generateAdditionalData(pSerializedRecipe.getAsJsonObject("output"), element);

            for(int i = 0; i<8-ingredientList.size();i++){
                ingredientListTemp.add(ItemStack.EMPTY);
            }

            ingredientList.addAll(ingredientListTemp);

            List<ElementData> dataList = new ArrayList<>();

            for (int i = 0, elementslistSize = elementslist.size(); i < elementslistSize; i++) {
                com.itskillerluc.alchemicalbrewery.elements.Element element1 = elementslist.get(i);
                dataList.add(new ElementData(null, null, null, null, extraDataList.get(i), element1));
            }

            return new ElementalCombinerRecipe(pRecipeId, output, ingredientList, dataList, counts, new ElementData(displayName, displayItem, itemcolor, secitemcolor, extraData, element) , itemcolor, secitemcolor);
        }

        private static CompoundTag generateAdditionalData(JsonObject parent, com.itskillerluc.alchemicalbrewery.elements.Element element) {
            CompoundTag extraData = element.defaultAdditionalData;
            if(parent.has("extradata")) {
                try {
                    JsonElement jsonElement = parent.get("extradata");
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
            return extraData;
        }

        @Nullable
        @Override
        public ElementalCombinerRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf buf) {
            ItemStack output = buf.readItem();
            List<ItemStack> ingredientlist = buf.readList(FriendlyByteBuf::readItem);
            List<ElementData> elementslist = buf.readList(FriendlyByteBuf::readNbt).stream().map(ElementData::of).toList();
            List<Integer> counts = buf.readList(FriendlyByteBuf::readInt);
            ElementData element = ElementData.of(Objects.requireNonNull(buf.readNbt()));
            int itemcolor = buf.readInt();
            int secitemcolor = buf.readInt();

            return new ElementalCombinerRecipe(pRecipeId, output, ingredientlist, elementslist, counts,element,itemcolor, secitemcolor);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, ElementalCombinerRecipe recipe) {
            buf.writeItemStack(recipe.getResultItem(), false);
            buf.writeCollection(recipe.ingredientList, FriendlyByteBuf::writeItem);
            buf.writeCollection(recipe.elementslist, (friendlyByteBuf, elementData) -> friendlyByteBuf.writeNbt(elementData.toTag()));
            buf.writeCollection(recipe.counts, FriendlyByteBuf::writeInt);
            buf.writeNbt(recipe.Element.toTag());
            buf.writeInt(recipe.itemcolor);
            buf.writeInt(recipe.secitemcolor);
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
