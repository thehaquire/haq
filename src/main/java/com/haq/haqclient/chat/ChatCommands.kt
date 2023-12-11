package com.haq.haqclient.chat

import com.haq.haqclient.util.Sender
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import com.haq.haqclient.util.noControlCodes
import net.minecraft.nbt.NBTTagString
import net.minecraft.nbt.NBTTagList
import net.minecraftforge.client.event.GuiOpenEvent
import net.minecraft.client.gui.inventory.GuiChest
import java.util.regex.Pattern
import java.lang.reflect.Field
import net.minecraft.inventory.Slot
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import org.lwjgl.input.Mouse
import net.minecraft.inventory.ContainerChest

class ChatCommands {
    private var lock = false
    private var logMenu = false
    private var downtimer = ""

    @SubscribeEvent
    fun onGuiOpen(event: GuiOpenEvent) {
        if (!logMenu || event.gui !is GuiChest) return

        val gui = event.gui as GuiChest
        val container = getPrivateField<GuiChest, IInventory>(gui, "lowerChestInventory", "field_147015_w")
        val guiTitle = container?.displayName?.unformattedText ?: return

        if (isCustomServerGui(guiTitle) && guiTitle == "Pets") {
            val hoveredItemStack = getHoveredItemStack(gui) ?: return
            getItemLore(hoveredItemStack)?.let { loreText ->
                Sender.send(loreText)
            }
        }
    }

    private fun getItemLore(itemStack: ItemStack): String? {
        if (itemStack.hasTagCompound() && itemStack.tagCompound!!.hasKey("display", 10)) {
            val display = itemStack.tagCompound!!.getCompoundTag("display")
            if (display.hasKey("Lore", 9)) {
                val loreTagList = display.getTagList("Lore", 8)
                val pattern = Pattern.compile("\\[Lvl \\d+\\] (.+)")

                for (i in 0 until loreTagList.tagCount()) {
                    val loreLine = loreTagList.getStringTagAt(i)
                    val matcher = pattern.matcher(loreLine)
                    if (matcher.find()) {
                        return matcher.group(1) // Returns the first matched group
                    }
                }
            }
        }
        return null
    }

    private fun getHoveredItemStack(gui: GuiChest): ItemStack? {
        val mouseX = Mouse.getX() * gui.width / gui.mc.displayWidth
        val mouseY = gui.height - Mouse.getY() * gui.height / gui.mc.displayHeight - 1

        val slots = (gui.inventorySlots as ContainerChest).inventorySlots
        for (slot in slots) {
            if (isMouseOverSlot(slot, mouseX, mouseY, gui)) {
                return slot.stack
            }
        }
        return null
    }

    private fun isMouseOverSlot(slot: Slot, mouseX: Int, mouseY: Int, gui: GuiChest): Boolean {
        val guiLeft = (gui.width - getProtectedFieldValue(gui, "xSize")) / 2
        val guiTop = (gui.height - getProtectedFieldValue(gui, "ySize")) / 2
        val x = mouseX - guiLeft
        val y = mouseY - guiTop
        return x >= slot.xDisplayPosition && x < slot.xDisplayPosition + 16 &&
                y >= slot.yDisplayPosition && y < slot.yDisplayPosition + 16
    }

    private fun getProtectedFieldValue(obj: Any, fieldName: String): Int {
        return try {
            val field: Field = obj.javaClass.superclass.getDeclaredField(fieldName)
            field.isAccessible = true
            field.getInt(obj)
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    private fun <T : Any, R> getPrivateField(instance: T, vararg fieldNames: String): R? {
        var field: Field? = null
        for (fieldName in fieldNames) {
            try {
                field = instance.javaClass.getDeclaredField(fieldName)
                break
            } catch (e: NoSuchFieldException) {
                // Ignore and try next name
            }
        }
        field?.isAccessible = true
        return field?.get(instance) as? R
    }

    private fun isCustomServerGui(title: String): Boolean {
        return title in listOf("Pets", "Lobby Selector")
    }

    @SubscribeEvent
    fun onChatReceived(event: ClientChatReceivedEvent) {
        val message = event.message.unformattedText.noControlCodes
        var rega = Regex("Party > (\\[.+\\])? ?(.+): (meow)$")
        if (rega.matches(message)) {
            val command = rega.matchEntire(message)?.groups?.get(3)?.value
            if (command.equals("meow")) {
                rCmd("/pc meow")
            }
        }
        if (message.contains("> EXTRA STATS <")) {
            if (downtimer != "") {
                rCmd("/pc $downtimer needs downtime!")
                downtimer = ""
            }
        }
        if (Regex("(\\[.+])? ?(.+) has disbanded the party!").matches(message)) {
            lock = false
        } else {
            val reg = Regex("Party > (\\[.+])? ?(.+): \\?([^\\s]+)(?: ([^\\s]+))?$")
            if (reg.matches(message)) {
                val matchResult = reg.matchEntire(message)
                val sender = matchResult?.groups?.get(2)?.value
                val command = matchResult?.groups?.get(3)?.value
                val ign = matchResult?.groups?.get(4)?.value // IGN as the optional fourth parameter

                cmdRun(sender, command, ign)
            }
        }
    }
    private fun numToStr(num: Char): String {
        return when (num) {
            '0' -> "ENTRANCE"
            '1' -> "ONE"
            '2' -> "TWO"
            '3' -> "THREE"
            '4' -> "FOUR"
            '5' -> "FIVE"
            '6' -> "SIX"
            '7' -> "SEVEN"
            else -> ""
        }
    }
    private fun cmdRun(sender: String?, command: String?, ign: String?) {
        val floorCommandRegex = Regex("\\?(m|f)([0-7])")
        command?.let { cmd ->
            floorCommandRegex.matchEntire(cmd)?.let { matchResult ->
                val commandType = matchResult.groups[1]?.value // 'm' or 'f'
                val floorNumber = matchResult.groups[2]?.value?.firstOrNull() // Digit between 0 and 7
                rCmd("/pc COMMAND TYPE: $commandType, $floorNumber")
                floorNumber?.let {
                    val floorStr = numToStr(it)
                    val response = when (commandType) {
                        "m" -> "/joininstance MASTER_CATACOMBS_FLOOR_$floorStr"
                        "f" -> "/joininstance CATACOMBS_FLOOR_$floorStr"
                        else -> ""
                    }
                    rCmd(response)
                    return // Stop further processing
                }
            }
        }

        val firstWord = command?.split(" ")?.getOrNull(0) ?: return
        val slowLowUsers = arrayOf("Adenaaa13", "haquire")
        when (firstWord) {
            "devhelp" -> if (ign == null) rCmd("/pc HaqClient Developer CMDs: ?lock, ?ping, ?logmenu, ?m{floor}, ?f{floor}")
            "help" -> if (ign == null) rCmd("/pc HaqClient CMDs: ?help, ?pt, ?warp, ?allinv, ?rq, ?dt, ?coords, ?kickoffline, ?inv, ?open, ?close")
            "pt" -> if (!lock && ign == null) {
                rCmd("/p transfer " + sender)
            }
            "warp" -> if (!lock && ign == null) rCmd("/p warp")
            "allinv" -> if (!lock && ign == null) rCmd("/p settings allinvite")
            "rq" -> if (!lock && ign == null) rCmd("/instancerequeue")
            "lock" -> if (lock && sender.equals(Minecraft.getMinecraft().thePlayer.name) && ign == null) {
                lock = false
                rCmd("/pc Unlocked Command Execution")
            } else if (!lock && sender.equals(Minecraft.getMinecraft().thePlayer.name) && ign == null) {
                lock = true
                rCmd("/pc Locked Command Execution")
            } else {
                rCmd("/pc You cannot use this command!")
            }
            "dt" -> if (!lock && ign == null) downtimer = sender.toString()
            "coords" -> if (!lock && ign == null) {
                var a = Minecraft.getMinecraft().thePlayer.posX.toInt()
                var b = Minecraft.getMinecraft().thePlayer.posY.toInt()
                var c = Minecraft.getMinecraft().thePlayer.posZ.toInt()
                rCmd("/pc x: $a, y: $b, z: $c")
            }
            "kickoffline" -> if (!lock && ign == null) {
                rCmd("/p kickoffline")
            }
            "ping" -> if (!lock && sender in slowLowUsers && ign == null) {
                var ping = Minecraft.getMinecraft().netHandler.getPlayerInfo(Minecraft.getMinecraft().thePlayer.gameProfile.id)?.responseTime
                rCmd("/pc ${ping ?: "unknown"}ms")
            }
            "inv" -> if (!lock) {
                rCmd("/p $ign")
            }
            "logmenu" -> if (!lock && sender in slowLowUsers && ign == null) {
                logMenu = true
            }
            "open" -> if (!lock) {
                rCmd("/stream open $ign")
            }
            "close" -> if (!lock && ign == null) {
                rCmd("/stream close")
            }
        }
    }

    private fun rCmd(st: String) {
        Minecraft.getMinecraft().thePlayer.sendChatMessage(st)
    }
}
