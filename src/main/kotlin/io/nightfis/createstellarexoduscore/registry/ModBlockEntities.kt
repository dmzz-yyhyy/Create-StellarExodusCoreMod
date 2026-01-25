package io.nightfis.createstellarexoduscore.registry

import com.tterrag.registrate.util.entry.BlockEntityEntry
import io.nightfis.createstellarexoduscore.StellarExodusCore
import io.nightfis.createstellarexoduscore.block.entity.AutoAimTurretBlockEntity
import io.nightfis.createstellarexoduscore.block.entity.StellarShuttleControllerBlockEntity

object ModBlockEntities {

    @JvmField
    val AUTO_AIM_TURRET_ENTITY: BlockEntityEntry<AutoAimTurretBlockEntity> = StellarExodusCore.REGISTRATE
        .blockEntity("auto_aim_turret", ::AutoAimTurretBlockEntity)
        .validBlock(ModBlocks.AUTO_AIM_TURRET)
        .register()

    @JvmField
    val STELLAR_SHUTTLE_CONTROLLER: BlockEntityEntry<StellarShuttleControllerBlockEntity> = StellarExodusCore.REGISTRATE
        .blockEntity("stellar_shuttle_controller", ::StellarShuttleControllerBlockEntity)
        .validBlock(ModBlocks.STELLAR_SHUTTLE_CONTROLLER)
        .register()

    @JvmStatic
    fun register() {}
}
