package com.haq.haqclient.util
import net.minecraft.client.Minecraft
import net.minecraft.util.ChatComponentText

object Sender {
    fun send(message: String) {
        // Ensure that we are on the client side and the player is not null
        val mc = Minecraft.getMinecraft()
        val player = mc.thePlayer
        if (player != null) {
            val chatComponent = ChatComponentText(message)
            player.addChatMessage(chatComponent)
        }
    }
}
