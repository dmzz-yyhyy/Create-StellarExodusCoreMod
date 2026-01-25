package io.nightfis.createstellarexoduscore.datagen

import io.nightfis.createstellarexoduscore.StellarExodusCore
import net.minecraftforge.common.data.ExistingFileHelper
import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Mod.EventBusSubscriber(modid = StellarExodusCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object ModDataGen {
    lateinit var existingFileHelper: ExistingFileHelper

    @SubscribeEvent
    @JvmStatic
    fun gatherData(event: GatherDataEvent) {
        existingFileHelper = event.existingFileHelper
        val generator = event.generator
        val output = generator.packOutput
        if (event.includeClient()) {
            generator.addProvider(true, ModLangProvider(output, "en_us"))
        }
    }
}
