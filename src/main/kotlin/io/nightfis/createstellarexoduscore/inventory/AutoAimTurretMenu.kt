package io.nightfis.createstellarexoduscore.inventory

import io.nightfis.createstellarexoduscore.block.entity.AutoAimTurretBlockEntity
import io.nightfis.createstellarexoduscore.filter.TargetFilter
import io.nightfis.createstellarexoduscore.registry.ModBlocks
import it.unimi.dsi.fastutil.Pair
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity

class AutoAimTurretMenu(
    menuType: MenuType<*>,
    containerId: Int,
    inventory: Inventory,
    machine: BlockEntity
) : AbstractContainerMenu(menuType, containerId) {
    companion object {
        fun create(
            menuType: MenuType<*>,
            containerId: Int,
            inventory: Inventory,
            data: FriendlyByteBuf?
        ) = AutoAimTurretMenu(menuType, containerId, inventory, inventory.player.level().getBlockEntity(data!!.readBlockPos())!!)
    }

    private val blockEntity = machine as AutoAimTurretBlockEntity
    private val level: Level = inventory.player.level()

    override fun quickMoveStack(player: Player, index: Int): ItemStack = ItemStack.EMPTY

    override fun stillValid(player: Player): Boolean {
        return stillValid(
            ContainerLevelAccess.create(level, blockEntity.blockPos),
            player,
            ModBlocks.AUTO_AIM_TURRET.get()
        )
    }

    fun addFilter(id: String, arg: String) {
        blockEntity.addFilter(id, arg)
    }

    fun removeFilter(id: String, arg: String) {
        blockEntity.removeFilter(id, arg)
    }

    fun handleSync(filters: List<Pair<TargetFilter, String>>) {
        blockEntity.handleSync(filters)
    }
}
