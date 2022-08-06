package com.itskillerluc.alchemicalbrewery.elements;

import com.itskillerluc.alchemicalbrewery.util.ModTags;
import com.itskillerluc.alchemicalbrewery.util.Utilities;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.Function;

public class ItemElement extends Element{
    public ItemElement(String Displayname) {
        super(Displayname, Util.make(() -> {
            CompoundTag tag = new CompoundTag();
            tag.put("item", new CompoundTag());
            ItemStack.EMPTY.save(tag.getCompound("item"));
            return tag;
        }), null, 0, 0);
    }

    public static int getColorOfImage(ResourceLocation image){
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
    public Function<ItemStack, Integer> getDynamicColor() {
        return stack -> ItemElement.getColorOfImage(new ResourceLocation(Objects.requireNonNull(stack.getItem().getRegistryName()).getNamespace(),"textures/item/"+stack.getItem().getRegistryName().getPath()+".png"));
    }

    public static Color darker(Color color, float modifier) {
        return new Color(Math.max((int)(color.getRed()  *modifier), 0),
                Math.max((int)(color.getGreen()*modifier), 0),
                Math.max((int)(color.getBlue() *modifier), 0),
                color.getAlpha());
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
    public boolean matches(ElementData element, ElementData other) {
        return super.matches(element, other) && element.additionalData.getCompound("item").equals(other.additionalData.getCompound("item"));
    }

    @Override
    public ElementData fromTag(CompoundTag tag) {
        Element type = tag.contains("type") ? ModElements.ELEMENTS.get().getValue(ResourceLocation.tryParse(tag.getString("type"))) : ModElements.EMPTY.get();
        try {
            tag.getCompound("additionalData").getCompound("item");
            if(type != null){
                return new ElementData(
                        tag.contains("displayName") ? tag.getString("displayName") : type.defaultDisplayName,
                        tag.contains("itemModel") ? ItemStack.of(tag.getCompound("itemModel")) : type.defualtItemModel,
                        Block.byItem(ItemStack.of(tag.getCompound("additionalData").getCompound("item")).getItem()).defaultMaterialColor().col,
                        Block.byItem(ItemStack.of(tag.getCompound("additionalData").getCompound("item")).getItem()).defaultMaterialColor().col,
                        tag.contains("additionalData") ? tag.getCompound("additionalData") : type.defaultAdditionalData,
                        type);
            }else{
                LogManager.getLogger().error("type is not allowed to be null");
                return new ElementData(ModElements.EMPTY.get());
            }
        }catch(NullPointerException exception){
            LogManager.getLogger().error("'"+tag.getString("type") + "'" + " Does not exist. Most likely a typo but could also mean it wasn't registered correctly");
            if (Minecraft.getInstance().level != null) {
                Minecraft.getInstance().level.players().forEach((abstractClientPlayer -> abstractClientPlayer.sendMessage(new TextComponent("'" + tag.getString("type") + "'" + " Does not exist. Most likely a typo but could also mean it wasn't registered correctly"), abstractClientPlayer.getUUID())));
            }
            return new ElementData(ModElements.EMPTY.get());
        }
    }

    @Override
    void elementFunction(Direction dir, BlockPos pos, Level level, LivingEntity user, InteractionHand hand, boolean consume, CompoundTag extraData) {

    }
}
