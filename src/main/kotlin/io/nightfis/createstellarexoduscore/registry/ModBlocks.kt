package io.nightfis.createstellarexoduscore.registry

import com.simibubi.create.AllBlocks
import com.tterrag.registrate.util.entry.BlockEntry
import io.nightfis.createstellarexoduscore.StellarExodusCore
import io.nightfis.createstellarexoduscore.block.AutoAimTurretBlock
import io.nightfis.createstellarexoduscore.block.StellarShuttleControllerBlock
import io.nightfis.createstellarexoduscore.datagen.existingModelFile
import io.nightfis.createstellarexoduscore.item.AutoAimTurretItem
import net.minecraft.tags.BlockTags
import net.minecraft.world.item.CreativeModeTabs

object ModBlocks {

    @JvmField
    val AUTO_AIM_TURRET: BlockEntry<AutoAimTurretBlock> = StellarExodusCore.REGISTRATE
        .block("auto_aim_turret", ::AutoAimTurretBlock)
        .initialProperties(AllBlocks.MECHANICAL_ARM)
        .properties {
            it.noOcclusion()
        }
        .blockstate { ctx, provider ->
            provider.horizontalBlock(
                ctx.get(),
                existingModelFile("block/auto_aim_turret")
            )
        }
        .tag(BlockTags.MINEABLE_WITH_PICKAXE)
        .item(::AutoAimTurretItem)
        .model { context, provider ->
            provider.blockItem(context)
        }
        .tab(CreativeModeTabs.REDSTONE_BLOCKS)
        .build()
        .register()

    @JvmField
    val STELLAR_SHUTTLE_CONTROLLER: BlockEntry<StellarShuttleControllerBlock> = StellarExodusCore.REGISTRATE
        .block("stellar_shuttle_controller", ::StellarShuttleControllerBlock)
        .properties {
            it.strength(2.0f)
        }
        .blockstate { ctx, provider ->
            provider.simpleBlock(ctx.get())
        }
        .tag(BlockTags.MINEABLE_WITH_PICKAXE)
        .item()
        .model { context, provider ->
            provider.blockItem(context)
        }
        .tab(CreativeModeTabs.FUNCTIONAL_BLOCKS)
        .build()
        .register()

    @JvmStatic
    fun register() {}
}
