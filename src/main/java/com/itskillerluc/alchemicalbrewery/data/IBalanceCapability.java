package com.itskillerluc.alchemicalbrewery.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public interface IBalanceCapability extends INBTSerializable<CompoundTag> {
    int getBalance();
    void setBalance(int balance);
    void subBalance(int balance);
    void addBalance(int balance);
}
