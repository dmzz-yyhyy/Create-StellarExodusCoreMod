package io.nightfis.createstellarexoduscore.client.gui.screen

import io.nightfis.createstellarexoduscore.inventory.StellarShuttleControllerMenu
import io.nightfis.createstellarexoduscore.network.ModNetwork
import io.nightfis.createstellarexoduscore.network.PlaceShuttleStructurePacket
import io.nightfis.createstellarexoduscore.network.SaveShuttleStructurePacket
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

class StellarShuttleControllerScreen(
    menu: StellarShuttleControllerMenu,
    playerInventory: Inventory,
    title: Component
) : AbstractContainerScreen<StellarShuttleControllerMenu>(menu, playerInventory, title) {

    init {
        imageWidth = 176
        imageHeight = 88
    }

    override fun init() {
        super.init()
        val saveLabel = Component.translatable("screen.create_stellar_exodus_core.stellar_shuttle_controller.save")
        val placeLabel = Component.translatable("screen.create_stellar_exodus_core.stellar_shuttle_controller.place")
        addRenderableWidget(
            Button.builder(saveLabel) {
                ModNetwork.channel.sendToServer(SaveShuttleStructurePacket(menu.blockPos))
            }.bounds(leftPos + 10, topPos + 30, 74, 20).build()
        )
        addRenderableWidget(
            Button.builder(placeLabel) {
                ModNetwork.channel.sendToServer(PlaceShuttleStructurePacket(menu.blockPos))
            }.bounds(leftPos + 92, topPos + 30, 74, 20).build()
        )
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        val x = leftPos
        val y = topPos
        guiGraphics.fill(x, y, x + imageWidth, y + imageHeight, 0xFF202020.toInt())
        guiGraphics.fill(x + 2, y + 2, x + imageWidth - 2, y + imageHeight - 2, 0xFF3A3A3A.toInt())
    }

    override fun renderLabels(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        guiGraphics.drawString(font, title, titleLabelX, titleLabelY, 0xFFFFFF, false)
    }
}
