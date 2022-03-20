package com.itskillerluc.alchemicalbrewery.item.custom;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class Element_Basic extends Item {
    public Element_Basic(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Component getName(ItemStack pStack) {
        return pStack.hasTag() ? new TranslatableComponent(getDescriptionId(), "\u00A7a(" + pStack.getTag().getString("Element") + ")") : new TranslatableComponent("item.alchemicalbrewery.element_basic");
    }

    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        Level plevel = pAttacker.getLevel();
        if(pTarget.isDeadOrDying()){
            //TODO: add config setting (optional. default is this). also this has to be on a different element item.
            if(plevel.getServer().isDedicatedServer()){
                if (pTarget instanceof Player){
                    if(pAttacker instanceof Player){
                        pAttacker.sendMessage(new TextComponent("TEST"), pAttacker.getUUID());
                    }
                }
            }else{
                if (pTarget instanceof LivingEntity){
                    if(pAttacker instanceof Player){
                        pAttacker.sendMessage(new TextComponent("TEST"), pAttacker.getUUID());
                    }
                }
            }
        }
        return true;
    }

    public static class ColorHandler implements ItemColor{
        CompoundTag tag = new CompoundTag();

        @Override
        public int getColor(ItemStack pStack, int pTintIndex) {
            return pStack.hasTag() ? pStack.getTag().getInt("ItemColor") : -1;
        }
    }
}
