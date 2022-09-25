package com.itskillerluc.alchemicalbrewery.events;

import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import com.itskillerluc.alchemicalbrewery.entity.ModEntityTypes;

import com.itskillerluc.alchemicalbrewery.entity.custom.ElementProjectileModel;
import com.itskillerluc.alchemicalbrewery.entity.custom.ElementProjectileRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = AlchemicalBrewery.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class EntityRendererHandler 
{
	@SubscribeEvent
	public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event)
	{
		event.registerLayerDefinition(ElementProjectileModel.LAYER_LOCATION, ElementProjectileModel::createBodyLayer);
	}
	
	@SubscribeEvent
	public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event)
	{
        event.registerEntityRenderer(ModEntityTypes.ELEMENT_PROJECTILE.get(), ElementProjectileRenderer::new);
	}
}
