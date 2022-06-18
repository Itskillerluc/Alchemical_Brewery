package com.itskillerluc.alchemicalbrewery.elements;

import com.itskillerluc.alchemicalbrewery.entity.custom.ElementProjectileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class Element extends ForgeRegistryEntry<Element> {
    public final String DISPLAYNAME;
    public Item itemModel = Items.AIR;
    public int color = 0;

    public static final Element EMPTY = new Element("Empty") {
        @Override
        public CompoundTag getDataCompound() {
            return null;
        }
        @Override
        public void setDataCompound(CompoundTag dataCompound) {
        }

        @Override
        void elementFunction(Direction dir, BlockPos pos, Level level, LivingEntity user, InteractionHand hand, boolean consume, CompoundTag ElementTags) {

        }
    };

    abstract public CompoundTag getDataCompound();
    abstract public void setDataCompound(CompoundTag dataCompound);

    public Element(String Displayname){
        DISPLAYNAME = (Displayname != null) ? Displayname : "Empty";
    }

    /**
     * Call this when you want to call the Function for the element.
     * @param context The UseOnContext of the Item
     * @param Consume if the item should be consumed
     */
    public final void run (UseOnContext context, boolean Consume){
        if(context == null){
            throw new NullPointerException("Context is not allowed to be null.");
        }
        elementFunction(context.getClickedFace(), context.getClickedPos(), context.getLevel(), context.getPlayer(), context.getHand(), Consume, context.getItemInHand().getTag().getCompound("Element"));
    }

    /**
     * Call this when you want to call the Function for the element.
     * @param result The BlockHitResult of the ElementProjectileEntity
     * @param entity The ElementProjectileEntity
     */
    public final void run (BlockHitResult result, ElementProjectileEntity entity){
        if(result == null){
            throw new NullPointerException("entity is not allowed to be null.");
        }
        elementFunction(result.getDirection(), result.getBlockPos(), entity.getLevel(), (LivingEntity)entity.getOwner(), InteractionHand.MAIN_HAND, false, entity.getPersistentData().getCompound("Element"));
    }
    /**
     * Call this when you want to call the Function for the element.
     * @param result The EntityHitResult of the ElementProjectileEntity
     * @throws NullPointerException when result is null
     */
    public final void run (EntityHitResult result){
        if(result == null){
            throw new NullPointerException("result is not allowed to be null.");
        }
        ElementProjectileEntity entity = ((ElementProjectileEntity) result.getEntity());
        elementFunction(Direction.UP, entity.blockPosition(), entity.getLevel(), (LivingEntity)entity.getOwner(), InteractionHand.MAIN_HAND, false, result.getEntity().getPersistentData().getCompound("Element"));
    }
    /**
     * Override this to define what the element does.
     */
    abstract void elementFunction(Direction dir, BlockPos pos, Level level, LivingEntity user, InteractionHand hand, boolean consume, CompoundTag ElementTags);
}
