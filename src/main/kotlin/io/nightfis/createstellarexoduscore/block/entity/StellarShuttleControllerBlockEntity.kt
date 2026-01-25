package io.nightfis.createstellarexoduscore.block.entity

import io.nightfis.createstellarexoduscore.inventory.StellarShuttleControllerMenu
import io.nightfis.createstellarexoduscore.registry.ModMenus
import io.nightfis.createstellarexoduscore.structure.ShuttleStructureData
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB

class StellarShuttleControllerBlockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState
) : BlockEntity(type, pos, state), MenuProvider {

    override fun getDisplayName(): Component {
        return Component.translatable("block.create_stellar_exodus_core.stellar_shuttle_controller")
    }

    override fun createMenu(containerId: Int, inventory: Inventory, player: Player): AbstractContainerMenu? {
        if (level == null || player.isSpectator) {
            return null
        }
        return StellarShuttleControllerMenu(
            ModMenus.STELLAR_SHUTTLE_CONTROLLER_MENU.get(),
            containerId,
            inventory,
            worldPosition
        )
    }

    override fun getRenderBoundingBox(): AABB {
        val base = worldPosition.below()
        val min = base.offset(-RADIUS, 0, -RADIUS)
        val max = base.offset(RADIUS + 1, RADIUS + 1, RADIUS + 1)
        return AABB(min, max)
    }

    fun saveStructure(): Boolean {
        val serverLevel = level as? ServerLevel ?: return false
        val server = serverLevel.server
        val center = worldPosition.below()
        val entries = mutableListOf<ShuttleStructureData.StructureEntry>()
        for (x in -RADIUS..RADIUS) {
            for (y in 0..RADIUS) {
                for (z in -RADIUS..RADIUS) {
                    val distanceSqr = x * x + y * y + z * z
                    if (distanceSqr > RADIUS_SQR) {
                        continue
                    }
                    val targetPos = center.offset(x, y, z)
                    if (targetPos == worldPosition) {
                        continue
                    }
                    val targetState = serverLevel.getBlockState(targetPos)
                    val blockEntity = serverLevel.getBlockEntity(targetPos)
                    val blockEntityTag: CompoundTag? = blockEntity?.saveWithoutMetadata()
                    entries.add(
                        ShuttleStructureData.StructureEntry(
                            BlockPos(x, y, z),
                            targetState,
                            blockEntityTag
                        )
                    )
                }
            }
        }
        ShuttleStructureData.get(server).setStructure(entries)
        return true
    }

    fun placeStructure(): Boolean {
        val serverLevel = level as? ServerLevel ?: return false
        val server = serverLevel.server
        val data = ShuttleStructureData.get(server)
        if (data.entries.isEmpty()) {
            return false
        }
        val center = worldPosition.below()
        val pendingBlockEntities = mutableListOf<Pair<BlockPos, CompoundTag>>()
        for (entry in data.entries) {
            val targetPos = center.offset(entry.relativePos)
            if (targetPos == worldPosition) {
                continue
            }
            serverLevel.setBlock(targetPos, entry.state, 3)
            val blockEntityTag = entry.blockEntityTag
            if (blockEntityTag != null) {
                pendingBlockEntities.add(targetPos to blockEntityTag)
            }
        }
        for ((pos, tag) in pendingBlockEntities) {
            val blockEntity = serverLevel.getBlockEntity(pos) ?: continue
            blockEntity.load(tag)
            blockEntity.setChanged()
            serverLevel.sendBlockUpdated(pos, blockEntity.blockState, blockEntity.blockState, 3)
        }
        return true
    }

    companion object {
        private const val RADIUS = 16
        private const val RADIUS_SQR = RADIUS * RADIUS
    }
}
