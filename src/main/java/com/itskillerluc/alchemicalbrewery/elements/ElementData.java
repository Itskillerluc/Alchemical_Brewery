package com.itskillerluc.alchemicalbrewery.elements;

import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import com.itskillerluc.alchemicalbrewery.entity.custom.ElementProjectileEntity;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Objects;

public class ElementData {
    public String displayName;
    public ItemStack itemModel;
    public int color;
    public int secColor;
    public CompoundTag additionalData;
    public Element elementType;
    public static EntityDataSerializer<ElementData> ELEMENT_DATA = new EntityDataSerializer<>() {
        @Override
        public void write(@NotNull FriendlyByteBuf pBuffer, @NotNull ElementData pValue) {
            pBuffer.writeUtf(pValue.displayName);
            pBuffer.writeItem(pValue.itemModel);
            pBuffer.writeInt(pValue.color);
            pBuffer.writeInt(pValue.secColor);
            pBuffer.writeNbt(pValue.additionalData);
            pBuffer.writeResourceLocation(Objects.requireNonNull(pValue.elementType.getRegistryName()));
        }

        @Override
        public @NotNull ElementData read(@NotNull FriendlyByteBuf pBuffer) {
            return new ElementData(pBuffer.readUtf(), pBuffer.readItem(), pBuffer.readInt(), pBuffer.readInt(), pBuffer.readNbt(), ModElements.ELEMENTS.get().getValue(pBuffer.readResourceLocation()));
        }

        @Override
        public @NotNull ElementData copy(@NotNull ElementData pValue) {
            return pValue;
        }
    };

    public boolean isEmpty(){
        return elementType.getRegistryName() == null || elementType.getRegistryName().equals(new ResourceLocation(AlchemicalBrewery.MOD_ID, "empty"));
    }

    public CompoundTag toTag() {
        return elementType.toTag(this);
    }

    public ElementData decodeFromTag(CompoundTag tag) {
        return elementType.fromTagSafe(tag);
    }

    public boolean matches(ElementData elementData){
        return elementType.matches(this, elementData);
    }
    public ElementData(String displayName, ItemStack itemModel, int color, int secColor, CompoundTag additionalInfo, Element elementType) {
        this.additionalData = additionalInfo != null ? additionalInfo : new CompoundTag();
        this.itemModel = itemModel != null ? itemModel : ItemStack.EMPTY;
        this.displayName = displayName != null ? displayName : "Empty";
        this.color = color;
        this.secColor = secColor;
        this.elementType = elementType != null ? elementType : ModElements.EMPTY.get();
    }

    public ElementData(String displayName, ItemStack itemModel, Integer color, Integer secColor, CompoundTag additionalInfo, Element elementType) {
        this.additionalData = additionalInfo != null ? additionalInfo : elementType.defaultAdditionalData;
        this.itemModel = itemModel != null ? itemModel : elementType.defaultItemModel;
        this.displayName = displayName != null ? displayName : elementType.getName().apply(additionalData);
        this.color = color != null ? color : elementType.getColor().applyAsInt(additionalData);
        this.secColor = secColor != null ? secColor : new Color(elementType.getColor().applyAsInt(additionalData)).darker().getRGB();
        this.elementType = elementType != null ? elementType : ModElements.EMPTY.get();
    }

    public ElementData(Element elementType) {
        this.elementType = elementType != null ? elementType : ModElements.EMPTY.get();
        this.additionalData = elementType != null && elementType.defaultAdditionalData != null ? elementType.defaultAdditionalData : new CompoundTag();
        this.itemModel = elementType != null && elementType.defaultItemModel != null ? elementType.defaultItemModel : ItemStack.EMPTY;
        this.displayName = elementType != null && elementType.defaultDisplayName != null ? elementType.defaultDisplayName : "Empty";
        this.color = elementType != null ? elementType.defaultColor : 0;
        this.secColor = elementType != null ? elementType.defaultSecColor : 0;
    }

    public static ElementData of(CompoundTag tag){
        Element type = tag.contains("type") ? ModElements.ELEMENTS.get().getValue(ResourceLocation.tryParse(tag.getString("type"))) : ModElements.EMPTY.get();
        return type != null ? type.fromTagSafe(tag) : ModElements.EMPTY.get().fromTagSafe(tag);
    }


    /**
     * Call this when you want to call the Function for the element.
     * @param context The UseOnContext of the Item
     * @param Consume if the item should be consumed
     * @throws NullPointerException The context is null
     */
    public final void run (UseOnContext context, boolean Consume){
        if(context == null){
            throw new NullPointerException("Context is not allowed to be null.");
        }
        this.elementType.elementFunction(context.getClickedFace(), context.getClickedPos(), context.getLevel(), context.getPlayer(), context.getHand(), Consume, this.additionalData);
    }

    /**
     * Call this when you want to call the Function for the element.
     * @param result The BlockHitResult of the ElementProjectileEntity
     * @param entity The ElementProjectileEntity
     * @throws NullPointerException The result or entity is null
     */
    public final void run (BlockHitResult result, ElementProjectileEntity entity){
        if(result == null || entity == null){
            throw new NullPointerException("result is not allowed to be null.");
        }
        this.elementType.elementFunction(result.getDirection(), result.getBlockPos(), entity.getLevel(), (LivingEntity)entity.getOwner(), InteractionHand.MAIN_HAND, false, this.additionalData);
    }
    /**
     * Call this when you want to call the Function for the element.
     * @param result The EntityHitResult of the ElementProjectileEntity
     * @throws NullPointerException when result is null
     */
    public final void run (EntityHitResult result, ElementProjectileEntity projectile){
        if(result == null){
            throw new NullPointerException("result is not allowed to be null.");
        }
        Entity entity = result.getEntity();
        this.elementType.elementFunction(Direction.UP, entity.blockPosition(), entity.getLevel(), (LivingEntity)projectile.getOwner(), InteractionHand.MAIN_HAND, false, this.additionalData);
    }

    static {
        EntityDataSerializers.registerSerializer(ELEMENT_DATA);
    }
}

