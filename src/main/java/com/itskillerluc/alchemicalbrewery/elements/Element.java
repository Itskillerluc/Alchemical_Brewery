package com.itskillerluc.alchemicalbrewery.elements;

import com.itskillerluc.alchemicalbrewery.entity.custom.ElementProjectileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public abstract class Element extends ForgeRegistryEntry<Element> {
    public final String DISPLAYNAME;

    private ArrayList<Object> Arguments;

    public Element(String Displayname){
        DISPLAYNAME = (Displayname != null) ? Displayname : "Empty";
    }

    public ArrayList<Object> getArguments() {
        return Arguments;
    }

    private void FunctionWrapper(){

    }

    abstract void SetArgs (UseOnContext context);

    public final void SetArgsWrapper (UseOnContext context){
        if(context == null){
            throw new NullPointerException("Context is not allowed to be null.");
        }
        SetArgs(context);
    }

    abstract void SetArgs (ElementProjectileEntity entity);

    public final void SetArgsWrapper (ElementProjectileEntity entity){
        if(entity == null){
            throw new NullPointerException("entity is not allowed to be null.");
        }
        SetArgs(entity);
    }

    abstract <T> void SetArgs (T arguments);

    public final <T> void SetArgsWrapper (T arguments){
        SetArgs(arguments);
    }

    abstract void ElementFunction(Direction dir, BlockPos pos, Level level, LivingEntity user, InteractionHand hand, boolean consume, ArrayList<Object> arguments);
}
