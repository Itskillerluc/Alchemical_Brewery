package com.itskillerluc.alchemicalbrewery.events;

import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import com.itskillerluc.alchemicalbrewery.item.ModItems;
import com.itskillerluc.alchemicalbrewery.item.custom.ElementBasic;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AlchemicalBrewery.MOD_ID)
public class EventHandler {
    /**
     * converts the element_basic into element_use when item is thrown into the void
     */
    @SubscribeEvent
    @SuppressWarnings("unused")
    public static void entityKilled(EntityLeaveWorldEvent event){
        if (event.getEntity().level.isClientSide()) {
            return;
        }
        Entity entity = event.getEntity();
        Level level = event.getWorld();
        if (entity instanceof ItemEntity itemEntity && (itemEntity).getItem().is(ModItems.ELEMENT_BASIC.get())) {
            ElementBasic.convert(itemEntity, level);
        }
    }
}
