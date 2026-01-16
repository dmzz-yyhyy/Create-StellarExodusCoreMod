package io.nightfis.createstellarexoduscore.client.gui.component

import com.mojang.blaze3d.systems.RenderSystem
import io.nightfis.createstellarexoduscore.StellarExodusCore
import io.nightfis.createstellarexoduscore.client.gui.screen.AutoAimTurretScreen
import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.ComponentUtils
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import java.util.regex.Pattern

class AutoAimTurretButton(
    x: Int,
    y: Int,
    private val index: Int,
    private val variant: Int,
    onPress: OnPress,
    private val parent: AutoAimTurretScreen,
    textureVariant: String
) : Button(x, y, 10, 10, Component.literal(""), onPress, { parent.getFilterTitle(index, variant).copy() }) {

    private val texture: ResourceLocation =
        StellarExodusCore.of("textures/gui/container/machine/active_silencer_button_%s.png".format(textureVariant))

    init {
        height = 15
        width = 112
    }

    fun getIndex(): Int = index

    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val searchText = parent.getFilterText()
        val id = parent.getFilterToolTipAt(index, variant) ?: return
        drawTexture(guiGraphics, texture, x, y, 0, 0, 15, width, height, 112, 30)
        val message = if (searchText.startsWith("#") || searchText.startsWith("~")) {
            parent.getFilterTitle(index, variant)
        } else {
            highlighted(parent.getFilterTitle(index, variant).string, searchText, ChatFormatting.WHITE)
        }
        setMessage(message)
        renderString(
            guiGraphics,
            Minecraft.getInstance().font,
            16777215 or (Mth.ceil(alpha * 255.0f) shl 24)
        )
        if (isHovered) {
            val filterText = highlighted(id, searchText.replaceFirst("#", ""), ChatFormatting.GRAY)
            guiGraphics.renderTooltip(
                Minecraft.getInstance().font,
                if (filterText.string.isEmpty()) {
                    listOf(message.visualOrderText)
                } else {
                    listOf(message.visualOrderText, filterText.visualOrderText)
                },
                mouseX,
                mouseY
            )
        }
    }

    @Suppress("SameParameterValue")
    private fun drawTexture(
        guiGraphics: GuiGraphics,
        texture: ResourceLocation,
        x: Int,
        y: Int,
        puOffset: Int,
        pvOffset: Int,
        textureDifference: Int,
        width: Int,
        height: Int,
        textureWidth: Int,
        textureHeight: Int
    ) {
        var i = pvOffset
        if (isHovered) {
            i += textureDifference
        }
        RenderSystem.enableDepthTest()
        guiGraphics.blit(
            texture,
            x,
            y,
            puOffset.toFloat(),
            i.toFloat(),
            width,
            height,
            textureWidth,
            textureHeight
        )
    }

    companion object {
        private fun highlighted(original: String, hightlighted: String, originalFormatting: ChatFormatting): Component {
            return try {
                val parts = original.split(Pattern.quote(hightlighted).toRegex(), -1)
                val components = parts.map {
                    Component.literal(it).copy().setStyle(Style.EMPTY.applyFormat(originalFormatting))
                }
                ComponentUtils.formatList(components, Component.literal(hightlighted).withStyle(ChatFormatting.YELLOW))
            } catch (_: Throwable) {
                Component.literal(original)
            }
        }
    }
}
