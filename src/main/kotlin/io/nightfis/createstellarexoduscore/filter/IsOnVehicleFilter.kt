package io.nightfis.createstellarexoduscore.filter

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity

class IsOnVehicleFilter : TargetFilter() {

    override fun getId(): String = ID

    override fun match(entity: LivingEntity, arg: String): Boolean {
        return entity.vehicle != null
    }

    override fun title(): Component {
        return Component.translatable("screen.create_stellar_exodus_core.auto_aim_turret.filter.is_on_vehicle")
    }

    companion object {
        private const val ID = "IsOnVehicleFilter"
    }
}
