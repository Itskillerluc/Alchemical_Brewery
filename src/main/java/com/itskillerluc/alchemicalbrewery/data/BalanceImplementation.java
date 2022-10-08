package com.itskillerluc.alchemicalbrewery.data;

import net.minecraft.nbt.CompoundTag;

public class BalanceImplementation implements IBalanceCapability{
    private int balance;

    @Override
    public int getBalance() {
        return balance;
    }

    @Override
    public void setBalance(int balance) {
        this.balance = balance;
    }

    @Override
    public void subBalance(int balance) {
        this.balance -= balance;
    }

    @Override
    public void addBalance(int balance) {
        this.balance += balance;
    }

    @Override
    public CompoundTag serializeNBT() {
        final CompoundTag tag = new CompoundTag();
        tag.putInt("balance", this.balance);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.balance = nbt.getInt("balance");
    }
}
