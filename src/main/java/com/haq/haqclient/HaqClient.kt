package com.haq.haqclient

import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraft.util.IChatComponent
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
import org.lwjgl.input.Keyboard
import scala.swing.event.Key
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.Mod
import net.minecraft.entity.Entity
import net.minecraft.entity.monster.EntityZombie
@Mod(modid = HaqClient.MODID, version = HaqClient.VERSION)
class HaqClient {
    companion object {
        const val MODID = "haqclient"
        const val VERSION = "1.0"
    }

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        // Pre-initialization code
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
        // Post-initialization code
    }



    @SubscribeEvent
    fun onPlayerLogin(event: PlayerEvent.PlayerLoggedInEvent) {
        event.player.addChatMessage(ChatComponentText("Welcome to HaqClient!"))
        Sender.send("Player logged in")
    }
}