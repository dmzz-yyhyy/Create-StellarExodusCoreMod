package io.nightfis.createstellarexoduscore.network

import io.nightfis.createstellarexoduscore.StellarExodusCore
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.simple.SimpleChannel
import java.util.function.Supplier

object ModNetwork {

    private const val PROTOCOL_VERSION = "1"

    @JvmField
    val channel: SimpleChannel = NetworkRegistry.newSimpleChannel(
        StellarExodusCore.of("main"),
        { PROTOCOL_VERSION },
        { it == PROTOCOL_VERSION },
        { it == PROTOCOL_VERSION }
    )

    @Suppress("AssignedValueIsNeverRead")
    fun init() {
        var index = 0
        registerMessage(
            index++,
            AddAutoAimTurretFilterPacket::class.java,
            AddAutoAimTurretFilterPacket::encode,
            AddAutoAimTurretFilterPacket::decode,
            AddAutoAimTurretFilterPacket::handle
        )
        registerMessage(
            index++,
            RemoveAutoAimTurretFilterPacket::class.java,
            RemoveAutoAimTurretFilterPacket::encode,
            RemoveAutoAimTurretFilterPacket::decode,
            RemoveAutoAimTurretFilterPacket::handle
        )
        registerMessage(
            index++,
            AutoAimTurretFilterSyncPacket::class.java,
            AutoAimTurretFilterSyncPacket::encode,
            AutoAimTurretFilterSyncPacket::decode,
            AutoAimTurretFilterSyncPacket::handle
        )
        registerMessage(
            index++,
            SaveShuttleStructurePacket::class.java,
            SaveShuttleStructurePacket::encode,
            SaveShuttleStructurePacket::decode,
            SaveShuttleStructurePacket::handle
        )
        registerMessage(
            index++,
            PlaceShuttleStructurePacket::class.java,
            PlaceShuttleStructurePacket::encode,
            PlaceShuttleStructurePacket::decode,
            PlaceShuttleStructurePacket::handle
        )
    }

    @Suppress("INFERRED_INVISIBLE_RETURN_TYPE_WARNING")
    private fun <T> registerMessage(
        index: Int,
        type: Class<T>,
        encoder: (T, FriendlyByteBuf) -> Unit,
        decoder: (FriendlyByteBuf) -> T,
        handler: (T, Supplier<NetworkEvent.Context>) -> Unit
    ) {
        channel.registerMessage(index, type, encoder, decoder, handler)
    }
}
