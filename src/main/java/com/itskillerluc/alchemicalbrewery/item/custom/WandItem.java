package com.itskillerluc.alchemicalbrewery.item.custom;

import com.itskillerluc.alchemicalbrewery.entity.custom.ElementProjectileEntity;
import com.itskillerluc.alchemicalbrewery.item.ModItems;
import com.itskillerluc.alchemicalbrewery.util.Utilities;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;


import java.util.ArrayList;

public class WandItem extends Item {
    protected ArrayList<String> elements = new ArrayList<>();
    protected ArrayList<Integer> counts = new ArrayList<>();
    protected ArrayList<Integer> colors = new ArrayList<>();
    protected ArrayList<Integer> seccolors = new ArrayList<>();
    String elementinhand = "None";
    private int count;
    private int color;
    private int seccolor;

    /**
     * resets all the variables to their default values use this after setvar(Itemstack stack)
     */
    public void resetvar(){
        elements = new ArrayList<>();
        counts = new ArrayList<>();
        colors = new ArrayList<>();
        seccolors = new ArrayList<>();
        count = 0;
    }

    /**
     * Always run resetvar() after having used this
     * @param stack the stack which the nbt should be read from
     */
    public void setvar(ItemStack stack){
        if(stack.hasTag()) {
            for (int i = 0; i < stack.getTag().getList("Elements", 10).size(); i++) {
                elements.add(stack.getTag().getList("Elements", 10).getCompound(i).getString("element"));
                counts.add(stack.getTag().getList("Elements", 10).getCompound(i).getInt("amount"));
                colors.add(stack.getTag().getList("Elements", 10).getCompound(i).getInt("color"));
                seccolors.add(stack.getTag().getList("Elements", 10).getCompound(i).getInt("seccolor"));
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

    /**
     * @param stack stack that is being targeted
     * @return the total amount of element slots that are being used
     */
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

    /**
     * handles the upgrades
     * @param stack itemstack which is targeted
     */
    private void upgradeHandler(ItemStack stack){
        setMaxelements(stack, 16);
    }


    /**
     * use this if you need to update the changes in the variables to the nbt of the itemstack
     * @param stack itemstack that is being targeted
     * @return compoundtag that is put into the itemstack
     */
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
                itemTag.putInt("seccolor", seccolors.get(i));
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

    /**
     * use this the first time when pushing the variables to the items nbt
     * @param stack itemstack that is being targeted
     * @return nbt that is put into the itemstack
     */
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
                itemTag.putInt("seccolor", seccolors.get(i));
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
                //adds the selected tag if not present
                if (!pPlayer.getItemInHand(pUsedHand).getTag().contains("Selected")) {
                    pPlayer.getItemInHand(pUsedHand).getOrCreateTag().putInt("Selected", 0);
                }
            }
            color = 0;
            seccolor = 0;
            upgradeHandler(pPlayer.getItemInHand(pUsedHand));
            if(pPlayer.isCrouching()){
                //add the element in your hand to the wand item
                if(!pPlayer.getMainHandItem().isEmpty() && !pPlayer.getOffhandItem().isEmpty()) {
                    if(pPlayer.getOffhandItem().is(ModItems.ELEMENT_USE.get())||pPlayer.getMainHandItem().is(ModItems.ELEMENT_USE.get())) {
                        if (pUsedHand.equals(InteractionHand.OFF_HAND)) {
                            if(pPlayer.getItemInHand(InteractionHand.MAIN_HAND).hasTag()) {
                                elementinhand = pPlayer.getItemInHand(InteractionHand.MAIN_HAND).getTag().getString("Element");
                                count = pPlayer.getItemInHand(InteractionHand.MAIN_HAND).getCount();
                                color = pPlayer.getItemInHand(InteractionHand.MAIN_HAND).hasTag() ? pPlayer.getItemInHand(InteractionHand.MAIN_HAND).getTag().getInt("ItemColor") : -1;
                                seccolor = pPlayer.getItemInHand(InteractionHand.MAIN_HAND).hasTag() ? pPlayer.getItemInHand(InteractionHand.MAIN_HAND).getTag().getInt("SecItemColor") : -1;
                                if(getUsedelements(pPlayer.getItemInHand(pUsedHand)) < getMaxelements(pPlayer.getItemInHand(pUsedHand))) {
                                    pPlayer.setItemInHand(InteractionHand.MAIN_HAND, Utilities.DecodeStackTags(new ItemStack(pPlayer.getItemInHand(InteractionHand.MAIN_HAND).getItem(), pPlayer.getItemInHand(InteractionHand.MAIN_HAND).getCount() - 1, pPlayer.getItemInHand(InteractionHand.MAIN_HAND).getTag())));
                                }
                            }
                        } else {
                            if(pPlayer.getItemInHand(InteractionHand.OFF_HAND).hasTag()) {
                                elementinhand = pPlayer.getItemInHand(InteractionHand.OFF_HAND).getTag().getString("Element");
                                count = pPlayer.getItemInHand(InteractionHand.OFF_HAND).getCount();
                                color = pPlayer.getItemInHand(InteractionHand.OFF_HAND).hasTag() ? pPlayer.getItemInHand(InteractionHand.OFF_HAND).getTag().getInt("ItemColor") : -1;
                                seccolor = pPlayer.getItemInHand(InteractionHand.OFF_HAND).hasTag() ? pPlayer.getItemInHand(InteractionHand.OFF_HAND).getTag().getInt("SecItemColor") : -1;
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
                                    seccolors.add(seccolor);
                                    color = 0;
                                    seccolor = 0;
                                    this.serializeNBT(pPlayer.getItemInHand(pUsedHand));
                                }
                                upgradeHandler(pPlayer.getItemInHand(pUsedHand));
                                this.resetvar();
                            }
                        }
                    }
                }else {
                    //select a different element
                    setvar(pPlayer.getItemInHand(pUsedHand));
                    if(getSlot(pPlayer.getItemInHand(pUsedHand))+1 <elements.size()){
                        setSlot(pPlayer.getItemInHand(pUsedHand), getSlot(pPlayer.getItemInHand(pUsedHand))+1);
                        if(getSlot(pPlayer.getItemInHand(pUsedHand))<elements.size()) {
                            String ElementRaw = elements.get(getSlot(pPlayer.getItemInHand(pUsedHand)));
                            String Element = ElementRaw;
                            if (ElementRaw.contains("-")) {
                                if(ElementRaw.substring(ElementRaw.indexOf('-')).length() < 1){
                                    try {
                                        throw new ResourceLocationException("found - sign without Element value behind it. Correct syntax should be: Displayname-RealElement or Realelement. Found in:" + ElementRaw);

                                    }catch (ResourceLocationException exception){
                                        exception.printStackTrace();
                                    }
                                }
                                Element = ElementRaw.substring(0, ElementRaw.indexOf('-')+1);
                            }
                            pPlayer.displayClientMessage(new TextComponent("Selected " + Element), true);
                        }
                    }else{
                        setSlot(pPlayer.getItemInHand(pUsedHand), 0);
                        if(getSlot(pPlayer.getItemInHand(pUsedHand))<elements.size()) {
                            String ElementRaw = elements.get(getSlot(pPlayer.getItemInHand(pUsedHand)));
                            String Element = ElementRaw;
                            if (ElementRaw.contains("-")) {
                                if(ElementRaw.substring(ElementRaw.indexOf('-')).length() < 1){
                                    try {
                                        throw new ResourceLocationException("found - sign without Element value behind it. Correct syntax should be: Displayname-RealElement or Realelement. Found in:" + ElementRaw);

                                    }catch (ResourceLocationException exception){
                                        exception.printStackTrace();
                                    }
                                }
                                Element = ElementRaw.substring(0, ElementRaw.indexOf('-')+1);
                            }
                            pPlayer.displayClientMessage(new TextComponent("Selected " + Element), true);
                        }
                    }
                    this.resetvar();
                }
            }else if(getUsedelements(pPlayer.getItemInHand(pUsedHand)) > 0){
                setvar(pPlayer.getItemInHand(pUsedHand));
                String ElementRaw = elements.get(getSlot(pPlayer.getItemInHand(pUsedHand)));
                String Element = ElementRaw;
                if (ElementRaw.contains("-")) {
                    if(ElementRaw.substring(ElementRaw.indexOf('-')).length() < 1){
                        try {
                            throw new ResourceLocationException("found - sign without Element value behind it. Correct syntax should be: Displayname-RealElement or Realelement. Found in:" + ElementRaw);

                        }catch (ResourceLocationException exception){
                            exception.printStackTrace();
                        }
                    }
                    Element = ElementRaw.substring(ElementRaw.indexOf('-')+1).substring(1);
                }
                //summon the projectile with the element
                int slot = 0;
                pLevel.addFreshEntity(new ElementProjectileEntity(pLevel, pPlayer, pPlayer.getX(), pPlayer.getEyeY(), pPlayer.getZ(), pPlayer.getLookAngle().x * 1, pPlayer.getLookAngle().y * 1, pPlayer.getLookAngle().z * 1, colors.get(getSlot(pPlayer.getItemInHand(pUsedHand))), Element));
                if(counts.get(getSlot(pPlayer.getItemInHand(pUsedHand)))<=1){
                    elements.remove(getSlot(pPlayer.getItemInHand(pUsedHand)));
                    colors.remove(getSlot(pPlayer.getItemInHand(pUsedHand)));
                    seccolors.remove(getSlot(pPlayer.getItemInHand(pUsedHand)));
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

    /**
     * @param stack the itemstack that is targeted
     * @return returns true if the wand is containing atleast 1 element
     */
    public static boolean hasElement(ItemStack stack){
        return stack.hasTag() && stack.getTag().getList("Elements", 10).size() > 0;
    }

    /**
     * Sets the color to the nbt that is provided
     */
    public static class ColorHandler implements ItemColor {
        @Override
        public int getColor(ItemStack pStack, int pTintIndex) {
            return pStack.hasTag() ? pStack.getTag().getList("Elements", 10).getCompound(pStack.getTag().getInt("Selected")).getInt("color") : -1;
        }
    }


}
