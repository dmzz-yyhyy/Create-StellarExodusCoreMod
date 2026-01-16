package io.nightfis.createstellarexoduscore.item

import io.nightfis.createstellarexoduscore.block.entity.AutoAimTurretBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

class AutoAimTurretItem(block: Block, properties: Properties) : BlockItem(block, properties) {

    override fun updateCustomBlockEntityTag(
        pos: BlockPos,
        level: Level,
        player: Player?,
        itemStack: ItemStack,
        blockState: BlockState
    ): Boolean {
        val blockEntity = level.getBlockEntity(pos) as? AutoAimTurretBlockEntity ?: return false
        val owner = player ?: return false
        blockEntity.initWhiteList(owner)
        return true
    }
}
