package io.nightfis.createstellarexoduscore.block

import io.nightfis.createstellarexoduscore.block.entity.StellarShuttleControllerBlockEntity
import io.nightfis.createstellarexoduscore.registry.ModBlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraftforge.network.NetworkHooks

class StellarShuttleControllerBlock(properties: Properties) : Block(properties), EntityBlock {

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return ModBlockEntities.STELLAR_SHUTTLE_CONTROLLER.get().create(pos, state)
    }

    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? = null

    @Deprecated("Deprecated in Java")
    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS
        }
        val blockEntity = level.getBlockEntity(pos)
        if (blockEntity is StellarShuttleControllerBlockEntity && player is ServerPlayer) {
            NetworkHooks.openScreen(player, blockEntity, pos)
            return InteractionResult.SUCCESS
        }
        return InteractionResult.PASS
    }
}
