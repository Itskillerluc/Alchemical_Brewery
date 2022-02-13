package com.itskillerluc.alchemicalbrewery.util;


import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.List;

public class LootHandler{
    private LootHandler() {}

    private static final List<String> ENTITY_TABLES = List.of("creeper");


    public static void registerEventBusListeners(IEventBus eventBus){
        eventBus.addListener(LootHandler::lootLoad);
    }
    private static void lootLoad(LootTableLoadEvent evt){
        String entitesPrefix = "minecraft:entities/";
        String name = evt.getName().toString();

        if(name.startsWith(entitesPrefix)&&ENTITY_TABLES.contains(name.substring(entitesPrefix.length()))){
            String file = name.substring("minecraft:".length());
            evt.getTable().addPool(getInjectPool(file));
        }
    }

    public static LootPool getInjectPool(String entryName){
        return LootPool.lootPool().add(getInjectEntry(entryName)).setBonusRolls(UniformGenerator.between(0, 1)).name("alchemicalbrewery_inject_pool").build();
    }

    public static LootPoolEntryContainer.Builder<?> getInjectEntry(String name){
        return LootTableReference.lootTableReference(new ResourceLocation(AlchemicalBrewery.MOD_ID, "inject/" + name)).setWeight(1);
    }

}
