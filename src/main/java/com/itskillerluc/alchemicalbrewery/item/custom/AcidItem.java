package com.itskillerluc.alchemicalbrewery.item.custom;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import net.minecraft.world.item.*;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

import net.minecraftforge.event.entity.item.ItemTossEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import java.util.function.Supplier;

public class AcidItem extends Item {

    public AcidItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return true;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return 32;
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(new TranslatableComponent("tooltip.alchemicalbrewery.acid"));
    }
    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity entity) {
        if (!(entity instanceof Player player) || pLevel.isClientSide) {
            return pStack;
        }

        if (!player.isCreative()) {
            player.kill();
            pStack.shrink(1);
            ItemStack emptyBottle = new ItemStack(Items.GLASS_BOTTLE);
            if (pStack.getCount() <= 0) {
                return emptyBottle;
            } else {
                player.getInventory().add(emptyBottle);
            }
        }
        return pStack;
    }
}