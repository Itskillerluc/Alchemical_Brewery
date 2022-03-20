package com.itskillerluc.alchemicalbrewery.item;

import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import com.itskillerluc.alchemicalbrewery.fluid.ModFluids;
import com.itskillerluc.alchemicalbrewery.item.custom.AcidItem;
import com.itskillerluc.alchemicalbrewery.item.custom.Element_Basic;
import com.itskillerluc.alchemicalbrewery.item.custom.Element_Crafting;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AlchemicalBrewery.MOD_ID);
    public static final RegistryObject<Item> SULPHUR = ITEMS.register("sulphur", () -> new Item(new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB)));
    public static final RegistryObject<Item> ACID = ITEMS.register("acid", () -> new AcidItem(new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB).stacksTo(16)));
    public static final RegistryObject<Item> SALT = ITEMS.register("salt", () -> new Item(new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB)));
    public static final RegistryObject<Item> CAPSULE_SMALL = ITEMS.register("capsule_small", () -> new Item(new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB)));
    public static final RegistryObject<Item> CAPSULE_MEDIUM = ITEMS.register("capsule_medium", () -> new Item(new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB)));
    public static final RegistryObject<Item> CAPSULE_LARGE = ITEMS.register("capsule_large", () -> new Item(new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB)));
    public static final RegistryObject<Item> ELEMENT_BASIC = ITEMS.register("element_basic", () -> new Element_Basic(new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB)){
        @Override
        public boolean isFoil(ItemStack pStack) {
            return true;
        }
    });
    public static final RegistryObject<Item> ELEMENT_CRAFTING = ITEMS.register("element_crafting", () -> new Element_Crafting(new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB)){
        @Override
        public boolean isFoil(ItemStack pStack) {
            return true;
        }
    });
    public static final RegistryObject<Item> ACID_BUCKET = ITEMS.register("acid_bucket", () -> new BucketItem(()->ModFluids.ACID_FLUID.get(),new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB).stacksTo(1)));
    public static final RegistryObject<Item> CHEMICAL_BUCKET = ITEMS.register("chemical_bucket", () -> new BucketItem(()->ModFluids.CHEMICAL_FLUID.get(),new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB).stacksTo(1)));
    public static final RegistryObject<Item> FUELMIX = ITEMS.register("fuelmix", () -> new Item(new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB)){
        @Override
        public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
            return 50000;
        }
    });

    public static final RegistryObject<Item> ACTIVATEDCHARCOAL = ITEMS.register("activatedcharcoal", () -> new Item(new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB)));
    public static final RegistryObject<Item> CORRODED_GOLD = ITEMS.register("corroded_gold", () -> new Item(new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB)));
    public static final RegistryObject<Item> HARDENED_DIAMOND = ITEMS.register("hardened_diamond", () -> new Item(new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB)));


    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }

}
