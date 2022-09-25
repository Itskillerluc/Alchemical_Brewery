package com.itskillerluc.alchemicalbrewery.item;

import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import com.itskillerluc.alchemicalbrewery.fluid.ModFluids;
import com.itskillerluc.alchemicalbrewery.item.custom.*;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AlchemicalBrewery.MOD_ID);
    public static final RegistryObject<Item> SULPHUR = ITEMS.register("sulphur", () -> new Item(new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB)));
    public static final RegistryObject<Item> ACID = ITEMS.register("acid", () -> new AcidItem(new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB).stacksTo(16)));
    public static final RegistryObject<Item> SALT = ITEMS.register("salt", () -> new Item(new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB)));
    public static final RegistryObject<Item> CAPSULE_SMALL = ITEMS.register("capsule_small", () -> new Item(new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB)));
    public static final RegistryObject<Item> CAPSULE_MEDIUM = ITEMS.register("capsule_medium", () -> new Item(new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB)));
    public static final RegistryObject<Item> CAPSULE_LARGE = ITEMS.register("capsule_large", () -> new Item(new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB)));
    public static final RegistryObject<Item> ELEMENT_BASIC = ITEMS.register("element_basic", () -> new ElementBasic(new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB)){
        @Override
        public boolean isFoil(@NotNull ItemStack pStack) {
            return true;
        }
    });
    public static final RegistryObject<Item> ELEMENT_CRAFTING = ITEMS.register("element_crafting", () -> new ElementCrafting(new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB)){
        @Override
        public boolean isFoil(@NotNull ItemStack pStack) {
            return true;
        }
    });
    public static final RegistryObject<Item> ACID_BUCKET = ITEMS.register("acid_bucket", () -> new BucketItem(ModFluids.ACID_FLUID,new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB).stacksTo(1)));
    public static final RegistryObject<Item> CHEMICAL_BUCKET = ITEMS.register("chemical_bucket", () -> new BucketItem(ModFluids.CHEMICAL_FLUID,new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB).stacksTo(1)));
    public static final RegistryObject<Item> FUEL_MIX = ITEMS.register("fuel_mix", () -> new Item(new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB)){
        @Override
        public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
            return 50000;
        }
    });

    public static final RegistryObject<Item> ACTIVATED_CHARCOAL = ITEMS.register("activated_charcoal", () -> new Item(new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB)));
    public static final RegistryObject<Item> CORRODED_GOLD = ITEMS.register("corroded_gold", () -> new Item(new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB)));
    public static final RegistryObject<Item> HARDENED_DIAMOND = ITEMS.register("hardened_diamond", () -> new Item(new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB)));
    public static final RegistryObject<Item> ELEMENT_USE = ITEMS.register("element_use", ()-> new ElementUseItem(new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB)));
    public static final RegistryObject<Item> WAND_ITEM = ITEMS.register("wand", ()-> new WandItem(new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB)));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }

}
