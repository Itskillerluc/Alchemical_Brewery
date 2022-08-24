package com.itskillerluc.alchemicalbrewery.block;

import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import com.itskillerluc.alchemicalbrewery.block.custom.ElementalCombinerBlock;
import com.itskillerluc.alchemicalbrewery.block.custom.ElementalExtractorBlock;
import com.itskillerluc.alchemicalbrewery.block.custom.ElementalInjectorBlock;
import com.itskillerluc.alchemicalbrewery.block.custom.VoidWell;
import com.itskillerluc.alchemicalbrewery.item.ModCreativeTab;
import com.itskillerluc.alchemicalbrewery.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AlchemicalBrewery.MOD_ID);

    public static final RegistryObject<Block> ELEMENTALEXTRACTOR = registerBlock("elementalextractor", () -> new ElementalExtractorBlock(BlockBehaviour.Properties.of(Material.HEAVY_METAL).strength(5, 5).requiresCorrectToolForDrops().noOcclusion()), 1);
    public static final RegistryObject<Block> ELEMENTALINJECTOR = registerBlock("elementalinjector",()-> new ElementalInjectorBlock(BlockBehaviour.Properties.of(Material.HEAVY_METAL).strength(5, 5).requiresCorrectToolForDrops().noOcclusion()), 1);
    public static final RegistryObject<Block> ELEMENTALCOMBINER = registerBlock("elementalcombiner",()-> new ElementalCombinerBlock(BlockBehaviour.Properties.of(Material.HEAVY_METAL).strength(5, 5).requiresCorrectToolForDrops().noOcclusion()), 1);
    public static final RegistryObject<Block> VOIDWELL = registerBlock("voidwell", () -> new VoidWell(BlockBehaviour.Properties.of(Material.STONE).strength(7, 100).requiresCorrectToolForDrops().noOcclusion()), 64);


    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, int stackSize) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, stackSize);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, RegistryObject<T> block, int stackSize) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(),
                new Item.Properties().tab(ModCreativeTab.ALCHEMICALBREWERY_TAB).stacksTo(stackSize)));
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
