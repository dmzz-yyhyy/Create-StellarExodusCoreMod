package io.nightfis.createstellarexoduscore.inventory

import io.nightfis.createstellarexoduscore.registry.ModBlocks
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack

class StellarShuttleControllerMenu(
    menuType: MenuType<*>,
    containerId: Int,
    inventory: Inventory,
    val blockPos: BlockPos
) : AbstractContainerMenu(menuType, containerId) {

    companion object {
        fun create(
            menuType: MenuType<*>,
            containerId: Int,
            inventory: Inventory,
            data: FriendlyByteBuf?
        ) = StellarShuttleControllerMenu(menuType, containerId, inventory, data!!.readBlockPos())
    }

    private val level = inventory.player.level()

    override fun quickMoveStack(player: Player, index: Int): ItemStack = ItemStack.EMPTY

    override fun stillValid(player: Player): Boolean {
        return stillValid(
            ContainerLevelAccess.create(level, blockPos),
            player,
            ModBlocks.STELLAR_SHUTTLE_CONTROLLER.get()
        )
    }
}
