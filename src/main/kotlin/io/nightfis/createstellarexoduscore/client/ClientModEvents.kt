package io.nightfis.createstellarexoduscore.client

import io.nightfis.createstellarexoduscore.StellarExodusCore
import io.nightfis.createstellarexoduscore.registry.ModBlockEntities
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

@Mod.EventBusSubscriber(modid = StellarExodusCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object ClientModEvents {
    @SubscribeEvent
    @JvmStatic
    fun onClientSetup(event: FMLClientSetupEvent) {
        event.enqueueWork {
            BlockEntityRenderers.register(
                ModBlockEntities.AUTO_AIM_TURRET_ENTITY.get(),
                ::AutoAimTurretRenderer
            )
            BlockEntityRenderers.register(
                ModBlockEntities.STELLAR_SHUTTLE_CONTROLLER.get(),
                ::StellarShuttleControllerRenderer
            )
        }
    }
}
