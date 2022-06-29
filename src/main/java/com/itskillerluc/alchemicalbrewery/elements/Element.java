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


    /**
     * dont use this unless you HAVE TO in very specific cases. use the wrapper in ElementData class instead
     */
    public CompoundTag toTag(ElementData data) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("color", data.color);
        tag.putString("displayName", data.displayName);
        tag.put("additionalData",data.additionalData);
        tag.put("itemModel", new CompoundTag());
        data.itemModel.save(tag.getCompound("itemModel"));
        tag.putString("type",this.getRegistryName().toString());
        return tag;
    }


    /**
     * dont use this unless you HAVE TO in very specific cases. use the wrapper in ElementData class instead
     */
    public ElementData fromTag(CompoundTag tag) {
        ElementData toReturn = new ElementData(
                tag.contains("displayName") ? tag.getString("displayName") : defaultDisplayName,
                tag.contains("itemModel") ? ItemStack.of(tag.getCompound("itemModel")) : defualtItemModel,
                tag.contains("color") ? tag.getInt("color") : defaultColor,
                tag.contains("additionalData") ? tag.getCompound("additionalData") : defaultAdditionalData,
                tag.contains("type") ? ModElements.ELEMENTS.get().getValue(ResourceLocation.tryParse(tag.getString("type"))) : ModElements.EMPTY.get());
        return toReturn;
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
