package com.itskillerluc.alchemicalbrewery.block;

import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import com.itskillerluc.alchemicalbrewery.item.ModCreativeTab;
import com.itskillerluc.alchemicalbrewery.item.ModItems;
import com.itskillerluc.alchemicalbrewery.item.custom.AcidItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AlchemicalBrewery.MOD_ID);

    public static RegistryObject<Block> ELEMENTALEXTRACTOR = registerBlock("elementalextractor", ()-> new Block(BlockBehaviour.Properties.of(Material.HEAVY_METAL)));

    private static <T extends Block>RegistryObject<T> registerBlock(String name, Supplier<T> block){
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        return toReturn;
    }

    private static <T extends Block>void registerBlockItem(String name, RegistryObject<T> block){
        ModItems.ITEMS.register(name, ()-> new BlockItem(block.get(), new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB)));
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
