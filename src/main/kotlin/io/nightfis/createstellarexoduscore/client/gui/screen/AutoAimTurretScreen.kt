package io.nightfis.createstellarexoduscore.client.gui.screen

import io.nightfis.createstellarexoduscore.StellarExodusCore
import io.nightfis.createstellarexoduscore.client.gui.component.AutoAimTurretButton
import io.nightfis.createstellarexoduscore.filter.TargetFilter
import io.nightfis.createstellarexoduscore.inventory.AutoAimTurretMenu
import io.nightfis.createstellarexoduscore.network.AddAutoAimTurretFilterPacket
import io.nightfis.createstellarexoduscore.network.ModNetwork
import io.nightfis.createstellarexoduscore.network.RemoveAutoAimTurretFilterPacket
import it.unimi.dsi.fastutil.Pair
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.entity.player.Inventory
import net.minecraftforge.registries.ForgeRegistries
import java.util.regex.Pattern

@Suppress("MismatchedReadAndWriteOfArray", "FieldCanBeLocal")
class AutoAimTurretScreen(
    menu: AutoAimTurretMenu,
    playerInventory: Inventory,
    title: Component
) : AbstractContainerScreen<AutoAimTurretMenu>(menu, playerInventory, title) {

    private val turretMenu = menu
    private val allFilterButtons = arrayOfNulls<AutoAimTurretButton>(8)
    private val mutedSoundButtons = arrayOfNulls<AutoAimTurretButton>(8)
    private lateinit var editBox: EditBox
    private var leftScrollOff = 0
    private var rightScrollOff = 0

    private var filterText = ""

    private var isDraggingLeft = false
    private var isDraggingRight = false
    private val allFilter = mutableListOf<Pair<TargetFilter, String>>()
    private val filteredFilters = mutableListOf<Pair<TargetFilter, String>>()
    private val whiteFilters = mutableListOf<Pair<TargetFilter, String>>()

    fun getFilterText(): String = filterText

    private fun onSearchTextChange(text: String) {
        leftScrollOff = 0
        filteredFilters.clear()
        if (text.isEmpty()) {
            filterText = ""
            filteredFilters.addAll(allFilter)
            filteredFilters.removeAll { filter ->
                whiteFilters.any {
                    it.left().getId() == filter.left().getId() && it.right() == filter.right()
                }
            }
            return
        } else {
            filterText = text
        }

        if (text.startsWith("#")) {
            val search = text.replaceFirst("#", "")
            allFilter
                .filter { it.right().contains(search) }
                .filter { filter ->
                    whiteFilters.none {
                        it.left().getId() == filter.left().getId() && it.right() == filter.right()
                    }
                }
                .forEach { filteredFilters.add(it) }
        } else {
            if (text.startsWith("~")) {
                try {
                    val search = Pattern.compile(text.replaceFirst("~", ""))
                    allFilter
                        .filter { search.matcher(it.left().getId()).matches() }
                        .forEach { filteredFilters.add(it) }
                } catch (_: Exception) {
                    // intentionally empty
                }
            }
            allFilter
                .filter { it.left().title().string.contains(filterText) }
                .filter { filter ->
                    whiteFilters.none {
                        it.left().getId() == filter.left().getId() && it.right() == filter.right()
                    }
                }
                .forEach { filteredFilters.add(it) }
        }
    }

    override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
        val mc = minecraft ?: return super.keyPressed(keyCode, scanCode, modifiers)
        return if (mc.options.keyInventory.matches(keyCode, scanCode)) {
            focused != null && focused!!.keyPressed(keyCode, scanCode, modifiers)
        } else {
            super.keyPressed(keyCode, scanCode, modifiers)
        }
    }

    private fun refreshFilterList() {
        onSearchTextChange(filterText)
    }

    private fun onAllFilterButtonClick(selectedIndex: Int) {
        val actualIndex = selectedIndex + leftScrollOff
        if (filteredFilters.isEmpty() || actualIndex >= filteredFilters.size) {
            return
        }
        val id = filteredFilters[actualIndex].left().getId()
        val arg = filteredFilters[actualIndex].right()
        addWhiteFilter(id, arg)
        ModNetwork.channel.sendToServer(AddAutoAimTurretFilterPacket(id, arg))
        refreshFilterList()
    }

    private fun onWhiteListFilterButtonClick(selectedIndex: Int) {
        val actualIndex = selectedIndex + rightScrollOff
        if (whiteFilters.isEmpty() || actualIndex >= whiteFilters.size) {
            return
        }
        val id = whiteFilters[actualIndex].left().getId()
        val arg = whiteFilters[actualIndex].right()
        removeWhiteFilter(id, arg)
        ModNetwork.channel.sendToServer(RemoveAutoAimTurretFilterPacket(id, arg))
        refreshFilterList()
    }

    private fun addWhiteFilter(id: String, arg: String) {
        turretMenu.addFilter(id, arg)
        whiteFilters.add(Pair.of(TargetFilter.getFilter(id), arg))
    }

    private fun removeWhiteFilter(id: String, arg: String) {
        turretMenu.removeFilter(id, arg)
        whiteFilters.removeIf { it.left().getId() == id && it.right() == arg }
    }

    fun getFilterTitle(index: Int, variant: Int): Component {
        var actualIndex = index
        return if (variant == FILTER_FILTERED) {
            actualIndex += leftScrollOff
            if (filteredFilters.isEmpty() || actualIndex >= filteredFilters.size) {
                Component.empty()
            } else {
                filteredFilters[actualIndex].left().title()
            }
        } else {
            actualIndex += rightScrollOff
            if (whiteFilters.isEmpty() || actualIndex >= whiteFilters.size) {
                Component.empty()
            } else {
                whiteFilters[actualIndex].left().title()
            }
        }
    }

    fun getFilterToolTipAt(index: Int, variant: Int): String? {
        var actualIndex = index
        return if (variant == FILTER_FILTERED) {
            actualIndex += leftScrollOff
            if (filteredFilters.isEmpty() || actualIndex >= filteredFilters.size) {
                null
            } else {
                val filter = filteredFilters[actualIndex]
                filter.left().tooltip(filter.right())
            }
        } else {
            actualIndex += rightScrollOff
            if (whiteFilters.isEmpty() || actualIndex >= whiteFilters.size) {
                null
            } else {
                val filter = whiteFilters[actualIndex]
                filter.left().tooltip(filter.right())
            }
        }
    }

    init {
        imageWidth = 256
        imageHeight = 166
    }

    override fun init() {
        super.init()

        var buttonTop = topPos + 35
        for (l in 0 until 8) {
            val button = AutoAimTurretButton(
                leftPos + START_LEFT_X,
                buttonTop,
                l,
                FILTER_FILTERED,
                { buttonWidget ->
                    if (buttonWidget is AutoAimTurretButton) {
                        onAllFilterButtonClick(buttonWidget.getIndex())
                    }
                },
                this,
                "add"
            )
            button.width = 112
            allFilterButtons[l] = addRenderableWidget(button)
            buttonTop += 15
        }

        buttonTop = topPos + 35
        for (l in 0 until 8) {
            val button = AutoAimTurretButton(
                leftPos + START_RIGHT_X,
                buttonTop,
                l,
                SOUND_MUTED,
                { buttonWidget ->
                    if (buttonWidget is AutoAimTurretButton) {
                        onWhiteListFilterButtonClick(buttonWidget.getIndex())
                    }
                },
                this,
                "remove"
            )
            mutedSoundButtons[l] = addRenderableWidget(button)
            buttonTop += 15
        }

        val mc = minecraft ?: return
        editBox = EditBox(
            mc.font,
            leftPos + 78,
            topPos + 19,
            100,
            12,
            Component.translatable("screen.create_stellar_exodus_core.auto_aim_turret.search")
        )
        editBox.setResponder(this::onSearchTextChange)
        addRenderableWidget(editBox)

        allFilter.addAll(
            TargetFilter.all()
                .filter { !it.needArg() }
                .map { Pair.of(it, "") }
        )
        val player = Minecraft.getInstance().player
        if (player != null) {
            allFilter.addAll(
                player.connection.onlinePlayers
                    .map { Pair.of(TargetFilter.getFilter("IsPlayerIdFilter"), it.profile.name) }
            )
        }

        allFilter.addAll(
            ForgeRegistries.ENTITY_TYPES.map {
                Pair.of(TargetFilter.getFilter("IsEntityIdFilter"), it.descriptionId)
            }
        )
        filteredFilters.addAll(allFilter)
    }

    private fun mouseInLeft(mouseX: Double, mouseY: Double, leftPos: Int, topPos: Int): Boolean {
        return mouseX >= leftPos + START_LEFT_X &&
            mouseX <= leftPos + SCROLL_BAR_START_LEFT_X + SCROLL_BAR_WIDTH &&
            mouseY >= topPos + SCROLL_BAR_TOP_POS_Y &&
            mouseY <= topPos + SCROLL_BAR_TOP_POS_Y + SCROLL_BAR_HEIGHT
    }

    private fun mouseInRight(mouseX: Double, mouseY: Double, leftPos: Int, topPos: Int): Boolean {
        return mouseX >= leftPos + START_RIGHT_X &&
            mouseX <= leftPos + SCROLL_BAR_START_RIGHT_X + SCROLL_BAR_WIDTH &&
            mouseY >= topPos + SCROLL_BAR_TOP_POS_Y &&
            mouseY <= topPos + SCROLL_BAR_TOP_POS_Y + SCROLL_BAR_HEIGHT
    }

    private fun mouseInLeftSlider(mouseX: Double, mouseY: Double, leftPos: Int, topPos: Int): Boolean {
        return mouseX >= leftPos + SCROLL_BAR_START_LEFT_X &&
            mouseX <= leftPos + SCROLL_BAR_START_LEFT_X + SCROLL_BAR_WIDTH &&
            mouseY >= topPos + SCROLL_BAR_TOP_POS_Y &&
            mouseY <= topPos + SCROLL_BAR_TOP_POS_Y + SCROLL_BAR_HEIGHT
    }

    private fun mouseInRightSlider(mouseX: Double, mouseY: Double, leftPos: Int, topPos: Int): Boolean {
        return mouseX >= leftPos + SCROLL_BAR_START_RIGHT_X &&
            mouseX <= leftPos + SCROLL_BAR_START_RIGHT_X + SCROLL_BAR_WIDTH &&
            mouseY >= topPos + SCROLL_BAR_TOP_POS_Y &&
            mouseY <= topPos + SCROLL_BAR_TOP_POS_Y + SCROLL_BAR_HEIGHT
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, delta: Double): Boolean {
        val leftPos = (width - imageWidth) / 2
        val topPos = (height - imageHeight) / 2
        if (mouseInLeft(mouseX, mouseY, leftPos, topPos)) {
            if (filteredFilters.size > 8) {
                leftScrollOff = Mth.clamp(
                    (leftScrollOff - delta).toInt(),
                    0,
                    filteredFilters.size - 7
                )
            }
        } else {
            if (mouseInRight(mouseX, mouseY, leftPos, topPos)) {
                if (whiteFilters.size > 8) {
                    rightScrollOff = Mth.clamp(
                        (rightScrollOff - delta).toInt(),
                        0,
                        whiteFilters.size - 7
                    )
                }
            }
        }
        return true
    }

    @Suppress("DuplicatedCode")
    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, dragX: Double, dragY: Double): Boolean {
        val leftPos = (width - imageWidth) / 2
        val topPos = (height - imageHeight) / 2
        if (mouseInLeftSlider(mouseX, mouseY, leftPos, topPos)) {
            val i = filteredFilters.size
            return if (isDraggingLeft) {
                val j = this.topPos + SCROLL_BAR_TOP_POS_Y
                val k = j + SCROLL_BAR_HEIGHT
                val dragMax = i - 7
                var scroll = ((mouseY - j - 13.5f) / ((k - j) - 27.0f))
                scroll = scroll * dragMax + 0.5f
                leftScrollOff = Mth.clamp(scroll.toInt(), 0, dragMax)
                true
            } else {
                super.mouseDragged(mouseX, mouseY, button, dragX, dragY)
            }
        } else if (mouseInRightSlider(mouseX, mouseY, leftPos, topPos)) {
            val i = whiteFilters.size
            return if (isDraggingRight) {
                val j = this.topPos + SCROLL_BAR_TOP_POS_Y
                val k = j + SCROLL_BAR_HEIGHT
                val dragMax = i - 7
                var scroll = ((mouseY - j - 13.5f) / ((k - j) - 27.0f))
                scroll = scroll * dragMax + 0.5f
                rightScrollOff = Mth.clamp(scroll.toInt(), 0, dragMax)
                true
            } else {
                super.mouseDragged(mouseX, mouseY, button, dragX, dragY)
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        isDraggingLeft = false
        isDraggingRight = false
        val leftPos = (width - imageWidth) / 2
        val topPos = (height - imageHeight) / 2
        if (mouseInLeftSlider(mouseX, mouseY, leftPos, topPos) && filteredFilters.size > 8) {
            isDraggingLeft = true
        }
        if (mouseInRightSlider(mouseX, mouseY, leftPos, topPos) && whiteFilters.size > 8) {
            isDraggingRight = true
        }
        return super.mouseClicked(mouseX, mouseY, button)
    }

    private fun renderScroller(guiGraphics: GuiGraphics, posX: Int, posY: Int, totalCount: Int, scrollOff: Int) {
        val i = totalCount + 1 - 8
        if (i > 1) {
            val maxY = posY + SCROLL_BAR_HEIGHT - SCROLLER_HEIGHT
            var scrollY = (posY + (scrollOff / totalCount.toFloat()) * SCROLL_BAR_HEIGHT).toInt()
            scrollY = Mth.clamp(scrollY, posY, maxY)

            guiGraphics.blit(ACTIVE_SILENCER_SLIDER, posX, scrollY, 0f, 0f, 5, 9, 10, 9)
        } else {
            guiGraphics.blit(ACTIVE_SILENCER_SLIDER, posX, posY, 0f, 0f, 5, 9, 10, 9)
        }
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val leftPos = (width - imageWidth) / 2
        val topPos = (height - imageHeight) / 2

        super.render(guiGraphics, mouseX, mouseY, partialTick)
        renderScroller(guiGraphics, leftPos + 119, topPos + 35, filteredFilters.size, leftScrollOff)

        renderScroller(guiGraphics, leftPos + 245, topPos + 35, whiteFilters.size, rightScrollOff)

        renderTooltip(guiGraphics, mouseX, mouseY)
    }

    fun handleSync(filters: List<Pair<TargetFilter, String>>) {
        rightScrollOff = 0
        whiteFilters.clear()
        whiteFilters.addAll(filters)
        onSearchTextChange("")
        turretMenu.handleSync(filters)
    }

    override fun renderLabels(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        guiGraphics.drawString(font, title, titleLabelX, titleLabelY, 0x404040, false)
    }

    override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        val i = (width - imageWidth) / 2
        val j = (height - imageHeight) / 2
        guiGraphics.blit(CONTAINER_LOCATION, i, j, 0, 0, imageWidth, imageHeight)
    }

    companion object {
        private val CONTAINER_LOCATION: ResourceLocation =
            StellarExodusCore.of("textures/gui/container/machine/background/auto_aim_turret.png")

        val ACTIVE_SILENCER_SLIDER: ResourceLocation =
            StellarExodusCore.of("textures/gui/container/machine/active_silencer_slider.png")

        private const val SCROLL_BAR_HEIGHT = 120
        private const val SCROLL_BAR_TOP_POS_Y = 35
        private const val START_LEFT_X = 6
        private const val START_RIGHT_X = 132
        private const val SCROLL_BAR_START_LEFT_X = 120
        private const val SCROLL_BAR_START_RIGHT_X = 245
        private const val SCROLL_BAR_WIDTH = 5
        private const val SCROLLER_HEIGHT = 9

        const val FILTER_FILTERED = 0
        const val SOUND_MUTED = 1
    }
}
