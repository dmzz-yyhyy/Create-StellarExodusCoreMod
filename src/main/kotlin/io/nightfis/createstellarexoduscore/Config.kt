package io.nightfis.createstellarexoduscore

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.config.ModConfigEvent
import net.minecraftforge.registries.ForgeRegistries

@Mod.EventBusSubscriber(modid = StellarExodusCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object Config {
    private val BUILDER = ForgeConfigSpec.Builder()

    private val LOG_DIRT_BLOCK = BUILDER
        .comment("Whether to log the dirt block on common setup")
        .define("logDirtBlock", true)

    private val MAGIC_NUMBER = BUILDER
        .comment("A magic number")
        .defineInRange("magicNumber", 42, 0, Int.MAX_VALUE)

    private val MAGIC_NUMBER_INTRODUCTION = BUILDER
        .comment("What you want the introduction message to be for the magic number")
        .define("magicNumberIntroduction", "The magic number is... ")

    private val ITEM_STRINGS = BUILDER
        .comment("A list of items to log on common setup.")
        .defineListAllowEmpty("items", listOf("minecraft:iron_ingot"), ::validateItemName)

    val SPEC: ForgeConfigSpec = BUILDER.build()
    var logDirtBlock = false
    var magicNumber = 0
    var magicNumberIntroduction = ""
    var items: Set<Item> = emptySet()

    private fun validateItemName(obj: Any?): Boolean {
        return obj is String && ForgeRegistries.ITEMS.containsKey(ResourceLocation.parse(obj))
    }

    @SubscribeEvent
    @JvmStatic
    fun onLoad(event: ModConfigEvent) {
        logDirtBlock = LOG_DIRT_BLOCK.get()
        magicNumber = MAGIC_NUMBER.get()
        magicNumberIntroduction = MAGIC_NUMBER_INTRODUCTION.get()

        items = ITEM_STRINGS.get()
            .map { it.toString() }
            .mapNotNull { ForgeRegistries.ITEMS.getValue(ResourceLocation.parse(it)) }
            .toSet()
    }
}
