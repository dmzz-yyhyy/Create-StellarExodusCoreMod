package io.nightfis.createstellarexoduscore.network

import io.nightfis.createstellarexoduscore.inventory.AutoAimTurretMenu
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class AddAutoAimTurretFilterPacket(val id: String, val arg: String) {

    companion object {
        fun encode(msg: AddAutoAimTurretFilterPacket, buf: FriendlyByteBuf) {
            buf.writeUtf(msg.id)
            buf.writeUtf(msg.arg)
        }

        fun decode(buf: FriendlyByteBuf): AddAutoAimTurretFilterPacket {
            return AddAutoAimTurretFilterPacket(buf.readUtf(), buf.readUtf())
        }

        fun handle(msg: AddAutoAimTurretFilterPacket, ctx: Supplier<NetworkEvent.Context>) {
            val context = ctx.get()
            context.enqueueWork {
                val player: ServerPlayer? = context.sender
                val menu = player?.containerMenu
                if (menu is AutoAimTurretMenu) {
                    menu.addFilter(msg.id, msg.arg)
                }
            }
            context.packetHandled = true
        }
    }
}
