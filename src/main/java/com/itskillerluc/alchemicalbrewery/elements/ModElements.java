package com.itskillerluc.alchemicalbrewery.elements;

import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;


public class ModElements {
    public static final DeferredRegister<Element> REGISTER = DeferredRegister.create(new ResourceLocation(AlchemicalBrewery.MOD_ID, "elements"), AlchemicalBrewery.MOD_ID);

    public static final Supplier<IForgeRegistry<Element>> ELEMENTS = REGISTER.makeRegistry(Element.class, RegistryBuilder::new);

    public static final RegistryObject<Element> LAVA = REGISTER.register("lava", ()->new LavaElement("Lava"));
    public static final RegistryObject<Element> WATER = REGISTER.register("water", ()->new WaterElement("Water"));
    public static final RegistryObject<Element> ENDER = REGISTER.register("ender",()->new EnderElement("Ender"));
    public static final RegistryObject<Element> EMPTY = REGISTER.register("empty",()->new EmptyElement("Empty"));

    public static void register(IEventBus eventBus){
        REGISTER.register(eventBus);
    }
}
