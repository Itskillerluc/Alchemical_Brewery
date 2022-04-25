package com.itskillerluc.alchemicalbrewery.data.recipes;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import com.itskillerluc.alchemicalbrewery.item.ModItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ElementalCombinerRecipe implements Recipe<SimpleContainer> {

    private final ResourceLocation id;
    private final ItemStack output;
    private final List<ItemStack> ingredientList;
    private final List<String> elementslist;
    private final boolean haselement;
    private final List<Integer> counts;
    private final String Element;
    private final int itemcolor;
    private final int secitemcolor;

    public ElementalCombinerRecipe(ResourceLocation id, ItemStack output, List<ItemStack> ingredientList, List<String> elementslist, boolean haselement, List<Integer> counts, String element, int itemcolor, int secitemcolor) {
        this.id = id;
        this.output = output;
        this.ingredientList = ingredientList;
        this.elementslist = elementslist;
        this.haselement = haselement;
        this.counts = counts;
        this.Element = element;
        this.itemcolor = itemcolor;
        this.secitemcolor = secitemcolor;
    }

    public int size(){
        return elementslist.size();
    }

    public String getelement() {
        return Element;
    }
    public boolean isHaselement() {
        return haselement;
    }

    public int getItemcolor() {
        return itemcolor;
    }
    public int getSecitemcolor(){return secitemcolor;}
    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        if (haselement) {
            AtomicInteger iteration = new AtomicInteger(0);
            ingredientList.forEach((ele) -> {
                if(ele.is(ModItems.ELEMENT_CRAFTING.get())) {
                    ele.getOrCreateTag().putString("Element", getElement(iteration.get()));
                    try{
                        ele.getOrCreateTag().putInt("ItemColor", pContainer.getItem(iteration.get()).getTag().getInt("ItemColor"));
                        ele.getOrCreateTag().putInt("SecItemColor", pContainer.getItem(iteration.get()).getTag().getInt("SecItemColor"));
                    }catch (NullPointerException except){

                    }
                }
                iteration.getAndIncrement();
            });
            iteration.set(0);
        }
        return ItemStack.isSameItemSameTags(ingredientList.get(0),pContainer.getItem(0)) && ItemStack.isSameItemSameTags(ingredientList.get(1), pContainer.getItem(1)) && ItemStack.isSameItemSameTags(ingredientList.get(2),pContainer.getItem(2)) && ItemStack.isSameItemSameTags(ingredientList.get(3),pContainer.getItem(3)) && ItemStack.isSameItemSameTags(ingredientList.get(4),pContainer.getItem(4)) && ItemStack.isSameItemSameTags(ingredientList.get(5),pContainer.getItem(5)) && ItemStack.isSameItemSameTags(ingredientList.get(6),pContainer.getItem(6)) && ItemStack.isSameItemSameTags(ingredientList.get(7),pContainer.getItem(7));
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer) {
        return output;
    }

    public String getElement(int index){
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

        @Override
        public ElementalCombinerRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));
            String element = GsonHelper.getAsJsonObject(pSerializedRecipe, "output").get("element").getAsString();
            int itemcolor = GsonHelper.getAsJsonObject(pSerializedRecipe, "output").get("itemcolor").getAsInt();
            int secitemcolor = GsonHelper.getAsJsonObject(pSerializedRecipe, "output").get("secitemcolor").getAsInt();

            JsonArray ingredients = GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients");

            List<ItemStack> ingredientList = Lists.newArrayList();
            List<String> elementslist = Lists.newArrayList();
            List<Integer> counts = Lists.newArrayList();
            List<ItemStack> ingredientListTemp = new ArrayList<ItemStack>();

            ingredients.forEach((ele)->{
                ingredientList.add(itemStackFromJson(ele.getAsJsonObject()));

                elementslist.add(ele.getAsJsonObject().get("element").getAsString());
                counts.add(ele.getAsJsonObject().get("count").getAsInt());
            });

            for(int i = 0; i<8-ingredientList.size();i++){
                ingredientListTemp.add(ItemStack.EMPTY);
            }
            ingredientListTemp.forEach((ele)->{
                ingredientList.add(ele);
            });
            boolean haselement = GsonHelper.getAsBoolean(pSerializedRecipe,"haselement");

            return new ElementalCombinerRecipe(pRecipeId, output, ingredientList, elementslist, haselement, counts, element, itemcolor, secitemcolor);
        }

        @Nullable
        @Override
        public ElementalCombinerRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf buf) {

            ItemStack output = buf.readItem();
            List<ItemStack> ingredientlist = buf.readList(FriendlyByteBuf::readItem);
            List<String> elementslist = buf.readList(FriendlyByteBuf::readUtf);
            boolean haselement = buf.readBoolean();
            List<Integer> counts = buf.readList(FriendlyByteBuf::readInt);
            String element = buf.readUtf();
            int itemcolor = buf.readInt();
            int secitemcolor = buf.readInt();
            return new ElementalCombinerRecipe(pRecipeId, output, ingredientlist, elementslist, haselement, counts,element,itemcolor, secitemcolor);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, ElementalCombinerRecipe recipe) {
            buf.writeItemStack(recipe.getResultItem(), false);
            buf.writeCollection(recipe.ingredientList, FriendlyByteBuf::writeItem);
            buf.writeCollection(recipe.elementslist, FriendlyByteBuf::writeUtf);
            buf.writeBoolean(recipe.haselement);
            buf.writeCollection(recipe.counts, FriendlyByteBuf::writeInt);
            buf.writeUtf(recipe.Element);
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
