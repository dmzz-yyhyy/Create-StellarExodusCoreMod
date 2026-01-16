package io.nightfis.createstellarexoduscore.network

import io.nightfis.createstellarexoduscore.client.gui.screen.AutoAimTurretScreen
import io.nightfis.createstellarexoduscore.filter.TargetFilter
import it.unimi.dsi.fastutil.Pair
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.ArrayList
import java.util.function.Supplier

class AutoAimTurretFilterSyncPacket(filters: List<Pair<TargetFilter, String>>) {

    val filters: List<Pair<TargetFilter, String>> = ArrayList(filters)

    companion object {
        fun encode(msg: AutoAimTurretFilterSyncPacket, buf: FriendlyByteBuf) {
            buf.writeCollection(msg.filters.map { it.left().getId() }, FriendlyByteBuf::writeUtf)
            buf.writeCollection(msg.filters.map { it.right() }, FriendlyByteBuf::writeUtf)
        }

        fun decode(buf: FriendlyByteBuf): AutoAimTurretFilterSyncPacket {
            val ids = buf.readList(FriendlyByteBuf::readUtf)
            val args = buf.readList(FriendlyByteBuf::readUtf)
            val filters = ArrayList<Pair<TargetFilter, String>>()
            val count = minOf(args.size, ids.size)
            for (i in 0 until count) {
                filters.add(Pair.of(TargetFilter.getFilter(ids[i]), args[i]))
            }
            return AutoAimTurretFilterSyncPacket(filters)
        }

        fun handle(msg: AutoAimTurretFilterSyncPacket, ctx: Supplier<NetworkEvent.Context>) {
            val context = ctx.get()
            context.enqueueWork {
                val screen = Minecraft.getInstance().screen
                if (screen is AutoAimTurretScreen) {
                    screen.handleSync(msg.filters)
                }
            }
            context.packetHandled = true
        }
    }
}
