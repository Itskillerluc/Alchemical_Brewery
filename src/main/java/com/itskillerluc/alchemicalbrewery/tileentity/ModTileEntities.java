package com.itskillerluc.alchemicalbrewery.tileentity;

import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import com.itskillerluc.alchemicalbrewery.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModTileEntities {
    public static DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, AlchemicalBrewery.MOD_ID);

    @SuppressWarnings("ConstantConditions")
    public static RegistryObject<BlockEntityType<ElementalExtractorTile>> ELEMENTAL_EXTRACTOR_TILE =  TILE_ENTITIES.register("elemental_extractor_tile",()->BlockEntityType.Builder.of(ElementalExtractorTile::new, ModBlocks.ELEMENTALEXTRACTOR.get()).build(null));
    @SuppressWarnings("ConstantConditions")
    public static RegistryObject<BlockEntityType<ElementalInjectorTile>> ELEMENTAL_INJECTOR_TILE = TILE_ENTITIES.register("elemental_injector_tile",()->BlockEntityType.Builder.of(ElementalInjectorTile::new, ModBlocks.ELEMENTALINJECTOR.get()).build(null));
    @SuppressWarnings("ConstantConditions")
    public static RegistryObject<BlockEntityType<ElementalCombinerTile>> ELEMENTAL_COMBINER = TILE_ENTITIES.register("elemental_combiner_tile",()->BlockEntityType.Builder.of(ElementalCombinerTile::new, ModBlocks.ELEMENTALCOMBINER.get()).build(null));

    public static void register(IEventBus eventBus){
        TILE_ENTITIES.register(eventBus);
    }

}
