package com.itskillerluc.alchemicalbrewery.misc;

import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import com.itskillerluc.alchemicalbrewery.item.custom.elements.Element;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class ModRegistries {
    public static final DeferredRegister<Element> ELEMENTS = DeferredRegister.create(new ResourceLocation(AlchemicalBrewery.MOD_ID, "elements"), AlchemicalBrewery.MOD_ID);

    public static final Supplier<IForgeRegistry<Element>> REGISTRY = ELEMENTS.makeRegistry(Element.class, RegistryBuilder::new);


    public static void register(IEventBus eventBus){
        ELEMENTS.register(eventBus);
    }
}
