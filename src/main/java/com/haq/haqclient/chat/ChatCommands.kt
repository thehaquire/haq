package com.haq.haqclient.chat

import com.haq.haqclient.util.Sender
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import com.haq.haqclient.util.noControlCodes
import org.apache.logging.log4j.core.jmx.Server

class ChatCommands {
    private var lock = false
    private var downtimer = ""
    private fun rCmd(st: String) {
        Minecraft.getMinecraft().thePlayer.sendChatMessage(st)
    }
    @SubscribeEvent
    fun onChatReceived(event: ClientChatReceivedEvent) {
        val message = event.message.unformattedText.noControlCodes
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
    private fun cmdRun(sender: String?, command: String?, ign: String?) {
        val slowLowUsers = arrayOf("Adenaaa13", "haquire")
        val firstWord = command?.split(" ")?.getOrNull(0) ?: return
        when (firstWord) {
            "devhelp" -> if (ign == null) rCmd("/pc HaqClient Developer CMDs: ?lock, ?ping")
            "help" -> if (ign == null) rCmd("/pc HaqClient CMDs: ?help, ?pt, ?warp, ?allinv, ?rq, ?dt, ?coords, ?kickoffline, ?inv")
            "pt" -> if (!lock && ign == null) rCmd("/p transfer " + ign)
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
        }
    }
}