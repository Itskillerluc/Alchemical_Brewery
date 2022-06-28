package com.itskillerluc.alchemicalbrewery.elements;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.Objects;

public abstract class Element extends ForgeRegistryEntry<Element> {

    protected final String defaultDisplayName;
    protected final ItemStack defualtItemModel;
    protected final int defaultColor;
    protected final CompoundTag defaultAdditionalData;


    protected CompoundTag toTag(ElementData data) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("color", data.color);
        tag.putString("displayName", data.displayName);
        tag.put("additionalData",data.additionalData);
        tag.put("itemModel", new CompoundTag());
        data.itemModel.save(tag.getCompound("itemModel"));
        tag.putString("type",this.getRegistryName().toString());
        return tag;
    }

    protected ElementData fromTag(CompoundTag compoundTag, ElementData data) {
        CompoundTag tag = compoundTag.getCompound("element");
        data.color = tag.getInt("color");
        data.displayName = tag.getString("displayName");
        data.additionalData = tag.getCompound("additionalData");
        data.itemModel = ItemStack.of(tag.getCompound("itemModel"));
        data.elementType = ModElements.ELEMENTS.get().getValue(ResourceLocation.tryParse(tag.getString("type")));
        return data;
    }

    public Element(String defaultDisplayName, CompoundTag defaultAdditionalData, ItemStack defaultItemModel, int defaultColor){
        this.defaultAdditionalData = defaultAdditionalData != null ? defaultAdditionalData : new CompoundTag();
        this.defualtItemModel = defaultItemModel != null ? defaultItemModel : ItemStack.EMPTY;
        this.defaultColor = defaultColor;
        this.defaultDisplayName = defaultDisplayName != null ? defaultDisplayName : "Empty";
    }


    /**
     * Override this to define what the element does.
     */
    abstract void elementFunction(Direction dir, BlockPos pos, Level level, LivingEntity user, InteractionHand hand, boolean consume, CompoundTag extraData);
}
