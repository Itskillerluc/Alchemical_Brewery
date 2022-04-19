package com.itskillerluc.alchemicalbrewery.item.custom;

import com.itskillerluc.alchemicalbrewery.entity.custom.ElementProjectileEntity;
import com.itskillerluc.alchemicalbrewery.item.ModItems;
import com.itskillerluc.alchemicalbrewery.util.Utilities;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class WandItem extends Item {
    protected ArrayList<String> elements = new ArrayList<>();
    protected ArrayList<Integer> counts = new ArrayList<>();
    protected ArrayList<Integer> colors = new ArrayList<>();
    String elementinhand = "None";
    private int count;
    private int color;

    public void resetvar(){
        elements = new ArrayList<>();
        counts = new ArrayList<>();
        colors = new ArrayList<>();
        count = 0;
    }
    public void setvar(ItemStack stack){
        if(stack.hasTag()) {
            for (int i = 0; i < stack.getTag().getList("Elements", 10).size(); i++) {
                elements.add(stack.getTag().getList("Elements", 10).getCompound(i).getString("element"));
                counts.add(stack.getTag().getList("Elements", 10).getCompound(i).getInt("amount"));
                colors.add(stack.getTag().getList("Elements", 10).getCompound(i).getInt("color"));
            }
        }
    }

    public void setSlot(ItemStack stack, int slot){
        stack.getOrCreateTag().putInt("Selected", slot);
    }
    public int getSlot(ItemStack stack){
        return stack.getTag().getInt("Selected");
    }
    public int getMaxelements(ItemStack stack) {
        return stack.getTag().getInt("maxelements");
    }
    public int getUsedelements(ItemStack stack) {
        return stack.getTag().getInt("usedelements");
    }
    public int getAmount(ItemStack stack){
        ArrayList<Integer> amounts = new ArrayList<>();
        int amount = 0;
        stack.getTag().getList("Elements", 10).stream().forEach((ele)->{
            amounts.add(((CompoundTag) ele).getInt("amount"));
        });
        for (Integer i : amounts)
            amount += i;
        return amount;
    }
    public void setMaxelements(ItemStack stack, int value){
        stack.getOrCreateTag().putInt("maxelements", value);
    }
    public void setUsedelements(ItemStack stack, int value){
        stack.getOrCreateTag().putInt("usedelements", value);
    }
    private void upgradeHandler(ItemStack stack){
        setMaxelements(stack, 16);
    }


    public CompoundTag updateNBT(ItemStack stack)
    {
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < elements.size(); i++)
        {
            if (!elements.get(i).isEmpty())
            {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("amount", counts.get(i));
                itemTag.putString("element", elements.get(i));
                itemTag.putInt("color", colors.get(i));
                nbtTagList.add(itemTag);
            }
        }
        CompoundTag nbt = new CompoundTag();
        nbt.put("Elements", nbtTagList);
        nbt.putInt("Size", elements.size());
        stack.setTag(nbt);
        resetvar();
        upgradeHandler(stack);
        setUsedelements(stack, getAmount(stack));
        return nbt;
    }
    public CompoundTag serializeNBT(ItemStack stack)
    {
        setvar(stack);
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < elements.size(); i++)
        {
            if (!elements.get(i).isEmpty())
            {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("amount", counts.get(i));
                itemTag.putString("element", elements.get(i));
                itemTag.putInt("color", colors.get(i));
                nbtTagList.add(itemTag);
            }
        }

        CompoundTag nbt = new CompoundTag();
        nbt.put("Elements", nbtTagList);
        nbt.putInt("Size", elements.size());
        stack.setTag(nbt);
        resetvar();
        upgradeHandler(stack);
        setUsedelements(stack, getAmount(stack));
        return nbt;
    }

    public WandItem(Properties pProperties) {
        super(pProperties);
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if(!pLevel.isClientSide()){
            if(pPlayer.getItemInHand(pUsedHand).hasTag()) {
                if (!pPlayer.getItemInHand(pUsedHand).getTag().contains("Selected")) {
                    pPlayer.getItemInHand(pUsedHand).getOrCreateTag().putInt("Selected", 0);
                }
            }
            color = 0;
            upgradeHandler(pPlayer.getItemInHand(pUsedHand));
            if(pPlayer.isCrouching()){
                if(!pPlayer.getMainHandItem().isEmpty() && !pPlayer.getOffhandItem().isEmpty()) {
                    if(pPlayer.getOffhandItem().is(ModItems.ELEMENT_USE.get())||pPlayer.getMainHandItem().is(ModItems.ELEMENT_USE.get())) {
                        if (pUsedHand.equals(InteractionHand.OFF_HAND)) {
                            if(pPlayer.getItemInHand(InteractionHand.MAIN_HAND).hasTag()) {
                                elementinhand = pPlayer.getItemInHand(InteractionHand.MAIN_HAND).getTag().getString("Element");
                                count = pPlayer.getItemInHand(InteractionHand.MAIN_HAND).getCount();
                                color = pPlayer.getItemInHand(InteractionHand.MAIN_HAND).hasTag() ? pPlayer.getItemInHand(InteractionHand.MAIN_HAND).getTag().getInt("ItemColor") : -1;
                                if(getUsedelements(pPlayer.getItemInHand(pUsedHand)) < getMaxelements(pPlayer.getItemInHand(pUsedHand))) {
                                    pPlayer.setItemInHand(InteractionHand.MAIN_HAND, Utilities.DecodeStackTags(new ItemStack(pPlayer.getItemInHand(InteractionHand.MAIN_HAND).getItem(), pPlayer.getItemInHand(InteractionHand.MAIN_HAND).getCount() - 1, pPlayer.getItemInHand(InteractionHand.MAIN_HAND).getTag())));
                                }
                            }
                        } else {
                            if(pPlayer.getItemInHand(InteractionHand.OFF_HAND).hasTag()) {
                                elementinhand = pPlayer.getItemInHand(InteractionHand.OFF_HAND).getTag().getString("Element");
                                count = pPlayer.getItemInHand(InteractionHand.OFF_HAND).getCount();
                                color = pPlayer.getItemInHand(InteractionHand.OFF_HAND).hasTag() ? pPlayer.getItemInHand(InteractionHand.OFF_HAND).getTag().getInt("ItemColor") : -1;
                                if(getUsedelements(pPlayer.getItemInHand(pUsedHand)) < getMaxelements(pPlayer.getItemInHand(pUsedHand))) {
                                    pPlayer.setItemInHand(InteractionHand.OFF_HAND, Utilities.DecodeStackTags(new ItemStack(pPlayer.getItemInHand(InteractionHand.OFF_HAND).getItem(), pPlayer.getItemInHand(InteractionHand.OFF_HAND).getCount() - 1, pPlayer.getItemInHand(InteractionHand.OFF_HAND).getTag())));
                                }
                            }
                        }
                        if (!elementinhand.matches("None")){
                            if(getUsedelements(pPlayer.getItemInHand(pUsedHand)) < getMaxelements(pPlayer.getItemInHand(pUsedHand))) {
                                boolean inserted = false;
                                for (int i = 0; i < pPlayer.getItemInHand(pUsedHand).getTag().getList("Elements", 10).size(); i++) {
                                    if(pPlayer.getItemInHand(pUsedHand).getOrCreateTag().getList("Elements", 10).getCompound(i).getString("element").matches(elementinhand)){
                                        pPlayer.getItemInHand(pUsedHand).getOrCreateTag().getList("Elements", 10).getCompound(i).putInt("amount", pPlayer.getItemInHand(pUsedHand).getOrCreateTag().getList("Elements", 10).getCompound(i).getInt("amount")+1);
                                        inserted = true;
                                        setUsedelements(pPlayer.getItemInHand(pUsedHand), getUsedelements(pPlayer.getItemInHand(pUsedHand))+1);
                                    }
                                }if(!inserted){
                                    elements.add(elementinhand);
                                    counts.add(1);
                                    colors.add(color);
                                    color = 0;
                                    this.serializeNBT(pPlayer.getItemInHand(pUsedHand));
                                }
                                upgradeHandler(pPlayer.getItemInHand(pUsedHand));
                                this.resetvar();
                            }
                        }
                    }
                }else {
                    setvar(pPlayer.getItemInHand(pUsedHand));
                    if(getSlot(pPlayer.getItemInHand(pUsedHand))+1 <elements.size()){
                        setSlot(pPlayer.getItemInHand(pUsedHand), getSlot(pPlayer.getItemInHand(pUsedHand))+1);
                        if(getSlot(pPlayer.getItemInHand(pUsedHand))<elements.size()) {
                            pPlayer.displayClientMessage(new TextComponent("Selected " + elements.get(getSlot(pPlayer.getItemInHand(pUsedHand)))), true);
                        }
                    }else{
                        setSlot(pPlayer.getItemInHand(pUsedHand), 0);
                        if(getSlot(pPlayer.getItemInHand(pUsedHand))<elements.size()) {
                            pPlayer.displayClientMessage(new TextComponent("Selected " + elements.get(getSlot(pPlayer.getItemInHand(pUsedHand)))), true);
                        }
                    }
                    this.resetvar();
                }
            }else if(getUsedelements(pPlayer.getItemInHand(pUsedHand)) > 0){
                int slot = 0;
                setvar(pPlayer.getItemInHand(pUsedHand));
                pLevel.addFreshEntity(new ElementProjectileEntity(pLevel, pPlayer, pPlayer.getX(), pPlayer.getEyeY(), pPlayer.getZ(), pPlayer.getLookAngle().x * 1, pPlayer.getLookAngle().y * 1, pPlayer.getLookAngle().z * 1, colors.get(getSlot(pPlayer.getItemInHand(pUsedHand))), elements.get(getSlot(pPlayer.getItemInHand(pUsedHand)))));
                if(counts.get(getSlot(pPlayer.getItemInHand(pUsedHand)))<=1){
                    elements.remove(getSlot(pPlayer.getItemInHand(pUsedHand)));
                    colors.remove(getSlot(pPlayer.getItemInHand(pUsedHand)));
                    counts.remove(getSlot(pPlayer.getItemInHand(pUsedHand)));
                    if(getSlot(pPlayer.getItemInHand(pUsedHand)) >= elements.size()){
                        slot = elements.size()-1;
                    }
                }else{
                    counts.set(getSlot(pPlayer.getItemInHand(pUsedHand)), counts.get(getSlot(pPlayer.getItemInHand(pUsedHand)))-1);
                    slot = getSlot(pPlayer.getItemInHand(pUsedHand));
                }
                setUsedelements(pPlayer.getItemInHand(pUsedHand), getUsedelements(pPlayer.getItemInHand(pUsedHand))-1);
                updateNBT(pPlayer.getItemInHand(pUsedHand));
                resetvar();
                setSlot(pPlayer.getItemInHand(pUsedHand), slot);
            }
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
