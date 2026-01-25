package io.nightfis.createstellarexoduscore.network

import io.nightfis.createstellarexoduscore.block.entity.StellarShuttleControllerBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class SaveShuttleStructurePacket(val pos: BlockPos) {

    companion object {
        fun encode(msg: SaveShuttleStructurePacket, buf: FriendlyByteBuf) {
            buf.writeBlockPos(msg.pos)
        }

        fun decode(buf: FriendlyByteBuf): SaveShuttleStructurePacket {
            return SaveShuttleStructurePacket(buf.readBlockPos())
        }

        fun handle(msg: SaveShuttleStructurePacket, ctx: Supplier<NetworkEvent.Context>) {
            val context = ctx.get()
            context.enqueueWork {
                val player = context.sender ?: return@enqueueWork
                val blockEntity = player.level().getBlockEntity(msg.pos)
                if (blockEntity is StellarShuttleControllerBlockEntity) {
                    blockEntity.saveStructure()
                }
            }
            context.packetHandled = true
        }
    }
}
