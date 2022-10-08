package com.itskillerluc.alchemicalbrewery.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("deprecation")
public class ChargeLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Codec<Map<Either<TagKey<Item>, Item>, Integer>> codec = Codec.unboundedMap(Codec.either(TagKey.hashedCodec(Registry.ITEM_REGISTRY), Registry.ITEM.byNameCodec()), Codec.INT);
    private static final Map<Either<TagKey<Item>, Item>, Integer> charges = new HashMap<>();

    public static final Map<Ingredient, Integer> chargeValues = new HashMap<>();

    public ChargeLoader() {
        super(GSON, "charges");
    }

    public static boolean contains(ItemStack ingredient){
        return chargeValues.keySet().stream().anyMatch(ingredient1 -> ingredient1.test(ingredient));
    }

    public static int getCharge(ItemStack key){
        if (chargeValues.keySet().stream().anyMatch(ingredient -> ingredient.test(key))){
            return chargeValues.get(chargeValues.keySet().stream().filter(ingredient -> ingredient.test(key)).findFirst().orElseThrow());
        }
        LOGGER.debug(key.toString() + " doesn't have charge");
        return 5;
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, JsonElement> pObject, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pProfiler) {
        for (Map.Entry<ResourceLocation, JsonElement> entry : pObject.entrySet()) {
            var entries = entry.getValue().getAsJsonObject().getAsJsonObject("entries");
            var values = codec.parse(JsonOps.INSTANCE, entries).get();
            if (values.left().isPresent()) {
                for (int size = values.left().get().size(); size > 0; size--) {
                    values.left().get().forEach(charges::putIfAbsent);
                }
            }
        }
        var ingredients = charges.keySet().stream().map(ele -> {
            AtomicReference<Ingredient> toReturn = new AtomicReference<>();
            ele.ifRight(var -> toReturn.set(Ingredient.of(var))).ifLeft(var -> toReturn.set(Ingredient.of(var)));
            return toReturn.get();
        }).toList();

        for (int i = 0, ingredientsSize = ingredients.size(); i < ingredientsSize; i++) {
            chargeValues.putIfAbsent(ingredients.get(i), List.copyOf(charges.values()).get(i));
        }
    }
}
