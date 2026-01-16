package io.nightfis.createstellarexoduscore.block

import com.simibubi.create.content.kinetics.base.KineticBlock
import io.nightfis.createstellarexoduscore.block.entity.AutoAimTurretBlockEntity
import io.nightfis.createstellarexoduscore.network.AutoAimTurretFilterSyncPacket
import io.nightfis.createstellarexoduscore.network.ModNetwork
import io.nightfis.createstellarexoduscore.registry.ModBlockEntities
import net.createmod.catnip.data.Iterate
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Direction.Axis
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.phys.BlockHitResult
import net.minecraftforge.network.NetworkHooks
import net.minecraftforge.network.PacketDistributor
import rbasamoyai.createbigcannons.index.CBCBlocks

class AutoAimTurretBlock(properties: Properties) : KineticBlock(properties), EntityBlock {

    init {
        registerDefaultState(defaultBlockState().setValue(HORIZONTAL_DIRECTION, Direction.NORTH))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(HORIZONTAL_DIRECTION)
        super.createBlockStateDefinition(builder)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        val preferredDirection = getPreferredHorizontalAxis(context)
        return if (preferredDirection != null) {
            defaultBlockState().setValue(HORIZONTAL_DIRECTION, preferredDirection)
        } else {
            defaultBlockState().setValue(HORIZONTAL_DIRECTION, context.horizontalDirection)
        }
    }

    override fun hasShaftTowards(world: LevelReader, pos: BlockPos, state: BlockState, face: Direction): Boolean {
        return face == state.getValue(HORIZONTAL_DIRECTION)
    }

    @Deprecated("Deprecated in Java")
    override fun rotate(state: BlockState, rot: Rotation): BlockState {
        return state.setValue(HORIZONTAL_DIRECTION, rot.rotate(state.getValue(HORIZONTAL_DIRECTION)))
    }

    @Deprecated("Deprecated in Java")
    override fun mirror(state: BlockState, mirrorIn: Mirror): BlockState {
        return state
    }

    override fun getRotationAxis(state: BlockState): Axis {
        return state.getValue(HORIZONTAL_DIRECTION).axis
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return ModBlockEntities.AUTO_AIM_TURRET_ENTITY.get().create(pos, state)
    }

    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T>? {
        return if (type == ModBlockEntities.AUTO_AIM_TURRET_ENTITY.get()) {
            BlockEntityTicker { lvl, pos, st, be ->
                (be as AutoAimTurretBlockEntity).tick(lvl, pos, st)
            }
        } else {
            null
        }
    }

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
        if (blockEntity is AutoAimTurretBlockEntity && player is ServerPlayer) {
            NetworkHooks.openScreen(player, blockEntity, pos)
            ModNetwork.channel.send(
                PacketDistributor.PLAYER.with { player },
                AutoAimTurretFilterSyncPacket(blockEntity.getWhiteList())
            )
            return InteractionResult.SUCCESS
        }
        return InteractionResult.PASS
    }

    companion object {
        val HORIZONTAL_DIRECTION: DirectionProperty = BlockStateProperties.HORIZONTAL_FACING

        fun getPreferredHorizontalAxis(context: BlockPlaceContext): Direction? {
            for (side in Iterate.horizontalDirections) {
                val blockState = context.level.getBlockState(context.clickedPos.relative(side))
                if (blockState.`is`(CBCBlocks.CANNON_MOUNT.get())) return side
            }
            return null
        }
    }
}
