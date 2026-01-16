package io.nightfis.createstellarexoduscore.registry

import com.simibubi.create.AllBlocks
import com.simibubi.create.Create
import com.tterrag.registrate.util.entry.BlockEntry
import io.nightfis.createstellarexoduscore.StellarExodusCore
import io.nightfis.createstellarexoduscore.block.AutoAimTurretBlock
import io.nightfis.createstellarexoduscore.item.AutoAimTurretItem
import net.minecraft.tags.BlockTags
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.level.material.MapColor
import net.minecraftforge.client.model.generators.ModelFile
import net.minecraftforge.common.Tags

object ModBlocks {

    @JvmField
    val AUTO_AIM_TURRET: BlockEntry<AutoAimTurretBlock> = StellarExodusCore.REGISTRATE
        .block("auto_aim_turret", ::AutoAimTurretBlock)
        .initialProperties(AllBlocks.MECHANICAL_ARM)
        .blockstate { ctx, provider ->
            provider.horizontalBlock(
                ctx.get(),
                ModelFile.UncheckedModelFile(StellarExodusCore.of("models/block/auto_aim_turret"))
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

    @JvmStatic
    fun register() {}
}
