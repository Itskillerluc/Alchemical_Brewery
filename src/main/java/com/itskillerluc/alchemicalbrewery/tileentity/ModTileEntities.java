package com.itskillerluc.alchemicalbrewery.tileentity;
//TODO
import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import com.itskillerluc.alchemicalbrewery.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModTileEntities {
    public static DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, AlchemicalBrewery.MOD_ID);

    public static RegistryObject<BlockEntityType<ElementalExtractorTile>> ELEMENTALEXTRACTORTILE =  TILE_ENTITIES.register("elementalextractortile",()->BlockEntityType.Builder.of(ElementalExtractorTile::new, ModBlocks.ELEMENTALEXTRACTOR.get()).build(null));
    public static RegistryObject<BlockEntityType<ElementalInjectorTile>> ELEMENTALINJECTORTILE = TILE_ENTITIES.register("elementalinjectortile",()->BlockEntityType.Builder.of(ElementalInjectorTile::new, ModBlocks.ELEMENTALINJECTOR.get()).build(null));
    public static RegistryObject<BlockEntityType<ElementalCombinerTile>> ELEMENTALCOMBINER = TILE_ENTITIES.register("elementalcombinertile",()->BlockEntityType.Builder.of(ElementalCombinerTile::new, ModBlocks.ELEMENTALCOMBINER.get()).build(null));

    public static void register(IEventBus eventBus){
        TILE_ENTITIES.register(eventBus);
    }

}
