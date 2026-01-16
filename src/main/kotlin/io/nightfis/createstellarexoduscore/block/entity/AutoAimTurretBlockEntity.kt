package io.nightfis.createstellarexoduscore.block.entity

import com.simibubi.create.content.kinetics.base.KineticBlockEntity
import io.nightfis.createstellarexoduscore.block.AutoAimTurretBlock
import io.nightfis.createstellarexoduscore.filter.*
import io.nightfis.createstellarexoduscore.inventory.AutoAimTurretMenu
import io.nightfis.createstellarexoduscore.registry.ModMenus
import it.unimi.dsi.fastutil.Pair
import net.minecraft.commands.arguments.EntityAnchorArgument
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import rbasamoyai.createbigcannons.cannon_control.cannon_mount.CannonMountBlock
import rbasamoyai.createbigcannons.cannon_control.cannon_mount.CannonMountBlockEntity
import rbasamoyai.createbigcannons.cannon_control.contraption.AbstractMountedCannonContraption
import rbasamoyai.createbigcannons.cannons.autocannon.breech.AbstractAutocannonBreechBlockEntity
import kotlin.math.sqrt

class AutoAimTurretBlockEntity(type: BlockEntityType<*>, pos: BlockPos, state: BlockState) :
    KineticBlockEntity(type, pos, state), MenuProvider {

    private val whiteList = mutableListOf<Pair<TargetFilter, String>>()

    fun tick(level: Level, pos: BlockPos, state: BlockState) {
        tick()
        if (isOverStressed) return
        val cannonMountBlockPos = pos.relative(state.getValue(AutoAimTurretBlock.HORIZONTAL_DIRECTION))
        val blockEntity = level.getBlockEntity(cannonMountBlockPos)
        val blockState = level.getBlockState(cannonMountBlockPos)
        if (blockEntity !is CannonMountBlockEntity) return
        val verticalDirection = blockState.getValue(CannonMountBlock.VERTICAL_DIRECTION)
        var cannonMountPos = cannonMountBlockPos.center
        cannonMountPos = if (verticalDirection == Direction.DOWN) {
            cannonMountPos.add(0.0, 2.0, 0.0)
        } else {
            cannonMountPos.add(0.0, -2.0, 0.0)
        }
        val range = 64.0
        val area = AABB(worldPosition).inflate(range)
        var minDistanceSqr = Double.MAX_VALUE
        var target: LivingEntity? = null
        val rangeSqr = range * range
        val finalCannonMountPos = cannonMountPos
        level.getEntitiesOfClass(LivingEntity::class.java, area) { entity ->
            if (!entity.isAlive || entity.isSpectator) return@getEntitiesOfClass false
            if (isFiltered(entity)) return@getEntitiesOfClass false
            val distanceSqr = entity.position().distanceToSqr(finalCannonMountPos)
            if (distanceSqr > rangeSqr || minDistanceSqr <= distanceSqr) return@getEntitiesOfClass false
            for (filter in whiteList) {
                if (filter.left().match(entity, filter.right())) return@getEntitiesOfClass false
            }
            minDistanceSqr = distanceSqr
            target = entity
            true
        }
        if (target == null) {
            val contraption = blockEntity.contraption ?: return
            val cannonContraption = contraption.contraption as? AbstractMountedCannonContraption ?: return
            if (!cannonContraption.canBeFiredOnController(contraption.controller)) return
            val present = cannonContraption.presentBlockEntities[cannonContraption.startPos]
            if (present is AbstractAutocannonBreechBlockEntity) {
                present.fireRate = 0
            }
            return
        }
        val targetEntity = target
        val targetVec = EntityAnchorArgument.Anchor.EYES.apply(targetEntity)
            .add(EntityAnchorArgument.Anchor.FEET.apply(targetEntity))
            .scale(0.5)
        val d0 = cannonMountPos.x - targetVec.x
        val d1 = cannonMountPos.y - targetVec.y
        val d2 = cannonMountPos.z - targetVec.z
        val d3 = sqrt(d0 * d0 + d2 * d2)
        val xRot = Mth.wrapDegrees((-(Mth.atan2(d1, d3) * (180.0 / Math.PI))).toFloat())
        val yRot = Mth.wrapDegrees((Mth.atan2(d2, d0) * (180.0 / Math.PI) + 90.0).toFloat())
        blockEntity.setPitch(xRot)
        blockEntity.setYaw(yRot)
        if (level !is ServerLevel) return
        val contraption = blockEntity.contraption ?: return
        if (!blockEntity.isRunning) return
        val cannonContraption = contraption.contraption as? AbstractMountedCannonContraption ?: return
        if (!cannonContraption.canBeFiredOnController(contraption.controller)) return
        val present = cannonContraption.presentBlockEntities[cannonContraption.startPos]
        if (present is AbstractAutocannonBreechBlockEntity) {
            present.fireRate = 15
        }
        cannonContraption.fireShot(level, contraption)
    }

    override fun calculateStressApplied(): Float = BASE_STRESS_IMPACT

    private fun isFiltered(entity: LivingEntity): Boolean {
        for (filter in whiteList) {
            if (filter.left().match(entity, filter.right())) {
                return true
            }
        }
        return false
    }

    override fun write(tag: CompoundTag, clientPacket: Boolean) {
        super.write(tag, clientPacket)
        val filters = ListTag()
        for (entry in whiteList) {
            val entryTag = CompoundTag()
            entryTag.putString("id", entry.left().getId())
            entryTag.putString("arg", entry.right())
            filters.add(entryTag)
        }
        tag.put("Filters", filters)
    }

    override fun read(tag: CompoundTag, clientPacket: Boolean) {
        super.read(tag, clientPacket)
        whiteList.clear()
        for (entry in tag.getList("Filters", Tag.TAG_COMPOUND.toInt())) {
            if (entry is CompoundTag) {
                whiteList.add(Pair.of(TargetFilter.getFilter(entry.getString("id")), entry.getString("arg")))
            }
        }
    }

    fun addFilter(id: String, arg: String) {
        whiteList.add(Pair.of(TargetFilter.getFilter(id), arg))
        setChanged()
    }

    fun removeFilter(id: String, arg: String) {
        whiteList.removeIf { pair -> pair.left().getId() == id && pair.right() == arg }
        setChanged()
    }

    fun handleSync(filters: List<Pair<TargetFilter, String>>) {
        whiteList.clear()
        whiteList.addAll(filters)
        setChanged()
    }

    fun getWhiteList(): List<Pair<TargetFilter, String>> = ArrayList(whiteList)

    override fun getDisplayName(): Component {
        return Component.translatable("block.create_stellar_exodus_core.auto_aim_turret")
    }

    override fun createMenu(containerId: Int, inventory: Inventory, player: Player): AbstractContainerMenu? {
        if (level == null || player.isSpectator) {
            return null
        }
        return AutoAimTurretMenu(ModMenus.AUTO_AIM_TURRET_MENU.get(), containerId, inventory, this)
    }

    fun initWhiteList(player: Player) {
        whiteList.add(Pair.of(IsPlayerIdFilter(), player.name.string))
        whiteList.add(Pair.of(IsPetFilter(), ""))
        whiteList.add(Pair.of(HasCustomNameFilter(), ""))
        whiteList.add(Pair.of(IsEntityIdFilter(), Component.translatable("entity.minecraft.villager").string))
        whiteList.add(Pair.of(IsEntityIdFilter(), Component.translatable("entity.minecraft.wandering_trader").string))
        whiteList.add(Pair.of(IsFriendlyFilter(), ""))
        whiteList.add(Pair.of(IsOnVehicleFilter(), ""))
    }

    companion object {
        private const val BASE_STRESS_IMPACT = 32f
    }
}
