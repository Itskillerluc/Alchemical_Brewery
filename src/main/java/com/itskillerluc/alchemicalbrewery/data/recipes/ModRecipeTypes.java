package com.itskillerluc.alchemicalbrewery.data.recipes;

import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;

public class ModRecipeTypes {

    public static void register(IEventBus eventBus){
        eventBus.addGenericListener(RecipeSerializer.class, (RegistryEvent.Register<RecipeSerializer<?>> event) -> {
            ChemicalLiquidRecipe.Type.INSTANCE = new ChemicalLiquidRecipe.Type();
            Registry.register(Registry.RECIPE_TYPE, ChemicalLiquidRecipe.Type.ID, ChemicalLiquidRecipe.Type.INSTANCE);

            ElementalExtractorRecipe.Type.INSTANCE = new ElementalExtractorRecipe.Type();
            Registry.register(Registry.RECIPE_TYPE, ElementalExtractorRecipe.Type.ID, ElementalExtractorRecipe.Type.INSTANCE);
        });

        RECIPE_SERIALIZER.register(eventBus);
    }

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, AlchemicalBrewery.MOD_ID);

    public static final RegistryObject<RecipeSerializer<ElementalExtractorRecipe>> EXTRACTOR_SERIALIZER = RECIPE_SERIALIZER.register("extracting", ()->ElementalExtractorRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeSerializer<ChemicalLiquidRecipe>> CHEMICAL_SERIALIZER = RECIPE_SERIALIZER.register("bathing", ()->ChemicalLiquidRecipe.Serializer.INSTANCE);


    public static Map<ResourceLocation, Recipe<?>> getRecipes(RecipeType<?> type, RecipeManager manager) {
        final Map<RecipeType<?>, Map<ResourceLocation, Recipe<?>>> recipes = ObfuscationReflectionHelper
                .getPrivateValue(RecipeManager.class, manager, "recipes");
        return recipes.get(type);
    }
}


