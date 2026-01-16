package io.nightfis.createstellarexoduscore.filter

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity

class HasCustomNameFilter : TargetFilter() {

    override fun getId(): String = ID

    override fun match(entity: LivingEntity, arg: String): Boolean {
        return entity.customName != null
    }

    override fun title(): Component {
        return Component.translatable("screen.create_stellar_exodus_core.auto_aim_turret.filter.has_custom_name")
    }

    companion object {
        private const val ID = "HasCustomNameFilter"
    }
}
