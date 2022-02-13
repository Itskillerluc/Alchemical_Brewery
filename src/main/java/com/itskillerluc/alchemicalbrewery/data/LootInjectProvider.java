package com.itskillerluc.alchemicalbrewery.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import com.itskillerluc.alchemicalbrewery.item.ModItems;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class LootInjectProvider implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final DataGenerator generator;

    LootInjectProvider(DataGenerator generator) {
        this.generator = generator;
    }

    @Override
    public void run(HashCache cache) throws IOException {
        Map<ResourceLocation, LootTable.Builder> tables = new HashMap<>();


        tables.clear();

        CompoundTag poweredTag = new CompoundTag();
        poweredTag.putBoolean("powered", true);
        tables.put(EntityType.CREEPER.getDefaultLootTable(), getEntityLootTable(0.02f, 0.03f,
                getItemLootEntry(ModItems.SULPHUR.get(), 1)));

        for (Map.Entry<ResourceLocation, LootTable.Builder> e : tables.entrySet()) {
            Path path = getPath(generator.getOutputFolder(), e.getKey());
            DataProvider.save(GSON, cache, LootTables.serialize(e.getValue().setParamSet(LootContextParamSets.ENTITY).build()), path);
        }
    }

    @Override
    public String getName() {
        return "SophisticatedBackpacks chest loot additions";
    }

    private static Path getPath(Path root, ResourceLocation id) {
        return root.resolve("data/" + AlchemicalBrewery.MOD_ID + "/loot_tables/inject/" + id.getPath() + ".json");
    }

    private LootPoolEntryContainer.Builder<?> getItemLootEntry(Item item, int weight, int maxCount) {
        return LootItem.lootTableItem(item).setWeight(weight).apply(SetItemCountFunction.setCount(UniformGenerator.between(1, maxCount)));
    }

    private LootPoolEntryContainer.Builder<?> getItemLootEntry(Item item, int weight) {
        return LootItem.lootTableItem(item).setWeight(weight);
    }

    private static LootTable.Builder getLootTable(int emptyWeight, LootPoolEntryContainer.Builder<?>... entries) {
        LootPool.Builder pool = LootPool.lootPool().name("main");
        for (LootPoolEntryContainer.Builder<?> entry : entries) {
            pool.add(entry);
        }
        pool.add(EmptyLootItem.emptyItem().setWeight(emptyWeight));
        return LootTable.lootTable().withPool(pool);
    }

    private static LootTable.Builder getEntityLootTable(float baseChance, float lootingMultiplier, LootPoolEntryContainer.Builder<?>... entries) {
        LootPool.Builder pool = LootPool.lootPool().name("main");
        for (LootPoolEntryContainer.Builder<?> entry : entries) {
            pool.add(entry);
        }
        pool.when(LootItemKilledByPlayerCondition.killedByPlayer());
        return LootTable.lootTable().withPool(pool);
    }
}
