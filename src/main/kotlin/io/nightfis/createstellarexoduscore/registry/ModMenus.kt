package io.nightfis.createstellarexoduscore.registry

import com.tterrag.registrate.util.entry.MenuEntry
import io.nightfis.createstellarexoduscore.StellarExodusCore
import io.nightfis.createstellarexoduscore.client.gui.screen.AutoAimTurretScreen
import io.nightfis.createstellarexoduscore.inventory.AutoAimTurretMenu
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.MenuType

object ModMenus {

    @Suppress("DataFlowIssue")
    @JvmField
    val AUTO_AIM_TURRET_MENU: MenuEntry<AutoAimTurretMenu> = StellarExodusCore.REGISTRATE
        .menu(
            "auto_aim_turret",
            { type: MenuType<AutoAimTurretMenu>, windowId: Int, inv: Inventory, data: FriendlyByteBuf? ->
                AutoAimTurretMenu.create(type, windowId, inv, data)
            },
            { menu: AutoAimTurretMenu, inv: Inventory, displayName: Component ->

            }
        )

    @JvmStatic
    fun register() {
    }
}
