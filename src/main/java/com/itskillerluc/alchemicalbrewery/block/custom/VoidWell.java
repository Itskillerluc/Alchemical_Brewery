package com.itskillerluc.alchemicalbrewery.block.custom;
//TODO
import com.itskillerluc.alchemicalbrewery.item.ModItems;
import com.itskillerluc.alchemicalbrewery.item.custom.Element_Basic;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.stream.Stream;

public class VoidWell extends Block {
    public VoidWell(Properties p_49795_) {
        super(p_49795_);
    }

    private static final VoxelShape Model = Stream.of(
            Block.box(0.75, 0, 0.75, 15.25, 14.25, 15.25)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return Model;
    }

    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if (!pLevel.isClientSide()) {
            if (pEntity instanceof ItemEntity) {
                pEntity.kill();
            }else {
                pEntity.hurt(DamageSource.OUT_OF_WORLD, 4);
            }
        }
    }
}