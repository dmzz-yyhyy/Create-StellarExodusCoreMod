package io.nightfis.createstellarexoduscore.datagen

import io.nightfis.createstellarexoduscore.StellarExodusCore
import net.minecraft.data.PackOutput
import net.minecraftforge.common.data.LanguageProvider

class ModLangProvider(
    output: PackOutput,
    locale: String
) : LanguageProvider(output, StellarExodusCore.MOD_ID, locale) {

    override fun addTranslations() {
        add("block.create_stellar_exodus_core.auto_aim_turret", "Auto Aim Turret")
        add("block.create_stellar_exodus_core.stellar_shuttle_controller", "Stellar Shuttle Controller")
        add("item.create_stellar_exodus_core.entity_proximity_fuze", "Entity Proximity Fuze")
        add("screen.create_stellar_exodus_core.auto_aim_turret.search", "Search")
        add("screen.create_stellar_exodus_core.auto_aim_turret.filter.unknown", "Unknown Filter")
        add("screen.create_stellar_exodus_core.auto_aim_turret.filter.is_player", "Is Player")
        add("screen.create_stellar_exodus_core.auto_aim_turret.filter.is_player_id", "Is Player ID")
        add("screen.create_stellar_exodus_core.auto_aim_turret.filter.is_pet", "Is Pet")
        add("screen.create_stellar_exodus_core.auto_aim_turret.filter.is_on_vehicle", "Is On Vehicle")
        add("screen.create_stellar_exodus_core.auto_aim_turret.filter.is_friendly", "Is Friendly")
        add("screen.create_stellar_exodus_core.auto_aim_turret.filter.is_entity_id", "Is Entity ID")
        add("screen.create_stellar_exodus_core.auto_aim_turret.filter.is_baby_friendly", "Is Baby Friendly")
        add("screen.create_stellar_exodus_core.auto_aim_turret.filter.has_custom_name", "Has Custom Name")
        add("screen.create_stellar_exodus_core.stellar_shuttle_controller.save", "Save Hemisphere")
        add("screen.create_stellar_exodus_core.stellar_shuttle_controller.place", "Place Structure")
    }
}
