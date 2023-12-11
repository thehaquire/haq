package com.haq.haqclient.gui

import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiButton

class ConfigMenu : GuiScreen() {
    private var dropdownExpanded = false
    private val dropdownButton = GuiButton(0, this.width / 2 - 100, this.height / 2, "Open Dropdown")

    override fun initGui() {
        super.initGui()
        this.buttonList.add(dropdownButton)
    }

    override fun actionPerformed(button: GuiButton) {
        if (button == dropdownButton) {
            dropdownExpanded = !dropdownExpanded
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        if (dropdownExpanded) {
            // Render dropdown items here
            drawCenteredString(fontRendererObj, "Item 1", width / 2, height / 2 + 20, 0xFFFFFF)
            // Add more items as needed
        }
    }
}