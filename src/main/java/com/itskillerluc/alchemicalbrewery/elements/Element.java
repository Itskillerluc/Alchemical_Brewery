package com.itskillerluc.alchemicalbrewery.elements;

import com.itskillerluc.alchemicalbrewery.entity.custom.ElementProjectileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public abstract class Element extends ForgeRegistryEntry<Element> {
    public final String DISPLAYNAME;

    private ArrayList<Object> Arguments;

    public int Color;

    public String BoundItem = "air";

    public Element(String Displayname){
        DISPLAYNAME = (Displayname != null) ? Displayname : "Empty";
    }

    public ArrayList<Object> getArguments() {
        return Arguments;
    }

    private void FunctionWrapper(){

    }

    abstract void SetArgs (UseOnContext context);

    public final void SetArgsWrapper (UseOnContext context, boolean Consume){
        if(context == null){
            throw new NullPointerException("Context is not allowed to be null.");
        }
        SetArgs(context);
        Arguments.add(context.getClickedFace());
        Arguments.add(context.getClickedPos());
        Arguments.add(context.getLevel());
        Arguments.add(context.getPlayer());
        Arguments.add(context.getHand());
        Arguments.add(Consume);
        ElementFunction(((Direction) Arguments.get(0)), ((BlockPos) Arguments.get(1)), ((Level) Arguments.get(2)), ((LivingEntity) Arguments.get(3)), ((InteractionHand) Arguments.get(4)), ((boolean) Arguments.get(5)), (ArrayList<Object>) Arguments.subList(6, Arguments.size()-1));
    }

    abstract void SetArgs (EntityHitResult result);

    public final void SetArgsWrapper (EntityHitResult result, boolean Consume){
        if(result == null){
            throw new NullPointerException("entity is not allowed to be null.");
        }
        ElementProjectileEntity entity = ((ElementProjectileEntity) result.getEntity());
        SetArgs(result);
        Arguments.add(Direction.UP);
        Arguments.add(entity.getClickedPos());
        Arguments.add(entity.getLevel());
        Arguments.add(entity.getPlayer());
        Arguments.add(entity.getHand());
        Arguments.add(Consume);
        ElementFunction(((Direction) Arguments.get(0)), ((BlockPos) Arguments.get(1)), ((Level) Arguments.get(2)), ((LivingEntity) Arguments.get(3)), ((InteractionHand) Arguments.get(4)), ((boolean) Arguments.get(5)), (ArrayList<Object>) Arguments.subList(6, Arguments.size()-1));
    }

    abstract <T> void SetArgs (T arguments);

    public final <T> void SetArgsWrapper (T arguments, boolean Consume){
        SetArgs(arguments);
        ElementFunction(((Direction) Arguments.get(0)), ((BlockPos) Arguments.get(1)), ((Level) Arguments.get(2)), ((LivingEntity) Arguments.get(3)), ((InteractionHand) Arguments.get(4)), ((boolean) Arguments.get(5)), (ArrayList<Object>) Arguments.subList(6, Arguments.size()-1));
    }

    abstract void ElementFunction(Direction dir, BlockPos pos, Level level, LivingEntity user, InteractionHand hand, boolean consume, ArrayList<Object> arguments);
}
