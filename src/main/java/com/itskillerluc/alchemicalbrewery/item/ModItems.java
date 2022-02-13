package com.itskillerluc.alchemicalbrewery.item;

import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AlchemicalBrewery.MOD_ID);
    //TODO: Change the tab to custom tab
    public static final RegistryObject<Item> SULPHUR = ITEMS.register("sulphur", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));
    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
