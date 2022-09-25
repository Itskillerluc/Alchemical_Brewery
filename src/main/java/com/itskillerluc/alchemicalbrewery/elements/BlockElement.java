package com.itskillerluc.alchemicalbrewery.elements;

import com.itskillerluc.alchemicalbrewery.util.ModTags;
import com.itskillerluc.alchemicalbrewery.util.Utilities;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToIntFunction;

public class BlockElement extends Element {

    public BlockElement(String displayName) {
        super(displayName, Util.make(() -> {
            CompoundTag tag = new CompoundTag();
            tag.put("block", NbtUtils.writeBlockState(Blocks.AIR.defaultBlockState()));
            return tag;
        }), null, 0, 0);
    }

    @Override
    public ToIntFunction<ItemStack> getDynamicColor() {
        return stack -> ((BlockItem) stack.getItem()).getBlock().defaultMaterialColor().col;
    }

    @Override
    public Function<CompoundTag, String> getName(){
        return tag -> NbtUtils.readBlockState(tag.getCompound("block")).getBlock().getName().getString();
    }

    @Override
    public ToIntFunction<CompoundTag> getColor(){
        return tag -> NbtUtils.readBlockState(tag.getCompound("block")).getBlock().defaultMaterialColor().col;
    }

    @Override
    public @Nullable CompoundTag extractorRecipeHelper(ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof BlockItem) || itemStack.is(ModTags.Items.EXTRACTION_BLOCK_EXCEPTIONS)) {
            return null;
        }
        CompoundTag tag = new CompoundTag();
        tag.put("block", NbtUtils.writeBlockState(((BlockItem) itemStack.getItem()).getBlock().defaultBlockState()));
        return tag;
    }

    @Override
    public @Nullable Item injectorRecipeHelper(CompoundTag extraData) {
        if (extraData == null){
            return null;
        }
        var toReturn = NbtUtils.readBlockState(extraData.getCompound("element").getCompound("additionalData").getCompound("block")).getBlock().asItem();
        if (new ItemStack(toReturn).is(ModTags.Items.INJECTION_BLOCK_EXCEPTIONS)){
            return null;
        }
        return toReturn;
    }

    @Override
    public boolean matches(ElementData element, ElementData other) {
        return super.matches(element, other) && element.additionalData.getCompound("block").equals(other.additionalData.getCompound("block"));
    }

    @Override
    protected ElementData fromTagUnsafe(CompoundTag tag, Element type) {
        return new ElementData(
                tag.contains("displayName") ? tag.getString("displayName") : NbtUtils.readBlockState(tag.getCompound("additionalData").getCompound("block")).getBlock().getName().getString(),
                tag.contains("itemModel") ? ItemStack.of(tag.getCompound("itemModel")) : type.defaultItemModel,
                tag.contains("color") ? tag.getInt("color") : Block.byItem(ItemStack.of(tag.getCompound("additionalData").getCompound("block")).getItem()).defaultMaterialColor().col,
                tag.contains("secColor") ? tag.getInt("secColor") : new Color(Block.byItem(ItemStack.of(tag.getCompound("additionalData").getCompound("block")).getItem()).defaultMaterialColor().col).darker().getRGB(),
                tag.contains("additionalData") ? tag.getCompound("additionalData") : type.defaultAdditionalData,
                type);
    }



    @Override
    void elementFunction(Direction dir, BlockPos pos, Level level, LivingEntity user, InteractionHand hand, boolean consume, CompoundTag extraData) {
        BlockState blockState = NbtUtils.readBlockState(extraData.getCompound("block"));
        BlockPos newPos = pos.relative(dir);

        if (blockState.getMaterial().isReplaceable() && dir.equals(Direction.UP) && level.getBlockState(newPos.below()).getMaterial().isReplaceable()) {
            newPos = newPos.below();
        }
        if (level.isClientSide() || !level.getBlockState(newPos).getMaterial().isReplaceable()) {
            return;
        }
        level.setBlock(newPos, blockState, 3);

        if (user != null && consume && !Objects.requireNonNull(user.getItemInHand(hand).getTag()).getBoolean("Creative")) {
            user.setItemInHand(hand, Utilities.DecodeStackTags(new ItemStack(user.getItemInHand(hand).getItem(), user.getItemInHand(hand).getCount() - 1, user.getItemInHand(hand).getTag())));
        }
    }
}
