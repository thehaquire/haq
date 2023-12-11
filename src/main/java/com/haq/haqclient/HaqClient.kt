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
import com.haq.haqclient.gui.ConfigMenu
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

@Mod(modid = HaqClient.MODID, version = HaqClient.VERSION)
class HaqClient {
    companion object {
        const val MODID = "haqclient"
        const val VERSION = "1.0"
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
    @SubscribeEvent
    fun onRender(event: RenderWorldLastEvent) {
        val mc = Minecraft.getMinecraft()
        val player = mc.thePlayer
        val range = 16 // Range to check for beds

        for (x in -range..range) {
            for (y in -range..range) {
                for (z in -range..range) {
                    val pos = BlockPos(player.posX + x, player.posY + y, player.posZ + z)
                    val block = mc.theWorld.getBlockState(pos).block

                    if (block === Blocks.bed) {
                        renderBedESP(pos)
                    }
                }
            }
        }
    }

    private fun renderBedESP(pos: BlockPos) {
        // Render the ESP around the bed
 //       GlStateManager.pushMatrix()
        // Add OpenGL rendering code here
  //      GlStateManager.popMatrix()
        Sender.send("${pos.x}, ${pos.y}, ${pos.z}")
    }
}