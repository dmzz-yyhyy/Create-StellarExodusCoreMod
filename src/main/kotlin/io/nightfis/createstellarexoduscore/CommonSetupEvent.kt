package io.nightfis.createstellarexoduscore

import io.nightfis.createstellarexoduscore.filter.TargetFilter
import io.nightfis.createstellarexoduscore.network.ModNetwork
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent

@Mod.EventBusSubscriber(modid = StellarExodusCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object CommonSetupEvent {

    @SubscribeEvent
    @JvmStatic
    fun commonSetup(event: FMLCommonSetupEvent) {
        TargetFilter.init()
        ModNetwork.init()
    }
}