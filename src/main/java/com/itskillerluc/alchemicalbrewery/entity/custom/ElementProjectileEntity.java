package com.itskillerluc.alchemicalbrewery.entity.custom;

import com.itskillerluc.alchemicalbrewery.entity.ModEntityTypes;
import com.itskillerluc.alchemicalbrewery.item.custom.Element_UseItem;
import com.mojang.logging.LogUtils;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.registries.ForgeRegistries;

public class ElementProjectileEntity extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<String> DATA_ELEMENT = SynchedEntityData.defineId(ElementProjectileEntity.class, EntityDataSerializers.STRING);

    public LivingEntity Owner;

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    public ElementProjectileEntity(EntityType<? extends ElementProjectileEntity> type, Level level) {
        super(type, level);
    }
    public ElementProjectileEntity(Level level, LivingEntity owner, double x, double y, double z) {
        super(ModEntityTypes.ELEMENTPROJECTILE.get(), owner, x, y, z, level);
        this.Owner = owner;
    }

    public ElementProjectileEntity(Level level, double x, double y, double z, double xpower, double ypower, double zpower) {
        super(ModEntityTypes.ELEMENTPROJECTILE.get(), x, y, z, xpower, ypower, zpower, level);
    }

    public void setElement(String element) {
        this.getEntityData().set(DATA_ELEMENT, element);
    }

    protected String getElementRaw() {
        return this.getEntityData().get(DATA_ELEMENT);
    }

    public String getElement() {
        String element = this.getElementRaw();
        return getElementRaw().isEmpty() ? null : element;
    }
    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_ELEMENT, "None");
    }

    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (!this.level.isClientSide) {
            this.discard();
        }

    }

    public boolean isPickable() {
        return false;
    }
    public boolean hurt(DamageSource pSource, float pAmount) {
        return false;
    }

    @Override
    protected void onHitEntity(EntityHitResult pResult) {
        super.onHitEntity(pResult);
        if (!this.level.isClientSide && getElement() != null) {
            BlockPos pos = pResult.getEntity().blockPosition();
            useElement(pos);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        if (!this.level.isClientSide && getElement() != null) {
            BlockPos pos = pResult.getBlockPos();
            useElement(pos);
        }
    }


    private void useElement(BlockPos pos) {
        try {
            if (getElement().matches("Lava")) {
                Element_UseItem.elementfunctions.Lava(Direction.UP,pos, this.level, Owner);
            } else if (getElement().matches("Water")) {
                Element_UseItem.elementfunctions.Water(Direction.UP,pos, this.level, Owner);
            }else if(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(getElement())) != null){
                Element_UseItem.elementfunctions.Block(Direction.UP,pos, this.level, Owner, ForgeRegistries.BLOCKS.getValue(new ResourceLocation((getElement()))));
            }
        }
        catch (NullPointerException | ResourceLocationException exception){
            if(getElement() != null){
                LogUtils.getLogger().debug(getElement()+" is not a valid element type");
            }else{
                LogUtils.getLogger().debug("No element type found");
            }
        }
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        String element = this.getElementRaw();
        if (!element.isEmpty()) {
            pCompound.putString("Element", element);
        }

    }

    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        String element = pCompound.getString("Element");
        this.setElement(element);
    }

}
