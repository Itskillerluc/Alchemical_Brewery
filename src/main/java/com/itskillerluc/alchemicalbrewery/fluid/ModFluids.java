package com.itskillerluc.alchemicalbrewery.fluid;

import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import com.itskillerluc.alchemicalbrewery.block.ModBlocks;
import com.itskillerluc.alchemicalbrewery.fluid.custom.AcidLiquidBlock;
import com.itskillerluc.alchemicalbrewery.fluid.custom.ChemicalLiquidBlock;
import com.itskillerluc.alchemicalbrewery.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFluids {
    public static final ResourceLocation WATER_STILL_RL = new ResourceLocation("block/water_still");
    public static final ResourceLocation WATER_FLOWING_RL = new ResourceLocation("block/water_flow");
    public static final ResourceLocation WATER_OVERLAY_RL = new ResourceLocation("block/water_overlay");

    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, AlchemicalBrewery.MOD_ID);

    public static final RegistryObject<FlowingFluid> ACID_FLUID = FLUIDS.register("acid_fluid", ()-> new ForgeFlowingFluid.Source(ModFluids.ACID_PROPERTIES));
    public static final RegistryObject<FlowingFluid> ACID_FLOWING = FLUIDS.register("acid_flowing", ()-> new ForgeFlowingFluid.Flowing(ModFluids.ACID_PROPERTIES));

    public static final ForgeFlowingFluid.Properties ACID_PROPERTIES = new ForgeFlowingFluid.Properties(ACID_FLUID, ACID_FLOWING, FluidAttributes.builder(WATER_STILL_RL, WATER_FLOWING_RL).density(100).luminosity(1).viscosity(10).sound(SoundEvents.BUCKET_FILL_LAVA).overlay(WATER_OVERLAY_RL).color(0x12c4506)).slopeFindDistance(3).levelDecreasePerBlock(3).block(ModFluids.ACID_BLOCK).bucket(ModItems.ACID_BUCKET);

    public static final RegistryObject<FlowingFluid> CHEMICAL_FLUID = FLUIDS.register("chemical_fluid", ()-> new ForgeFlowingFluid.Source(ModFluids.CHEMICAL_PROPERTIES));
    public static final RegistryObject<FlowingFluid> CHEMICAL_FLOWING = FLUIDS.register("chemical_flowing", ()-> new ForgeFlowingFluid.Flowing(ModFluids.CHEMICAL_PROPERTIES));

    public static final ForgeFlowingFluid.Properties CHEMICAL_PROPERTIES = new ForgeFlowingFluid.Properties(CHEMICAL_FLUID, CHEMICAL_FLOWING, FluidAttributes.builder(WATER_STILL_RL, WATER_FLOWING_RL).density(100).luminosity(1).viscosity(10).sound(SoundEvents.BUCKET_FILL_LAVA).overlay(WATER_OVERLAY_RL).color(0x8ebacac)).slopeFindDistance(0).levelDecreasePerBlock(7).block(ModFluids.CHEMICAL_BLOCK).bucket(ModItems.CHEMICAL_BUCKET);
    public static final RegistryObject<LiquidBlock> CHEMICAL_BLOCK = ModBlocks.BLOCKS.register("chemical_block", ()->new ChemicalLiquidBlock(ModFluids.CHEMICAL_FLUID, BlockBehaviour.Properties.of(Material.WATER).noCollission().strength(100f).noDrops()));

    public static final RegistryObject<LiquidBlock> ACID_BLOCK = ModBlocks.BLOCKS.register("acid_block", ()->new AcidLiquidBlock(ModFluids.ACID_FLUID, BlockBehaviour.Properties.of(Material.LAVA).noCollission().strength(100f).noDrops().speedFactor(0.5f)));

    public static void register(IEventBus eventBus){
        FLUIDS.register(eventBus);
    }
}
