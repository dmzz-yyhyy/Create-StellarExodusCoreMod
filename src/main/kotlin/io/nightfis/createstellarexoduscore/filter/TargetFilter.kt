package io.nightfis.createstellarexoduscore.filter

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity

abstract class TargetFilter {

    abstract fun getId(): String

    abstract fun match(entity: LivingEntity, arg: String): Boolean

    open fun needArg(): Boolean = false

    abstract fun title(): Component

    open fun tooltip(arg: String): String = ""

    companion object {
        private val EMPTY_FILTER: TargetFilter = object : TargetFilter() {
            override fun getId(): String = ""

            override fun match(entity: LivingEntity, arg: String): Boolean = false

            override fun title(): Component {
                return Component.translatable("screen.create_stellar_exodus_core.auto_aim_turret.filter.unknown")
            }
        }

        private val FILTER_MAP = HashMap<String, TargetFilter>()

        @JvmStatic
        fun register(filter: TargetFilter) {
            FILTER_MAP[filter.getId()] = filter
        }

        @JvmStatic
        fun getFilter(id: String): TargetFilter {
            return FILTER_MAP[id] ?: EMPTY_FILTER
        }

        @JvmStatic
        fun all(): Collection<TargetFilter> = FILTER_MAP.values

        @JvmStatic
        fun init() {
            FILTER_MAP.clear()
            register(IsPlayerFilter())
            register(IsPlayerIdFilter())
            register(IsPetFilter())
            register(IsOnVehicleFilter())
            register(IsFriendlyFilter())
            register(IsEntityIdFilter())
            register(IsBabyFriendlyFilter())
            register(HasCustomNameFilter())
        }
    }
}
