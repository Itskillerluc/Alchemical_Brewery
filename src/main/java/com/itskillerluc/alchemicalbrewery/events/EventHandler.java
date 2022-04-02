package com.itskillerluc.alchemicalbrewery.events;


import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import com.itskillerluc.alchemicalbrewery.item.ModItems;
import com.itskillerluc.alchemicalbrewery.item.custom.Element_Basic;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AlchemicalBrewery.MOD_ID)
public class EventHandler {
    @SubscribeEvent
    public static void entityKilled(EntityLeaveWorldEvent event){
        if(!event.getEntity().level.isClientSide()) {
            Entity entity = event.getEntity();
            Level level = event.getWorld();
            if (entity instanceof ItemEntity) {
                if (((ItemEntity) entity).getItem().is(ModItems.ELEMENT_BASIC.get())) {
                    Element_Basic.convert(((ItemEntity) entity), level);
                }
            }
        }
    }
}
