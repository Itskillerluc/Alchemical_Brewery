package com.itskillerluc.alchemicalbrewery.data;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public class BalanceCapability {
    public static final Capability<IBalanceCapability> INSTANCE = CapabilityManager.get(new CapabilityToken<>() {});

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(IBalanceCapability.class);
    }

    public static void attach(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            final BalanceCapabilityProvider provider = new BalanceCapabilityProvider();
            event.addCapability(BalanceCapabilityProvider.IDENTIFIER, provider);
        }
    }
}
