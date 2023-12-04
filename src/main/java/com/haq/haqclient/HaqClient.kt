package com.haq.haqclient

import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraft.util.IChatComponent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.eventhandler.EventBus
import net.minecraftforge.fml.common.gameevent.PlayerEvent
import net.minecraft.util.ChatComponentText
import com.haq.haqclient.chat.ChatCommands
import com.haq.haqclient.feature.GhostBlock
import com.haq.haqclient.util.Sender
import net.minecraft.client.Minecraft

@Mod(modid = HaqClient.MODID, version = HaqClient.VERSION)
class HaqClient {
    companion object {
        const val MODID = "haqclient"   // Replace with your mod ID
        const val VERSION = "1.0"       // Replace with your mod version
    }
    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        // Register event handlers
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(this)
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(ChatCommands())
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(GhostBlock())
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
        // Your post-initialization code here
    }

    @SubscribeEvent
    fun onPlayerLogin(event: PlayerEvent.PlayerLoggedInEvent) {
        event.player.addChatMessage(ChatComponentText("Hi"))
        Sender.send("test")
    }

}