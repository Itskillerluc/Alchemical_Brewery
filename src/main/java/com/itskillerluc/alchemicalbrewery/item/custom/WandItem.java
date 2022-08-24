package com.itskillerluc.alchemicalbrewery.item.custom;
//TODO
import com.itskillerluc.alchemicalbrewery.elements.ElementData;
import com.itskillerluc.alchemicalbrewery.entity.custom.ElementProjectileEntity;
import com.itskillerluc.alchemicalbrewery.item.ModItems;
import com.itskillerluc.alchemicalbrewery.util.Utilities;
import net.minecraft.Util;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class WandItem extends Item {

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
        if(stack.getTag() != null) {
            stack.getTag().getList("Elements", 10).forEach((ele) -> amounts.add(((CompoundTag) ele).getInt("amount")));
        }
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

    public WandItem(Properties pProperties) {
        super(pProperties);
    }


    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        if (pLevel.isClientSide()) {
            return super.use(pLevel, pPlayer, pUsedHand);
        }
        //adds the selected tag if not present
        final ItemStack itemInHand = pPlayer.getItemInHand(pUsedHand);
        if(itemInHand.getTag() == null){
            itemInHand.getOrCreateTag();
        }
        if (itemInHand.getTag() != null && !itemInHand.getTag().contains("Selected")) {
            itemInHand.getOrCreateTag().putInt("Selected", 0);
        }
        if (!itemInHand.getTag().contains("Elements")){
            ListTag tag = new ListTag();
            itemInHand.getOrCreateTag().put("Elements", tag);
        }

        upgradeHandler(itemInHand);

        if (pPlayer.isCrouching()) {
            //add the element in your hand to the wand item
            if (!pPlayer.getMainHandItem().isEmpty() && !pPlayer.getOffhandItem().isEmpty()) {
                insertElement(pPlayer);
            } else {
                //select a different element
                cycleElements(pPlayer, itemInHand);
            }

        } else if (getUsedelements(itemInHand) > 0) {
            useElement(pLevel, pPlayer, itemInHand);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    private void useElement(Level pLevel, Player pPlayer, ItemStack itemInHand) {
        if (itemInHand.getTag() == null){
            return;
        }
        ElementData element = ElementData.of(itemInHand.getTag().getList("Elements", 10).getCompound(getSlot(itemInHand)).getCompound("element"));
        //summon the projectile with the element
        int slot = 0;
        pLevel.addFreshEntity(new ElementProjectileEntity(pLevel, pPlayer, pPlayer.getX(), pPlayer.getEyeY(), pPlayer.getZ(), pPlayer.getLookAngle().x * 0.1, pPlayer.getLookAngle().y * 0.1, pPlayer.getLookAngle().z * 0.1, element));
         if (itemInHand.getTag().getList("Elements", 10).getCompound(getSlot(itemInHand)).getInt("amount") == 1) {
            itemInHand.getTag().getList("Elements", 10).remove(getSlot(itemInHand));
            if (getSlot(itemInHand) >= itemInHand.getTag().getList("Elements", 10).size()) {
                slot = itemInHand.getTag().getList("Elements", 10).size() - 1;
            }
        } else {
            itemInHand.getTag().getList("Elements", 10).getCompound(getSlot(itemInHand)).putInt("amount", itemInHand.getTag().getList("Elements", 10).getCompound(getSlot(itemInHand)).getInt("amount")-1);
            slot = getSlot(itemInHand);
        }
        setUsedelements(itemInHand, getUsedelements(itemInHand)-1);
        setSlot(itemInHand, slot);
        if(getSlot(itemInHand) == -1){
            setSlot(itemInHand, 0);
        }
    }

    private void cycleElements(Player pPlayer, ItemStack itemInHand) {
        if(itemInHand.getTag() == null){
            return;
        }
        if (getSlot(itemInHand) + 1 < itemInHand.getTag().getList("Elements", 10).size()) {

            setSlot(itemInHand, getSlot(itemInHand) + 1);

            ElementData element = ElementData.of(itemInHand.getTag().getList("Elements", 10).getCompound(getSlot(itemInHand)).getCompound("element"));

            pPlayer.displayClientMessage(new TextComponent("Selected " + element.displayName), true);

        } else {
            setSlot(itemInHand, 0);
            if (getSlot(itemInHand) < itemInHand.getTag().getList("Elements", 10).size()) {
                ElementData element = ElementData.of(itemInHand.getTag().getList("Elements", 10).getCompound(getSlot(itemInHand)).getCompound("element"));
                pPlayer.displayClientMessage(new TextComponent("Selected " + element.displayName), true);
            }
        }
    }

    private void insertElement(Player pPlayer) {
        if (pPlayer.getOffhandItem().is(ModItems.ELEMENT_USE.get()) || pPlayer.getMainHandItem().is(ModItems.ELEMENT_USE.get())) {

            ElementData elementinhand = null;

            final ItemStack itemInHand = pPlayer.getItemInHand(pPlayer.getOffhandItem().is(ModItems.ELEMENT_USE.get()) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
            final ItemStack wand = pPlayer.getItemInHand(pPlayer.getOffhandItem().is(ModItems.WAND_ITEM.get()) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);

            if (itemInHand.getTag() != null) {
                elementinhand = ElementData.of(itemInHand.getOrCreateTag().getCompound("element"));
                if (getUsedelements(wand) < getMaxelements(wand)) {
                    pPlayer.setItemInHand(pPlayer.getOffhandItem().is(ModItems.ELEMENT_USE.get()) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND, Utilities.DecodeStackTags(new ItemStack(itemInHand.getItem(), itemInHand.getCount() - 1, itemInHand.getTag())));
                }
            }

            if (elementinhand == null || elementinhand.isEmpty() || wand.getTag() == null) {
                return;
            }

            if (getUsedelements(wand) < getMaxelements(wand)) {

                boolean inserted = false;

                //if it already exists add one
                for (int i = 0; i < wand.getTag().getList("Elements", 10).size(); i++) {
                    if (ElementData.of(wand.getTag().getList("Elements", 10).getCompound(i).getCompound("element")).matches(elementinhand)) {
                        wand.getTag().getList("Elements", 10).getCompound(i).putInt("amount", wand.getTag().getList("Elements", 10).getCompound(i).getInt("amount") + 1);
                        inserted = true;
                        setUsedelements(wand, getUsedelements(wand) + 1);
                    }
                }

                if (!inserted) {
                    ElementData finalElementinhand = elementinhand;
                    wand.getTag().getList("Elements", 10).add(Util.make(
                            () -> {
                                var tag = new CompoundTag();
                                tag.put("element", finalElementinhand.toTag());
                                return tag;
                            }
                    ));
                    wand.getTag().getList("Elements", 10).getCompound(wand.getTag().getList("Elements", 10).size() - 1).putInt("amount", 1);
                    setUsedelements(wand, getUsedelements(wand)+1);
                    cycleElements(pPlayer, wand);
                }
            }
        }
    }

    public static int getColorForSelected(ItemStack stack) {
        return stack.getTag() != null ? ElementData.of(stack.getTag().getList("Elements", 10).getCompound(stack.getTag().getInt("Selected")).getCompound("element")).color : -1;
    }

    /**
     * @param stack the itemstack that is targeted
     * @return returns true if the wand is containing atleast 1 element
     */
    public static boolean hasElement(ItemStack stack) {
        return stack.getTag() != null && stack.getTag().getList("Elements", 10).size() > 0;
    }

    /**
     * Sets the color to the nbt that is provided
     */
    public static class ColorHandler implements ItemColor {
        @Override
        public int getColor(@NotNull ItemStack pStack, int pTintIndex) {
            return getColorForSelected(pStack);
        }
    }


}
