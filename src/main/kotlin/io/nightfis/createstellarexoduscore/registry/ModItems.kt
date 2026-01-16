package io.nightfis.createstellarexoduscore.registry

import com.tterrag.registrate.util.entry.ItemEntry
import io.nightfis.createstellarexoduscore.StellarExodusCore
import io.nightfis.createstellarexoduscore.compat.createbigcannons.EntityProximityFuzeItem
import net.minecraft.world.item.CreativeModeTabs

@Suppress("unused")
object ModItems {

    @JvmField
    val ENTITY_PROXIMITY_FUZE: ItemEntry<EntityProximityFuzeItem> = StellarExodusCore.REGISTRATE
        .item("entity_proximity_fuze", ::EntityProximityFuzeItem)
        .properties {
            it.stacksTo(16)
        }
        .tab(CreativeModeTabs.COMBAT)
        .register()

    @JvmStatic
    fun register() {}
}
