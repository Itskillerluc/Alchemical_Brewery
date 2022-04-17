package com.itskillerluc.alchemicalbrewery.item.custom;


import com.itskillerluc.alchemicalbrewery.util.Utilities;
import com.mojang.logging.LogUtils;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;


public class Element_UseItem extends Element_Basic {
    public Element_UseItem(Properties pProperties) {
        super(pProperties);
    }

    /**
     * Contains the functions for elements.
     */
    public static class elementfunctions{

        public static void Lava(Direction dir, BlockPos pos, Level level, LivingEntity user, InteractionHand hand){
            BlockPos newpos;
            newpos = switch (dir){
                case UP -> pos.above();
                case DOWN -> pos.below();
                case EAST -> pos.east();
                case WEST -> pos.west();
                case NORTH -> pos.north();
                case SOUTH -> pos.south();
            };
            if(!level.isClientSide()&&level.getBlockState(newpos).getMaterial().isReplaceable()){
                level.setBlock(newpos, Blocks.LAVA.defaultBlockState(), 2);
                if(user != null) {
                    if (user.getItemInHand(hand).hasTag()) {
                        if (!user.getItemInHand(hand).getTag().getBoolean("Creative")) {
                            user.setItemInHand(hand, Utilities.DecodeStackTags(new ItemStack(user.getItemInHand(hand).getItem(), user.getItemInHand(hand).getCount() - 1, user.getItemInHand(hand).getTag())));
                        }
                    }
                }
            }
        }

        public static void Water(Direction dir, BlockPos pos, Level level, LivingEntity user, InteractionHand hand){
            BlockPos newpos;
            newpos = switch (dir){
                case UP -> pos.above();
                case DOWN -> pos.below();
                case EAST -> pos.east();
                case WEST -> pos.west();
                case NORTH -> pos.north();
                case SOUTH -> pos.south();
            };
            if(!level.isClientSide()&&level.getBlockState(newpos).getMaterial().isReplaceable()){
                level.setBlock(newpos, Blocks.WATER.defaultBlockState(), 2);
                if(user != null) {
                    if (!user.getItemInHand(hand).getTag().getBoolean("Creative")) {
                        user.setItemInHand(hand, Utilities.DecodeStackTags(new ItemStack(user.getItemInHand(hand).getItem(), user.getItemInHand(hand).getCount() - 1, user.getItemInHand(hand).getTag())));
                    }
                }
            }
        }

        public static void Block(Direction dir, BlockPos pos, Level level, LivingEntity user, Block block, InteractionHand hand){
            BlockPos newpos;
            newpos = switch (dir){
                case UP -> pos.above();
                case DOWN -> pos.below();
                case EAST -> pos.east();
                case WEST -> pos.west();
                case NORTH -> pos.north();
                case SOUTH -> pos.south();
            };
            if(!level.isClientSide()&&level.getBlockState(newpos).getMaterial().isReplaceable()){
                level.setBlock(newpos, block.defaultBlockState(), 3);
                if(user != null) {
                    if (!user.getItemInHand(hand).getTag().getBoolean("Creative")) {
                        user.setItemInHand(hand, Utilities.DecodeStackTags(new ItemStack(user.getItemInHand(hand).getItem(), user.getItemInHand(hand).getCount() - 1, user.getItemInHand(hand).getTag())));
                    }
                }
            }
        }
    }
    @Override
    public Component getName(ItemStack pStack) {
        return pStack.hasTag() ? new TranslatableComponent(getDescriptionId(), "\u00A7a(" + pStack.getTag().getString("Element") + ")") : new TranslatableComponent("item.alchemicalbrewery.element_use");
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
        if(!pContext.getPlayer().isCrouching()) {
            try {
                if (pContext.getItemInHand().getTag().getString("Element").matches("Lava")) {
                    elementfunctions.Lava(pContext.getClickedFace(), pContext.getClickedPos(), pContext.getLevel(), pContext.getPlayer(), pContext.getHand());
                } else if (pContext.getItemInHand().getTag().getString("Element").matches("Water")) {
                    elementfunctions.Water(pContext.getClickedFace(), pContext.getClickedPos(), pContext.getLevel(), pContext.getPlayer(), pContext.getHand());
                } else if (ForgeRegistries.BLOCKS.getValue(new ResourceLocation(pContext.getItemInHand().getOrCreateTag().getString("Element"))) != null) {
                    elementfunctions.Block(pContext.getClickedFace(), pContext.getClickedPos(), pContext.getLevel(), pContext.getPlayer(), ForgeRegistries.BLOCKS.getValue(new ResourceLocation((pContext.getItemInHand().getOrCreateTag().getString("Element")))), pContext.getHand());
                }
            } catch (NullPointerException | ResourceLocationException exception) {
                if (pContext.getItemInHand().hasTag()) {
                    LogUtils.getLogger().debug(pContext.getItemInHand().getTag().getString("Element") + " is not a valid element type");
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

    public static class ColorHandler implements ItemColor {
        @Override
        public int getColor(ItemStack pStack, int pTintIndex) {
            return pStack.hasTag() ? pStack.getTag().getInt("ItemColor") : -1;
        }
    }

}
