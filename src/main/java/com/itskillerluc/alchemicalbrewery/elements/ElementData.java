package com.itskillerluc.alchemicalbrewery.elements;

import com.itskillerluc.alchemicalbrewery.entity.custom.ElementProjectileEntity;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ElementData {
    public String displayName;
    public ItemStack itemModel;
    public int color;
    public CompoundTag additionalData;
    public static EntityDataSerializer<ElementData> ELEMENTDATA = new EntityDataSerializer<>() {
        @Override
        public void write(@NotNull FriendlyByteBuf pBuffer, @NotNull ElementData pValue) {
            pBuffer.writeUtf(pValue.displayName);
            pBuffer.writeItem(pValue.itemModel);
            pBuffer.writeInt(pValue.color);
            pBuffer.writeNbt(pValue.additionalData);
            pBuffer.writeResourceLocation(Objects.requireNonNull(pValue.elementType.getRegistryName()));
        }

        @Override
        public @NotNull ElementData read(@NotNull FriendlyByteBuf pBuffer) {
            return new ElementData(pBuffer.readUtf(), pBuffer.readItem(), pBuffer.readInt(), pBuffer.readNbt(), ModElements.ELEMENTS.get().getValue(pBuffer.readResourceLocation()));
        }

        @Override
        public @NotNull ElementData copy(@NotNull ElementData pValue) {
            return pValue;
        }
    };

    public Element elementType;

    public CompoundTag toTag() {
        return elementType.toTag(this);
    }

    public ElementData fromTag(CompoundTag tag) {
        return elementType.fromTag(tag, this);
    }

    public ElementData(String displayname, ItemStack itemModel, int color, CompoundTag additionalInfo, Element elementType) {
        this.additionalData = additionalInfo != null ? additionalInfo : new CompoundTag();
        this.itemModel = itemModel != null ? itemModel : ItemStack.EMPTY;
        this.displayName = displayname != null ? displayname : "Empty";
        this.color = elementType.defaultColor;
        this.elementType = elementType != null ? elementType : ModElements.EMPTY.get();;
    }

    public ElementData(Element elementType) {
        this.additionalData = elementType.defaultAdditionalData != null ? elementType.defaultAdditionalData : new CompoundTag();
        this.itemModel = elementType.defualtItemModel != null ? elementType.defualtItemModel : ItemStack.EMPTY;
        this.displayName = elementType.defaultDisplayName != null ? elementType.defaultDisplayName : "Empty";
        this.color = elementType.defaultColor;
        this.elementType = elementType != null ? elementType : ModElements.EMPTY.get();
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
        if(result == null){
            throw new NullPointerException("result is not allowed to be null.");
        }else if(entity == null){
            throw new NullPointerException("entity is not allowed to be null");
        }
        this.elementType.elementFunction(result.getDirection(), result.getBlockPos(), entity.getLevel(), (LivingEntity)entity.getOwner(), InteractionHand.MAIN_HAND, false, this.additionalData);
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
        this.elementType.elementFunction(Direction.UP, entity.blockPosition(), entity.getLevel(), (LivingEntity)entity.getOwner(), InteractionHand.MAIN_HAND, false, this.additionalData);
    }

    static{
        EntityDataSerializers.registerSerializer(ELEMENTDATA);
    }
}

