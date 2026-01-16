package io.nightfis.createstellarexoduscore.network

import io.nightfis.createstellarexoduscore.inventory.AutoAimTurretMenu
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class RemoveAutoAimTurretFilterPacket(val id: String, val arg: String) {

    companion object {
        @JvmStatic
        fun encode(msg: RemoveAutoAimTurretFilterPacket, buf: FriendlyByteBuf) {
            buf.writeUtf(msg.id)
            buf.writeUtf(msg.arg)
        }

        @JvmStatic
        fun decode(buf: FriendlyByteBuf): RemoveAutoAimTurretFilterPacket {
            return RemoveAutoAimTurretFilterPacket(buf.readUtf(), buf.readUtf())
        }

        @JvmStatic
        fun handle(msg: RemoveAutoAimTurretFilterPacket, ctx: Supplier<NetworkEvent.Context>) {
            val context = ctx.get()
            context.enqueueWork {
                val player: ServerPlayer? = context.sender
                val menu = player?.containerMenu
                if (menu is AutoAimTurretMenu) {
                    menu.removeFilter(msg.id, msg.arg)
                }
            }
            context.packetHandled = true
        }
    }
}
