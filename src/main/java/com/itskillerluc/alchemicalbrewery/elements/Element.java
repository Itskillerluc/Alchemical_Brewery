package com.itskillerluc.alchemicalbrewery.elements;
//TODO
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public abstract class Element extends ForgeRegistryEntry<Element> {

    public final String defaultDisplayName;
    public final ItemStack defualtItemModel;

    public final int defaultColor;
    public final int defaultSecColor;
    public final CompoundTag defaultAdditionalData;


    /**
     * dont use this unless you HAVE TO in very specific cases. use the wrapper in ElementData class instead
     */
    public CompoundTag toTag(ElementData data) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("color", data.color);
        tag.putInt("secColor", data.secColor);
        tag.putString("displayName", data.displayName);
        tag.put("additionalData",data.additionalData);
        tag.put("itemModel", new CompoundTag());
        data.itemModel.save(tag.getCompound("itemModel"));
        tag.putString("type",this.getRegistryName().toString());
        return tag;
    }

    /**
     *Override this if you need any dynamic color
     */
    public ToIntFunction<ItemStack> getDynamicColor() {
        return stack -> defaultColor;
    }

    /**
     *Override this if you need any dynamic name
     */
    public Function<CompoundTag, String> getName(){
        return tag -> defaultDisplayName;
    }

    /**
     *Override this if you need any dynamic color
     */
    public ToIntFunction<CompoundTag> getColor(){
        return tag -> defaultColor;
    }

    /**
     *Override this if you have any additionalData
     */
    public @Nullable CompoundTag extractorRecipeHelper(ItemStack itemStack){
        return null;
    }

    /**
     *Override this if you have any additionalData
     */
    public @Nullable Item injectorRecipeHelper(CompoundTag extraData){
        return null;
    }



    /**
     * dont use this unless you HAVE TO in very specific cases. use the wrapper in ElementData class instead
     */
    public ElementData fromTag(CompoundTag tag) {
        Element type = tag.contains("type") ? ModElements.ELEMENTS.get().getValue(ResourceLocation.tryParse(tag.getString("type"))) : ModElements.EMPTY.get();
        try {
            ElementData toReturn = new ElementData(
                    tag.contains("displayName") ? tag.getString("displayName") : type.defaultDisplayName,
                    tag.contains("itemModel") ? ItemStack.of(tag.getCompound("itemModel")) : type.defualtItemModel,
                    tag.contains("color") ? tag.getInt("color") : type.defaultColor,
                    tag.contains("secColor") ? tag.getInt("secColor") : type.defaultSecColor,
                    tag.contains("additionalData") ? tag.getCompound("additionalData") : type.defaultAdditionalData,
                    type);
            return toReturn;
        }catch(NullPointerException exception){
            LogManager.getLogger().error("'"+tag.getString("type") + "'" + " Does not exist. Most likely a typo but could also mean it wasn't registered correctly");
            Minecraft.getInstance().level.players().forEach((abstractClientPlayer -> {
                abstractClientPlayer.sendMessage(new TextComponent("'"+tag.getString("type")+"'" + " Does not exist. Most likely a typo but could also mean it wasn't registered correctly"), abstractClientPlayer.getUUID());
            }));
            return new ElementData(ModElements.EMPTY.get());
        }
    }

    public Element(String defaultDisplayName, CompoundTag defaultAdditionalData, ItemStack defaultItemModel, int defaultColor, int defaultSecColor){
        this.defaultAdditionalData = defaultAdditionalData != null ? defaultAdditionalData : new CompoundTag();
        this.defualtItemModel = defaultItemModel != null ? defaultItemModel : ItemStack.EMPTY;
        this.defaultColor = defaultColor;
        this.defaultSecColor = defaultSecColor;
        this.defaultDisplayName = defaultDisplayName != null ? defaultDisplayName : "Empty";
    }

    public boolean matches(ElementData element, ElementData other){
        if(element.elementType.getRegistryName() == null || other.elementType.getRegistryName() == null){
            return false;
        }
        return element.elementType.getRegistryName().equals(other.elementType.getRegistryName());
    }


    /**
     * Override this to define what the element does.
     */
    abstract void elementFunction(Direction dir, BlockPos pos, Level level, LivingEntity user, InteractionHand hand, boolean consume, CompoundTag extraData);

    public int getDefaultColor() {
        return defaultColor;
    }
}
