package io.nightfis.createstellarexoduscore.structure

import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.nbt.Tag
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.saveddata.SavedData

class ShuttleStructureData : SavedData() {

    data class StructureEntry(
        val relativePos: BlockPos,
        val state: BlockState,
        val blockEntityTag: CompoundTag?
    )

    private val structureEntries = mutableListOf<StructureEntry>()

    val entries: List<StructureEntry>
        get() = structureEntries

    fun setStructure(entries: Collection<StructureEntry>) {
        structureEntries.clear()
        structureEntries.addAll(entries)
        setDirty()
    }

    override fun save(tag: CompoundTag): CompoundTag {
        val list = ListTag()
        for (entry in structureEntries) {
            val entryTag = CompoundTag()
            entryTag.putInt("x", entry.relativePos.x)
            entryTag.putInt("y", entry.relativePos.y)
            entryTag.putInt("z", entry.relativePos.z)
            entryTag.put("state", NbtUtils.writeBlockState(entry.state))
            val blockEntityTag = entry.blockEntityTag
            if (blockEntityTag != null) {
                entryTag.put("blockEntity", blockEntityTag)
            }
            list.add(entryTag)
        }
        tag.put("entries", list)
        return tag
    }

    companion object {
        private const val DATA_NAME = "stellar_shuttle_structure"

        fun get(server: MinecraftServer): ShuttleStructureData {
            val storage = server.overworld().dataStorage
            return storage.computeIfAbsent(::load, ::create, DATA_NAME)
        }

        private fun create(): ShuttleStructureData {
            return ShuttleStructureData()
        }

        private fun load(tag: CompoundTag): ShuttleStructureData {
            val data = ShuttleStructureData()
            val list = tag.getList("entries", Tag.TAG_COMPOUND.toInt())
            for (element in list) {
                if (element !is CompoundTag) {
                    continue
                }
                val relativePos = BlockPos(
                    element.getInt("x"),
                    element.getInt("y"),
                    element.getInt("z")
                )
                val state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), element.getCompound("state"))
                val blockEntityTag = if (element.contains("blockEntity", Tag.TAG_COMPOUND.toInt())) {
                    element.getCompound("blockEntity")
                } else {
                    null
                }
                data.structureEntries.add(StructureEntry(relativePos, state, blockEntityTag))
            }
            return data
        }
    }
}
