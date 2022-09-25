package com.itskillerluc.alchemicalbrewery.elements;

import com.itskillerluc.alchemicalbrewery.util.ModTags;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public class ItemElement extends Element{
    public ItemElement(String displayName) {
        super(displayName, Util.make(() -> {
            CompoundTag tag = new CompoundTag();
            tag.put("item", new CompoundTag());
            ItemStack.EMPTY.save(tag.getCompound("item"));
            return tag;
        }), null, 0, 0);
    }

    public static int getColorOfImage(ResourceLocation image) throws FileNotFoundException{
        InputStream is;
        BufferedImage img;
        int res = 0;
        int[] texture;
        try {
            is = Minecraft.getInstance().getResourceManager().getResource(image).getInputStream();
            img = ImageIO.read(is);
        }catch(IOException e){e.printStackTrace();return res;}
        int[] coords = {Math.round(img.getHeight()/2f), Math.round(img.getWidth()/2f)};
        texture = img.getRaster().getPixel(coords[0], coords[1], new int[4]);
        res = new Color(texture[0],texture[1],texture[2]).getRGB();
        return res;
    }

    @Override
    public ToIntFunction<ItemStack> getDynamicColor() {
        return stack -> {
            try {
                return !stack.isEmpty() ? ItemElement.getColorOfImage(new ResourceLocation(Objects.requireNonNull(stack.getItem().getRegistryName()).getNamespace(),"textures/item/"+stack.getItem().getRegistryName().getPath()+".png")) : 0;
            } catch (FileNotFoundException ignored) {}
            return 0;
        };
    }

    @Override
    public @Nullable CompoundTag extractorRecipeHelper(ItemStack itemStack) {
        if ((itemStack.getItem() instanceof BlockItem) || itemStack.is(ModTags.Items.EXTRACTION_ITEM_EXCEPTIONS)) {
            return null;
        }
        CompoundTag tag = new CompoundTag();
        tag.put("item", new CompoundTag());
        itemStack.save(tag.getCompound("item"));
        return tag;
    }

    @Override
    public @Nullable Item injectorRecipeHelper(CompoundTag extraData) {
        if (extraData == null){
            return null;
        }
        return NbtUtils.readBlockState(extraData.getCompound("element").getCompound("additionalData").getCompound("item")).getBlock().asItem();
    }

    @Override
    public Function<CompoundTag, String> getName(){
        return tag -> !ItemStack.of(tag.getCompound("item")).isEmpty() ? ItemStack.of(tag.getCompound("item")).getDisplayName().getString().substring(1, ItemStack.of(tag.getCompound("item")).getDisplayName().getString().length()-1) : "Empty";
    }

    @Override
    public ToIntFunction<CompoundTag> getColor(){
        return tag -> getDynamicColor().applyAsInt(ItemStack.of(tag.getCompound("item")));
    }


    @Override
    public boolean matches(ElementData element, ElementData other) {
        return super.matches(element, other) && element.additionalData.getCompound("item").equals(other.additionalData.getCompound("item"));
    }

    @Override
    protected ElementData fromTagUnsafe(CompoundTag tag, Element type) {
        return new ElementData(
                tag.contains("displayName") ? tag.getString("displayName") : ItemStack.of(tag.getCompound("additionalData").getCompound("item")).getDisplayName().getString(),
                tag.contains("itemModel") ? ItemStack.of(tag.getCompound("itemModel")) : type.defaultItemModel,
                tag.contains("color") ? tag.getInt("color") : getDynamicColor().applyAsInt(ItemStack.of(tag.getCompound("additionalData").getCompound("item"))),
                tag.contains("secColor") ? tag.getInt("secColor") :  new Color(getDynamicColor().applyAsInt(ItemStack.of(tag.getCompound("additionalData").getCompound("item")))).darker().getRGB(),
                tag.contains("additionalData") ? tag.getCompound("additionalData") : type.defaultAdditionalData,
                type);

    }

    @Override
    void elementFunction(Direction dir, BlockPos pos, Level level, LivingEntity user, InteractionHand hand, boolean consume, CompoundTag extraData) {}
}
