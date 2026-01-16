package io.nightfis.createstellarexoduscore.filter

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

class IsPlayerFilter : TargetFilter() {

    override fun getId(): String = ID

    override fun match(entity: LivingEntity, arg: String): Boolean = entity is Player

    override fun title(): Component {
        return Component.translatable("screen.create_stellar_exodus_core.auto_aim_turret.filter.is_player")
    }

    companion object {
        private const val ID = "IsPlayerFilter"
    }
}
