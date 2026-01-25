package io.nightfis.createstellarexoduscore.registry

import com.tterrag.registrate.AbstractRegistrate
import com.tterrag.registrate.builders.BlockBuilder
import com.tterrag.registrate.builders.BlockEntityBuilder
import com.tterrag.registrate.builders.ItemBuilder
import com.tterrag.registrate.builders.MenuBuilder
import com.tterrag.registrate.util.nullness.NonNullSupplier
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.MenuAccess
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState

class KotlinRegistrate private constructor(modid: String) : AbstractRegistrate<KotlinRegistrate>(modid) {

    companion object {
        @JvmStatic
        fun create(modid: String): KotlinRegistrate {
            val ret = KotlinRegistrate(modid)
            ret.registerEventListeners(ret.getModEventBus())
            return ret
        }
    }

    fun <T : Block> block(
        name: String,
        factory: (BlockBehaviour.Properties) -> T
    ): BlockBuilder<T, KotlinRegistrate> {
        return block(self(), name, factory)
    }

    fun <T : Item> item(
        name: String,
        factory: (Item.Properties) -> T
    ): ItemBuilder<T, KotlinRegistrate> {
        return item(self(), name, factory)
    }

    fun <T : BlockEntity> blockEntity(
        name: String,
        factory: (type: BlockEntityType<T>, pos: BlockPos, state: BlockState) -> T
    ): BlockEntityBuilder<T, KotlinRegistrate> {
        return blockEntity(self(), name, factory)
    }

    fun <T, SC> menu(
        name: String,
        factory: (type: MenuType<*>, windowId: Int, inv: Inventory, data: FriendlyByteBuf?) -> T,
        screenFactory: (menu: T, inv: Inventory, displayName: Component) -> SC
    ): MenuBuilder<T, SC, KotlinRegistrate> where T : AbstractContainerMenu, SC : Screen, SC : MenuAccess<T> {
        val menuFactory = MenuBuilder.ForgeMenuFactory<T> { type, windowId, inv, data ->
            factory(type, windowId, inv, data)
        }
        val screenSupplier: NonNullSupplier<MenuBuilder.ScreenFactory<T, SC>> = NonNullSupplier {
            MenuBuilder.ScreenFactory { menu, inv, title ->
                screenFactory(menu, inv, title)
            }
        }
        return menu(self(), name, menuFactory, screenSupplier)
    }

    fun <T, SC> menu(
        name: String,
        factory: (type: MenuType<*>, windowId: Int, inv: Inventory) -> T,
        screenFactory: (menu: T, inv: Inventory, displayName: Component) -> SC
    ): MenuBuilder<T, SC, KotlinRegistrate> where T : AbstractContainerMenu, SC : Screen, SC : MenuAccess<T> {
        val menuFactory = MenuBuilder.MenuFactory<T> { type, windowId, inv ->
            factory(type, windowId, inv)
        }
        val screenSupplier: NonNullSupplier<MenuBuilder.ScreenFactory<T, SC>> = NonNullSupplier {
            MenuBuilder.ScreenFactory { menu, inv, title ->
                screenFactory(menu, inv, title)
            }
        }
        return menu(self(), name, menuFactory, screenSupplier)
    }
}
