package com.itskillerluc.alchemicalbrewery.data.recipes;

import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import net.minecraft.core.Registry;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeTypes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, AlchemicalBrewery.MOD_ID);

    public static final RegistryObject<RecipeSerializer<ElementalExtractorRecipe>> EXTRACTOR_SERIALIZER = RECIPE_SERIALIZER.register("extractor", ()->ElementalExtractorRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus){
        RECIPE_SERIALIZER.register(eventBus);
        Registry.register(Registry.RECIPE_TYPE, ElementalExtractorRecipe.Type.ID, ElementalExtractorRecipe.Type.INSTANCE);
    }
}


