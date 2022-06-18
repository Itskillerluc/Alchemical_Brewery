package com.itskillerluc.alchemicalbrewery.entity.custom;

import com.itskillerluc.alchemicalbrewery.AlchemicalBrewery;
import com.itskillerluc.alchemicalbrewery.elements.Element;
import com.itskillerluc.alchemicalbrewery.elements.ModElements;
import com.itskillerluc.alchemicalbrewery.entity.ModEntityTypes;
import com.itskillerluc.alchemicalbrewery.item.custom.elements.ElementInit;
import com.itskillerluc.alchemicalbrewery.item.custom.elements.elementfunctions;
import com.mojang.logging.LogUtils;
import com.mojang.math.Vector3f;
import net.minecraft.ResourceLocationException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ElementProjectileEntity extends AbstractHurtingProjectile {
    private static final EntityDataAccessor<CompoundTag> ELEMENT = SynchedEntityData.defineId(ElementProjectileEntity.class, EntityDataSerializers.COMPOUND_TAG);
    public Element element = Element.EMPTY;

    Random r1 = new Random();
    public float random1 = 0.05f + r1.nextFloat() * (0.1f - 0.05f);
    Random r2 = new Random();
    public float random2 = 0.05f + r2.nextFloat() * (0.1f - 0.05f);
    Random r3 = new Random();
    public float random3 = 0.05f + r3.nextFloat() * (0.1f - 0.05f);



    public LivingEntity Owner;

    public ElementProjectileEntity(EntityType<? extends ElementProjectileEntity> type, Level level) {
        super(type, level);
    }
    public ElementProjectileEntity(Level level, LivingEntity owner, double x, double y, double z, double xpower, double ypower, double zpower, Element element) {
        super(ModEntityTypes.ELEMENTPROJECTILE.get(), x, y, z, xpower, ypower, zpower, level);
        this.Owner = owner;
        setElement(element.getRegistryName().toString());
    }

    public ElementProjectileEntity(Level level, double x, double y, double z, double xpower, double ypower, double zpower) {
        super(ModEntityTypes.ELEMENTPROJECTILE.get(), x, y, z, xpower, ypower, zpower, level);
    }

    public ElementProjectileEntity(Level level, double x, double y, double z, double xpower, double ypower, double zpower, String element) {
        super(ModEntityTypes.ELEMENTPROJECTILE.get(), x, y, z, xpower, ypower, zpower, level);
        //setElement(element);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> pKey) {
        if(ModElements.ELEMENTS.get().containsKey(new ResourceLocation(AlchemicalBrewery.MOD_ID, this.entityData.get(ELEMENT).getString("Key")))){
            element = ModElements.ELEMENTS.get().getValue(new ResourceLocation(AlchemicalBrewery.MOD_ID, this.entityData.get(ELEMENT).getString("Key")));
        }else{
            LogManager.getLogger().warn(this.entityData.get(ELEMENT).getString("Key")+" Isn't a valid element");
        }

    }

    @Override
    protected ParticleOptions getTrailParticle() {
        return new DustParticleOptions(new Vector3f(Vec3.fromRGB24(this.element.color)), 0);
    }

    @Override
        public void tick() {
            Entity entity = this.getOwner();
            if (this.level.isClientSide || (entity == null || !entity.isRemoved()) && this.level.hasChunkAt(this.blockPosition())) {
                super.tick();
                if (this.shouldBurn()) {
                    this.setSecondsOnFire(1);
                }

                HitResult hitresult = ProjectileUtil.getHitResult(this, this::canHitEntity);
                if (hitresult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult)) {
                    this.onHit(hitresult);
                }

                this.checkInsideBlocks();
                Vec3 vec3 = this.getDeltaMovement();
                double d0 = this.getX() + vec3.x;
                double d1 = this.getY() + vec3.y;
                double d2 = this.getZ() + vec3.z;
                ProjectileUtil.rotateTowardsMovement(this, 0.2F);
                float f = this.getInertia();
                if (this.isInWater()) {
                    for (int i = 0; i < 4; ++i) {
                        float f1 = 0.25F;
                        this.level.addParticle(ParticleTypes.BUBBLE, d0 - vec3.x * 0.25D, d1 - vec3.y * 0.25D, d2 - vec3.z * 0.25D, vec3.x, vec3.y, vec3.z);
                    }

                    f = 0.8F;
                }

                this.setDeltaMovement(vec3.add(this.xPower, this.yPower, this.zPower).scale((double) f));
                this.setPos(d0, d1, d2);
            } else {
                this.discard();
            }
            if (this.level.isClientSide) {
                Vec3 deltaMovement = this.getDeltaMovement();
                List<Integer> color = new ArrayList<>();
                int[] intarray = Arrays.stream(Integer.toString(this.element.color).split("(?<=\\G...)")).map((ele) -> "#" + ele).mapToInt(Integer::decode).toArray();
                for (int i : intarray) {
                    color.add(i);
                }
                while (color.size() < 3) {
                    color.add(0);
                }
                this.level.addParticle(new DustParticleOptions(new Vector3f(Vec3.fromRGB24(this.element.color)), 1), this.getX() - deltaMovement.x, this.getY() + 0.2 - deltaMovement.y, this.getZ() - deltaMovement.z, 0.0D, 0.0D, 0.0D);
            }
        }
    @Override
    protected boolean shouldBurn() {
        return false;
    }

    public void setElement(CompoundTag elementTag) {
        this.getEntityData().set(ELEMENT, elementTag);
    }

    protected CompoundTag getElementRaw() {
        return this.getEntityData().get(ELEMENT);
    }


    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(ELEMENT, new CompoundTag());
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
            useElement(pos, Direction.UP);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        if (!this.level.isClientSide && getElement() != null) {
            BlockPos pos = pResult.getBlockPos();
            useElement(pos, pResult.getDirection());
        }
    }


    /**
     * runs the element that is inside of the nbt.
     * @param pos position where it should execute
     * @param dir side of the block that is being hit (itll appear on that side of the block)
     */
    private void useElement(BlockPos pos, Direction dir) {
        try {
            if(ElementInit.functions.containsKey(getElement())) {
                ElementInit.functions.get(getElement()).run(dir, pos, this.level, Owner, InteractionHand.MAIN_HAND, false, ElementInit.entityargs.get(getElement()).apply(this));
            }else {
                elementfunctions.block(dir, pos, this.level, Owner, InteractionHand.MAIN_HAND, false, ElementInit.entityargs.get(("Block")).apply(this));
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
    @Override
    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        Element element = this.element;
        if (element != Element.EMPTY) {
            pCompound.putString("Key", element.getRegistryName().toString());
            pCompound.put("Data", element.getDataCompound());
        }
    }
    @Override
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        if (pCompound.contains("Data")){
            this.element.setDataCompound(pCompound);
        }
    }

}
