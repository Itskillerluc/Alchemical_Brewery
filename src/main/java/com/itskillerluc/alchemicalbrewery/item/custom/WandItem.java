package com.itskillerluc.alchemicalbrewery.item.custom;

import com.itskillerluc.alchemicalbrewery.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class WandItem extends Item {
    protected ArrayList<String> elements = new ArrayList<>();
    protected ArrayList<Integer> counts = new ArrayList<>();
    String elementinhand = "None";
    int maxelements = 1;
    int usedelements = 0;
    private int count;

    public int getMaxelements() {
        return maxelements;
    }

    public CompoundTag serializeNBT(ItemStack stack)
    {
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < elements.size(); i++)
        {
            if (!elements.get(i).isEmpty())
            {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("amount", counts.get(i));
                itemTag.putString("element", elements.get(i));
                nbtTagList.add(itemTag);
            }
        }
        CompoundTag nbt = new CompoundTag();
        nbt.put("Elements", nbtTagList);
        nbt.putInt("Size", elements.size());
        stack.setTag(nbt);
        return nbt;
    }

    public void deserializeNBT(CompoundTag nbt)
    {
        ListTag tagList = nbt.getList("Elements", Tag.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++)
        {
            CompoundTag itemTags = tagList.getCompound(i);
            counts.set(i, itemTags.getInt("amount"));
            elements.set(i, itemTags.getString("element"));
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return super.initCapabilities(stack, this.serializeNBT(stack));
    }

    public void setMaxelements(int maxelements) {
        this.maxelements = maxelements;
    }

    public WandItem(Properties pProperties) {
        super(pProperties);
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if(!pLevel.isClientSide()){
            if(pPlayer.isCrouching()){
                if(pPlayer.getMainHandItem() != null && pPlayer.getOffhandItem() != null) {
                    if(pPlayer.getOffhandItem().is(ModItems.ELEMENT_USE.get())||pPlayer.getMainHandItem().is(ModItems.ELEMENT_USE.get())) {
                        if (pUsedHand.equals(InteractionHand.OFF_HAND)) {
                            if(pPlayer.getItemInHand(InteractionHand.MAIN_HAND).hasTag()) {
                                elementinhand = pPlayer.getItemInHand(InteractionHand.MAIN_HAND).getTag().getString("Element");
                                count = pPlayer.getItemInHand(InteractionHand.MAIN_HAND).getCount();
                            }
                        } else {
                            if(pPlayer.getItemInHand(InteractionHand.OFF_HAND).hasTag()) {
                                elementinhand = pPlayer.getItemInHand(InteractionHand.OFF_HAND).getTag().getString("Element");
                                count = pPlayer.getItemInHand(InteractionHand.OFF_HAND).getCount();
                            }
                        }
                        if (!elementinhand.matches("None")){
                            if(usedelements < maxelements) {
                                boolean inserted = false;
                                for (int i = 0; i < elements.size(); i++) {
                                    if(elements.get(i).matches(elementinhand)){
                                        counts.set(i, counts.get(i)+count);
                                        inserted = true;
                                    }
                                }if(!inserted){
                                    elements.add(elementinhand);
                                    counts.add(1);
                                    inserted = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        this.serializeNBT(pPlayer.getItemInHand(pUsedHand));
        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
