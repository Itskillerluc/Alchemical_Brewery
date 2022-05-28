package com.itskillerluc.alchemicalbrewery.elements;

import com.itskillerluc.alchemicalbrewery.entity.custom.ElementProjectileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;


import java.util.ArrayList;

public class TestElement extends Element{
    public TestElement(String Displayname) {
        super(Displayname);
    }

    @Override
    void SetArgs(UseOnContext context) {
        /*
        try {
            context.getItemInHand().getTag().putInt("ItemColor",Integer.decode(ImageColor.getHexColor(ImageIO.read(Files.newInputStream(ResourceLocation.)))));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    void SetArgs(ElementProjectileEntity entity) {

    }

    @Override
    <T> void SetArgs(T arguments) {

    }

    @Override
    void ElementFunction(Direction dir, BlockPos pos, Level level, LivingEntity user, InteractionHand hand, boolean consume, ArrayList<Object> arguments) {

    }
}
