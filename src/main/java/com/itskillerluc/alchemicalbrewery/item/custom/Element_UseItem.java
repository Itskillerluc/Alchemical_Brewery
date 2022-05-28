package com.itskillerluc.alchemicalbrewery.item.custom;


import com.itskillerluc.alchemicalbrewery.elements.ModElements;
import com.itskillerluc.alchemicalbrewery.item.custom.elements.ElementInit;
import com.itskillerluc.alchemicalbrewery.item.custom.elements.elementfunctions;
import com.mojang.logging.LogUtils;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;


public class Element_UseItem extends Element_Basic {
    public Element_UseItem(Properties pProperties) {
        super(pProperties);
    }


    /**
     * create a dynamic name
     */

    @Override
    public Component getName(ItemStack pStack) {
        String Name = null;
        if(pStack.hasTag()){
            String Element = pStack.getTag().getString("Element");
            Name = Element;
            if (Element != null) {
                if (Element.contains("-")) {
                    Name = Element.substring(0, Element.indexOf('-'));
                }
            }
        }
        return pStack.hasTag() ? new TranslatableComponent(getDescriptionId(), "\u00A7a(" + Name + ")") : new TranslatableComponent("item.alchemicalbrewery.element_use");
    }

    @Override
    public Rarity getRarity(ItemStack pStack) {
        try{
            return (pStack.getTag().getBoolean("Creative")) ? Rarity.EPIC : Rarity.COMMON;
        }catch (NullPointerException error){
            return Rarity.COMMON;
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        ModElements.TEST.get().SetArgsWrapper(pContext, true);
        String ElementRaw = pContext.getItemInHand().hasTag() ? pContext.getItemInHand().getTag().getString("Element") : null;
        String Element = pContext.getItemInHand().getTag().getString("Element");
        if (ElementRaw != null) {
            if (ElementRaw.contains("-")) {
                if (ElementRaw.substring(ElementRaw.indexOf('-')).length() < 1) {
                    try {
                        throw new ResourceLocationException("found - sign without Element value behind it. Correct syntax should be: Displayname-RealElement or Realelement. Found in:" + ElementRaw);

                    }catch (ResourceLocationException exception){
                        exception.printStackTrace();
                    }
                }
                Element = ElementRaw.substring(ElementRaw.indexOf('-') + 1);
            }
        }

        if(!pContext.getPlayer().isCrouching()) {
            try {
                //Run the element that is stored in the nbt
                if(ElementInit.functions.containsKey(Element)) {
                    ElementInit.functions.get(Element).run(pContext.getClickedFace(), pContext.getClickedPos(), pContext.getLevel(), pContext.getPlayer(), pContext.getHand(), true, ElementInit.arguments.get(Element).apply(pContext));
                }else {
                    elementfunctions.block(pContext.getClickedFace(), pContext.getClickedPos(), pContext.getLevel(), pContext.getPlayer(), pContext.getHand(), true, ElementInit.arguments.get("Block").apply(pContext));
                }

            } catch (NullPointerException | ResourceLocationException exception) {
                if (pContext.getItemInHand().hasTag()) {
                    LogUtils.getLogger().debug(ElementRaw + " is not a valid element type");
                } else {
                    LogUtils.getLogger().debug("No element type found");
                }
            }
        }
        return super.useOn(pContext);
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return true;
    }

    //this method is currently not being used, but itll be later.
    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        Level plevel = pAttacker.getLevel();
        if(pTarget.isDeadOrDying()){
            //TODO: add config setting (default is this).
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

    /**
     * Sets the color to the nbt that is provided
     */
    public static class ColorHandler implements ItemColor{
        @Override
        public int getColor(ItemStack pStack, int pTintIndex) {
            if(pStack.hasTag()){
                switch (pTintIndex){
                    case 0 -> {
                        assert pStack.getTag() != null;
                        return pStack.getTag().getInt("SecItemColor");}
                    case 1 -> {
                        assert pStack.getTag() != null;
                        return pStack.getTag().getInt("ItemColor");}
                    default -> {return 15869935;}
                }
            }else{
                if (pTintIndex == 0) {
                    return 15869935;
                }else return -1;
            }
        }
    }

}
