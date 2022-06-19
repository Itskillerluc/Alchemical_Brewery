package com.itskillerluc.alchemicalbrewery.elements;

import com.itskillerluc.alchemicalbrewery.entity.custom.ElementProjectileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
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
    public static EntityDataSerializer<Element> ELEMENTDATA;

    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("color", this.color);
        return tag;
    }

    public  abstract <T extends Element> T instanciate();

    public Element fromTag(CompoundTag tag) {
        this.color = tag.getInt("color");
        return this;
    }

    public Element(String Displayname){
        ELEMENTDATA = new EntityDataSerializer<>() {
            @Override
            public void write(FriendlyByteBuf pBuffer, Element pValue) {
                if(pValue.getRegistryName() != null) {
                    pBuffer.writeResourceLocation(pValue.getRegistryName());
                }
            }

            @Override
            public Element read(FriendlyByteBuf pBuffer) {
                ResourceLocation key = pBuffer.readResourceLocation();
                return ModElements.ELEMENTS.get().containsKey(key) ? ModElements.ELEMENTS.get().getValue(key).instanciate() : ModElements.EMPTY.get().instanciate();
            }

            @Override
            public Element copy(Element pValue) {
                return pValue;
            }
        };

        EntityDataSerializers.registerSerializer(ELEMENTDATA);
        DISPLAYNAME = (Displayname != null) ? Displayname : "Empty";
    }

    public Element(Element element){
        this.DISPLAYNAME = element.DISPLAYNAME;
        this.itemModel = element.itemModel;
        this.color = element.color;
        this.setRegistryName(element.getRegistryName());
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
        elementFunction(context.getClickedFace(), context.getClickedPos(), context.getLevel(), context.getPlayer(), context.getHand(), Consume);
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
        elementFunction(result.getDirection(), result.getBlockPos(), entity.getLevel(), (LivingEntity)entity.getOwner(), InteractionHand.MAIN_HAND, false);
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
        elementFunction(Direction.UP, entity.blockPosition(), entity.getLevel(), (LivingEntity)entity.getOwner(), InteractionHand.MAIN_HAND, false);
    }
    /**
     * Override this to define what the element does.
     */
    abstract void elementFunction(Direction dir, BlockPos pos, Level level, LivingEntity user, InteractionHand hand, boolean consume);
}
