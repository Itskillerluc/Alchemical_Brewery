package com.itskillerluc.alchemicalbrewery.fluid.custom;

import com.itskillerluc.alchemicalbrewery.fluid.ModFluids;
import com.itskillerluc.alchemicalbrewery.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.tags.SetTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class AcidLiquidBlock extends LiquidBlock {

    public AcidLiquidBlock(Supplier<? extends FlowingFluid> pFluid, Properties pProperties) {
        super(pFluid, pProperties);
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if(pEntity instanceof ItemEntity item &&(!pLevel.isClientSide)){
            if (item.getItem().is(ModItems.SALT.get())){
                item.kill();
                pLevel.destroyBlock(pPos, false);
                pLevel.setBlock(pPos, ModFluids.CHEMICAL_BLOCK.get().defaultBlockState(), 2);
            }
        }
        super.entityInside(pState, pLevel, pPos, pEntity);
    }


}
