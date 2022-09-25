package com.itskillerluc.alchemicalbrewery.entity;

import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import com.itskillerluc.alchemicalbrewery.entity.custom.ElementProjectileEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes {
    public static DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, AlchemicalBrewery.MOD_ID);

    public static final RegistryObject<EntityType<ElementProjectileEntity>> ELEMENT_PROJECTILE = ENTITY_TYPES.register("element_projectile", ()-> EntityType.Builder.<ElementProjectileEntity>of(ElementProjectileEntity::new, MobCategory.MISC).fireImmune().sized(0.5f, 0.5f).build(new ResourceLocation(AlchemicalBrewery.MOD_ID, "element_projectile").toString()));

    public static void register(IEventBus eventBus){
        ENTITY_TYPES.register(eventBus);
    }
}
