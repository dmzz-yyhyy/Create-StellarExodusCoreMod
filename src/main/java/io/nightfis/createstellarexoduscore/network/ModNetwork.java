package io.nightfis.createstellarexoduscore.network;

import io.nightfis.createstellarexoduscore.StellarExodusCore;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.tryBuild(StellarExodusCore.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    public static void init() {
        int index = 0;
        CHANNEL.registerMessage(index++, AddAutoAimTurretFilterPacket.class, AddAutoAimTurretFilterPacket::encode,
                AddAutoAimTurretFilterPacket::decode, AddAutoAimTurretFilterPacket::handle);
        CHANNEL.registerMessage(index++, RemoveAutoAimTurretFilterPacket.class, RemoveAutoAimTurretFilterPacket::encode,
                RemoveAutoAimTurretFilterPacket::decode, RemoveAutoAimTurretFilterPacket::handle);
        CHANNEL.registerMessage(index++, AutoAimTurretFilterSyncPacket.class, AutoAimTurretFilterSyncPacket::encode,
                AutoAimTurretFilterSyncPacket::decode, AutoAimTurretFilterSyncPacket::handle);
    }
}
