package com.itskillerluc.alchemicalbrewery;

import com.itskillerluc.alchemicalbrewery.block.ModBlocks;
import com.itskillerluc.alchemicalbrewery.container.ModContainers;
import com.itskillerluc.alchemicalbrewery.data.recipes.ModRecipeTypes;
import com.itskillerluc.alchemicalbrewery.data.recipes.brewRecipes;
import com.itskillerluc.alchemicalbrewery.entity.ModEntityTypes;
import com.itskillerluc.alchemicalbrewery.entity.custom.ElementProjectileRenderer;
import com.itskillerluc.alchemicalbrewery.fluid.ModFluids;
import com.itskillerluc.alchemicalbrewery.item.ModItems;
import com.itskillerluc.alchemicalbrewery.item.custom.Element_Basic;
import com.itskillerluc.alchemicalbrewery.item.custom.Element_Crafting;
import com.itskillerluc.alchemicalbrewery.item.custom.Element_UseItem;
import com.itskillerluc.alchemicalbrewery.item.custom.WandItem;
import com.itskillerluc.alchemicalbrewery.item.custom.elements.ElementInit;
import com.itskillerluc.alchemicalbrewery.screen.ElementalExtractorScreen;
import com.itskillerluc.alchemicalbrewery.screen.ElementalInjectorScreen;
import com.itskillerluc.alchemicalbrewery.tileentity.ModTileEntities;
import com.itskillerluc.alchemicalbrewery.util.LootHandler;
import com.itskillerluc.alchemicalbrewery.util.ModItemProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(AlchemicalBrewery.MOD_ID)
public class AlchemicalBrewery
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "alchemicalbrewery";

    public AlchemicalBrewery() {
        // Register the setup method for modloading
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.register(eventBus);
        ModFluids.register(eventBus);
        ModBlocks.register(eventBus);
        ModContainers.register(eventBus);
        ModTileEntities.register(eventBus);
        ModRecipeTypes.register(eventBus);
        ModEntityTypes.register(eventBus);

        eventBus.addListener(this::setup);
        eventBus.addListener(this::doClientStuff);
        MinecraftForge.EVENT_BUS.register(this);


        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus forgeeventbus = MinecraftForge.EVENT_BUS;
        LootHandler.registerEventBusListeners(forgeeventbus);
    }


    private void setup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> {
            registerRecipes();
        });
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    public void registerRecipes() {
        new brewRecipes().registerRecipes();
    }

    private void doClientStuff(final FMLClientSetupEvent event){
        event.enqueueWork(()->{
            MenuScreens.register(ModContainers.ELEMENTALEXTRACTORCONTAINER.get(), ElementalExtractorScreen::new);
            MenuScreens.register(ModContainers.ELEMENTALINJECTORCONTAINER.get(), ElementalInjectorScreen::new);
            ItemBlockRenderTypes.setRenderLayer(ModBlocks.ELEMENTALEXTRACTOR.get(), RenderType.translucent());

            Minecraft.getInstance().getItemColors().register(new Element_UseItem.ColorHandler(), ModItems.ELEMENT_USE.get());
            Minecraft.getInstance().getItemColors().register(new Element_Crafting.ColorHandler(), ModItems.ELEMENT_CRAFTING.get());
            Minecraft.getInstance().getItemColors().register(new Element_Basic.ColorHandler(), ModItems.ELEMENT_BASIC.get());
            Minecraft.getInstance().getItemColors().register(new WandItem.ColorHandler(), ModItems.WAND_ITEM.get());

            ModItemProperties.addCustomItemProperties();
        });

        EntityRenderers.register(ModEntityTypes.ELEMENTPROJECTILE.get(), ElementProjectileRenderer::new);
    }


}
