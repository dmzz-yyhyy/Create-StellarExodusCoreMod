package io.nightfis.createstellarexoduscore.filter

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

class IsPlayerIdFilter : TargetFilter() {

    override fun getId(): String = ID

    override fun match(entity: LivingEntity, arg: String): Boolean {
        return entity is Player && entity.name.string == arg
    }

    override fun needArg(): Boolean = true

    override fun title(): Component {
        return Component.translatable("screen.create_stellar_exodus_core.auto_aim_turret.filter.is_player_id")
    }

    override fun tooltip(arg: String): String = arg

    companion object {
        private const val ID = "IsPlayerIdFilter"
    }
}
