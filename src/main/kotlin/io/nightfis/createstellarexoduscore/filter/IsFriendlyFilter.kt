package io.nightfis.createstellarexoduscore.filter

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity

class IsFriendlyFilter : TargetFilter() {

    override fun getId(): String = ID

    override fun match(entity: LivingEntity, arg: String): Boolean {
        return entity.type.category.isFriendly
    }

    override fun title(): Component {
        return Component.translatable("screen.create_stellar_exodus_core.auto_aim_turret.filter.is_friendly")
    }

    companion object {
        private const val ID = "IsFriendlyFilter"
    }
}
