package com.itskillerluc.alchemicalbrewery.fluid.custom;
//TODO
import com.itskillerluc.alchemicalbrewery.fluid.ModFluids;
import com.itskillerluc.alchemicalbrewery.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.*;

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
                pLevel.setBlock(pPos, ModFluids.CHEMICAL_BLOCK.get().defaultBlockState(), 3);
            }
        }
        super.entityInside(pState, pLevel, pPos, pEntity);
    }


}
