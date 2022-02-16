package com.itskillerluc.alchemicalbrewery.item;

import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import com.itskillerluc.alchemicalbrewery.fluid.ModFluids;
import com.itskillerluc.alchemicalbrewery.item.custom.AcidItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AlchemicalBrewery.MOD_ID);
    public static final RegistryObject<Item> SULPHUR = ITEMS.register("sulphur", () -> new Item(new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB)));
    public static final RegistryObject<Item> ACID = ITEMS.register("acid", () -> new AcidItem(new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB).stacksTo(16)));
    public static final RegistryObject<Item> SALT = ITEMS.register("salt", () -> new Item(new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB)));
    public static final RegistryObject<Item> ACID_BUCKET = ITEMS.register("acid_bucket", () -> new BucketItem(()->ModFluids.ACID_FLUID.get(),new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB).stacksTo(1)));
    public static final RegistryObject<Item> CHEMICAL_BUCKET = ITEMS.register("chemical_bucket", () -> new BucketItem(()->ModFluids.CHEMICAL_FLUID.get(),new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB).stacksTo(1)));
    public static final RegistryObject<Item> FUELMIX = ITEMS.register("fuelmix", () -> new Item(new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB)){
        @Override
        public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
            return 50000;
        }
    });

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
