package io.nightfis.createstellarexoduscore.filter

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity

class IsEntityIdFilter : TargetFilter() {

    override fun getId(): String = ID

    override fun match(entity: LivingEntity, arg: String): Boolean {
        return entity.type.descriptionId == arg
    }

    override fun needArg(): Boolean = true

    override fun title(): Component {
        return Component.translatable("screen.create_stellar_exodus_core.auto_aim_turret.filter.is_entity_id")
    }

    override fun tooltip(arg: String): String {
        return Component.translatable(arg).string
    }

    companion object {
        private const val ID = "IsEntityIdFilter"
    }
}
