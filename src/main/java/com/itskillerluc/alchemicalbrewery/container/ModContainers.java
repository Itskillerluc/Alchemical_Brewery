package com.itskillerluc.alchemicalbrewery.container;

import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@SuppressWarnings("SpellCheckingInspection")
public class ModContainers {
    public static DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, AlchemicalBrewery.MOD_ID);

    public static final RegistryObject<MenuType<ElementalExtractorContainer>> ELEMENTALEXTRACTORCONTAINER = registerMenuType(ElementalExtractorContainer::new, "elementalextractorcontainer");
    public static final RegistryObject<MenuType<ElementalInjectorContainer>> ELEMENTALINJECTORCONTAINER = registerMenuType(ElementalInjectorContainer::new, "elementalinjectorcontainer");

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(IContainerFactory<T> factory, String name) {
        return CONTAINERS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus){
        CONTAINERS.register(eventBus);
    }
}
