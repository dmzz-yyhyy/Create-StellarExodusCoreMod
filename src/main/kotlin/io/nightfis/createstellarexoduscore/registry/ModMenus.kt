package io.nightfis.createstellarexoduscore.registry

import com.tterrag.registrate.util.entry.MenuEntry
import io.nightfis.createstellarexoduscore.StellarExodusCore
import io.nightfis.createstellarexoduscore.client.gui.screen.AutoAimTurretScreen
import io.nightfis.createstellarexoduscore.client.gui.screen.StellarShuttleControllerScreen
import io.nightfis.createstellarexoduscore.inventory.AutoAimTurretMenu
import io.nightfis.createstellarexoduscore.inventory.StellarShuttleControllerMenu

@Suppress("unused")
object ModMenus {
    @Suppress("DataFlowIssue")
    @JvmField
    val AUTO_AIM_TURRET_MENU: MenuEntry<AutoAimTurretMenu> = StellarExodusCore.REGISTRATE
        .menu(
            "auto_aim_turret",
            AutoAimTurretMenu::create,
            ::AutoAimTurretScreen
        )
        .register()

    @Suppress("DataFlowIssue")
    @JvmField
    val STELLAR_SHUTTLE_CONTROLLER_MENU: MenuEntry<StellarShuttleControllerMenu> = StellarExodusCore.REGISTRATE
        .menu(
            "stellar_shuttle_controller",
            StellarShuttleControllerMenu::create,
            ::StellarShuttleControllerScreen
        )
        .register()

    @JvmStatic
    fun register() {
    }
}
