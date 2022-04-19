package com.itskillerluc.alchemicalbrewery.item.custom.elements;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class ElementInit {
    public static Map<String, ElementFunction> functions = new HashMap<>();
    public static Map<String, ElementArgs> arguments = new HashMap<>();
    public static Map<String, ElementArgsEntity> entityargs = new HashMap<>();

    public static final ElementFunction LAVA = register("Lava", elementfunctions::lava);
    public static final ElementArgs LAVAARG = registerargs("Lava", (pContext)-> new String[]{""});
    public static final ElementArgsEntity ELAVAARG = registereargs("Lava", (level, owner, entity)-> new String[]{""});
    public static final ElementFunction WATER = register("Water", elementfunctions::water);
    public static final ElementArgs WATERARG = registerargs("Water", (UseOnContext pContext)-> new String[]{""});
    public static final ElementArgsEntity EWATERARG = registereargs("Water", (level, owner, entity)-> new String[]{""});
    public static final ElementArgs BLOCKARG = registerargs("Block", (UseOnContext pContext)-> new String[]{pContext.getItemInHand().getOrCreateTag().getString("Element")});
    public static final ElementArgsEntity EBLOCKARG = registereargs("Block", (level, owner, entity)-> new String[]{entity.getElement()});

    public static ElementFunction register(String key, ElementFunction function){
        return functions.put(key, function);
    }
    public static ElementArgs registerargs(String key, ElementArgs args){
        return arguments.put(key, args);
    }
    public static ElementArgsEntity registereargs(String key, ElementArgsEntity args){
        return entityargs.put(key, args);
    }
}
