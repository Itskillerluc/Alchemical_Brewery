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
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

@SuppressWarnings("SpellCheckingInspection")
public class BlockElement extends Element {

    public BlockElement(String Displayname) {
        super(Displayname, Util.make(() -> {
            CompoundTag tag = new CompoundTag();
            tag.put("block", NbtUtils.writeBlockState(Blocks.AIR.defaultBlockState()));
            return tag;
        }), null, 0, 0);
    }

    @Override
    public Function<ItemStack, Integer> getDynamicColor() {
        return stack -> ((BlockItem) stack.getItem()).getBlock().defaultMaterialColor().col;
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
    public boolean matches(ElementData element, ElementData other) {
        return super.matches(element, other) && element.additionalData.getCompound("block").equals(other.additionalData.getCompound("block"));
    }

    @Override
    public ElementData fromTag(CompoundTag tag) {

        Element type = tag.contains("type") ? ModElements.ELEMENTS.get().getValue(ResourceLocation.tryParse(tag.getString("type"))) : ModElements.EMPTY.get();
        try {
            tag.getCompound("additionalData").getCompound("block");
            if(type != null){
                return new ElementData(
                        tag.contains("displayName") ? tag.getString("displayName") : type.defaultDisplayName,
                        tag.contains("itemModel") ? ItemStack.of(tag.getCompound("itemModel")) : type.defualtItemModel,
                        Block.byItem(ItemStack.of(tag.getCompound("additionalData").getCompound("block")).getItem()).defaultMaterialColor().col,
                        Block.byItem(ItemStack.of(tag.getCompound("additionalData").getCompound("block")).getItem()).defaultMaterialColor().col,
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
        BlockState blockState = NbtUtils.readBlockState(extraData.getCompound("block"));
        BlockPos newpos;
        newpos = switch (dir) {
            case UP -> pos.above();
            case DOWN -> pos.below();
            case EAST -> pos.east();
            case WEST -> pos.west();
            case NORTH -> pos.north();
            case SOUTH -> pos.south();
        };
        if (blockState.getMaterial().isReplaceable() && dir.equals(Direction.UP)) {
            if (level.getBlockState(newpos.below()).getMaterial().isReplaceable()) {
                newpos = newpos.below();
            }
        }
        if (!level.isClientSide() && level.getBlockState(newpos).getMaterial().isReplaceable()) {
            if (blockState.getMaterial().isReplaceable()) {
                if (!level.getBlockState(newpos.below()).isAir()) {
                    level.setBlock(newpos, blockState, 3);
                }
            } else {
                level.setBlock(newpos, blockState, 3);
                if (user != null && consume) {
                    if (!Objects.requireNonNull(user.getItemInHand(hand).getTag()).getBoolean("Creative")) {
                        user.setItemInHand(hand, Utilities.DecodeStackTags(new ItemStack(user.getItemInHand(hand).getItem(), user.getItemInHand(hand).getCount() - 1, user.getItemInHand(hand).getTag())));
                    }
                }
            }
        }
    }
}
