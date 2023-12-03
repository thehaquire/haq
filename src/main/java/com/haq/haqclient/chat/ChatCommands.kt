package com.haq.haqclient.chat

import com.haq.haqclient.util.Sender
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import com.haq.haqclient.util.noControlCodes
class ChatCommands {
    private var lock = false
    private fun rCmd(st: String) {
        Minecraft.getMinecraft().thePlayer.sendChatMessage(st)
    }
    @SubscribeEvent
    fun onChatReceived(event: ClientChatReceivedEvent) {
        val message = event.message.unformattedText.noControlCodes
        if (Regex("(\\[.+])? ?(.+) has disbanded the party!").matches(message)) {
            lock = false
        } else {
            val reg = Regex("Party > (\\[.+])? ?(.+): \\?([^\\s]+)$") // Updated regex pattern
            if (reg.matches(message)) {
                cmdRun(reg.matchEntire(message)?.groups?.get(2)?.value, reg.matchEntire(message)?.groups?.get(3)?.value)
            }
        }
    }
    private fun cmdRun(ign: String?, command: String?) {
        val firstWord = command?.split(" ")?.getOrNull(0) ?: return
        when (firstWord) {
            "help" -> rCmd("/pc HaqClient CMDs: ?help, ?pt, ?warp, ?allinv, ?rq")
            "pt" -> if (!lock) rCmd("/p transfer " + ign)
            "warp" -> if (!lock) rCmd("/p warp")
            "allinv" -> if (!lock) rCmd("/p settings allinvite")
            "rq" -> if (!lock) rCmd("/instancerequeue")
            "lock" -> if (lock && ign.equals(Minecraft.getMinecraft().thePlayer.name)) {
                lock = false
                rCmd("/pc Unlocked Command Execution")
            } else if (!lock && ign.equals(Minecraft.getMinecraft().thePlayer.name)) {
                lock = true
                rCmd("/pc Locked Command Execution")
            } else {
                rCmd("/pc You cannot use this command!")
            }

        }
    }
}