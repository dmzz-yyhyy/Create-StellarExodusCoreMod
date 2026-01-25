package io.nightfis.createstellarexoduscore

import com.mojang.logging.LogUtils
import io.nightfis.createstellarexoduscore.registry.ModBlockEntities
import io.nightfis.createstellarexoduscore.registry.ModBlocks
import io.nightfis.createstellarexoduscore.registry.ModItems
import io.nightfis.createstellarexoduscore.registry.ModMenus
import io.nightfis.createstellarexoduscore.registry.KotlinRegistrate
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import org.slf4j.Logger

@Suppress("unused")
@Mod(StellarExodusCore.MOD_ID)
class StellarExodusCore(
    context: FMLJavaModLoadingContext
) {
    init {
        MinecraftForge.EVENT_BUS.register(this)

        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC)

        ModBlocks.register()
        ModBlockEntities.register()
        ModMenus.register()
        ModItems.register()
    }

    companion object {
        const val MOD_ID = "create_stellar_exodus_core"
        private val LOGGER: Logger = LogUtils.getLogger()
        @JvmField
        val REGISTRATE: KotlinRegistrate = KotlinRegistrate.create(MOD_ID)

        fun of(path: String): ResourceLocation = ResourceLocation.tryBuild(MOD_ID, path)!!
    }
}
