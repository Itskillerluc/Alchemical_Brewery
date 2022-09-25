package com.itskillerluc.alchemicalbrewery.entity.custom;

import com.itskillerluc.alchemicalbrewery.elements.ElementData;
import com.itskillerluc.alchemicalbrewery.elements.ModElements;
import com.itskillerluc.alchemicalbrewery.entity.ModEntityTypes;
import com.mojang.logging.LogUtils;
import com.mojang.math.Vector3f;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
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
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ElementProjectileEntity extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<ElementData> ELEMENT_DATA = SynchedEntityData.defineId(ElementProjectileEntity.class, ElementData.ELEMENT_DATA);
    static final ElementData EMPTY = new ElementData(ModElements.EMPTY.get());
    Random r1 = new Random();
    Random r2 = new Random();
    Random r3 = new Random();
    public float random1 = 0.05f + r1.nextFloat() * (0.1f - 0.05f);
    public float random2 = 0.05f + r2.nextFloat() * (0.1f - 0.05f);
    public float random3 = 0.05f + r3.nextFloat() * (0.1f - 0.05f);

    public LivingEntity Owner;

    public ElementProjectileEntity(EntityType<? extends ElementProjectileEntity> type, Level level) {
        super(type, level);
    }

    public ElementProjectileEntity(Level level, LivingEntity owner, double x, double y, double z, double xPower, double yPower, double zPower, ElementData element) {
        super(ModEntityTypes.ELEMENT_PROJECTILE.get(), x, y, z, xPower, yPower, zPower, level);
        this.Owner = owner;
        setElement(element);
    }

    @SuppressWarnings("unused")
    public ElementProjectileEntity(Level level, double x, double y, double z, double xPower, double yPower, double zPower) {
        super(ModEntityTypes.ELEMENT_PROJECTILE.get(), x, y, z, xPower, yPower, zPower, level);
    }

    @SuppressWarnings("unused")
    public ElementProjectileEntity(Level level, double x, double y, double z, double xPower, double yPower, double zPower, ElementData element) {
        super(ModEntityTypes.ELEMENT_PROJECTILE.get(), x, y, z, xPower, yPower, zPower, level);
        setElement(element);
    }

    public void setElement(ElementData element) {
        this.entityData.set(ELEMENT_DATA, element);
    }

    public ElementData getElement() {
        return this.entityData.get(ELEMENT_DATA);
    }

    @Override
    protected @NotNull ParticleOptions getTrailParticle() {
        return new DustParticleOptions(new Vector3f(Vec3.fromRGB24(getElement().color)), 0);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level.isClientSide) {
            Vec3 deltaMovement = this.getDeltaMovement();
            List<Integer> color = new ArrayList<>();
            int[] intArray = Arrays.stream(Integer.toString(getElement().color).split("(?<=\\G...)")).map((ele) -> "#" + ele).mapToInt(Integer::decode).toArray();
            for (int i : intArray) {
                color.add(i);
            }
            while (color.size() < 3) {
                color.add(0);
            }
            this.level.addParticle(new DustParticleOptions(new Vector3f(Vec3.fromRGB24(getElement().color)), 1), this.getX() - deltaMovement.x, this.getY() + 0.2 - deltaMovement.y, this.getZ() - deltaMovement.z, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected boolean shouldBurn() {
        return false;
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(ELEMENT_DATA, EMPTY);
    }

    protected void onHit(@NotNull HitResult pResult) {
        super.onHit(pResult);
        if (!this.level.isClientSide) {
            this.discard();
        }
    }
    public boolean isPickable() {
        return false;
    }
    public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
        return false;
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult pResult) {
        super.onHitEntity(pResult);
        onProjectileHit(pResult);
    }

    public void onProjectileHit(@NotNull EntityHitResult pResult) {
        if (this.level.isClientSide || getElement() == null || getElement().elementType == EMPTY.elementType) {
            return;
        }
        try {
            getElement().run(pResult, this);
        }
        catch (NullPointerException | ResourceLocationException exception){
            if(getElement() != null){
                LogUtils.getLogger().debug(getElement()+" is not a valid element type");
            }else{
                LogUtils.getLogger().debug("No element type found");
            }
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult pResult) {
        super.onHitBlock(pResult);
        if (!this.level.isClientSide && getElement() != null && getElement().elementType != EMPTY.elementType) {
            try {
                getElement().run(pResult, this);
            }
            catch (NullPointerException | ResourceLocationException exception){
                if(getElement() != null){
                    LogUtils.getLogger().debug(getElement()+" is not a valid element type");
                }else{
                    LogUtils.getLogger().debug("No element type found");
                }
            }
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.put("element", this.getElement().toTag());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (this.getElement() == null){
            if(ModElements.ELEMENTS.get().containsKey(new ResourceLocation(pCompound.getCompound("element").getString("type")))) {
                this.setElement(Objects.requireNonNull(ModElements.ELEMENTS.get().getValue(new ResourceLocation(pCompound.getCompound("element").getString("type")))).fromTagSafe(pCompound.getCompound("element")));
            }else{
                this.setElement(new ElementData(ModElements.EMPTY.get()).decodeFromTag(pCompound.getCompound("element")));
            }
        } else{
            this.setElement(this.getElement().decodeFromTag(pCompound.getCompound("element")));
        }
    }
}
