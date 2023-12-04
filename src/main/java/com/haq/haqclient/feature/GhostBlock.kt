package com.haq.haqclient.feature
import org.lwjgl.input.Keyboard
import net.minecraft.block.Block
import java.lang.Thread
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.client.multiplayer.WorldClient
import net.minecraft.client.renderer.RenderGlobal
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class GhostBlock {
    private val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private var pos: BlockPos? = null
    private var lastGhostBlockTime: Long = 0

    @SubscribeEvent
    fun onRenderWorld(event: RenderWorldLastEvent) {
        if (Minecraft.getMinecraft().ingameGUI.chatGUI.chatOpen) {
            // player is in chat cooldown, don't create ghost blocks
            return
        }
        val ghostBlockKeybind = Keyboard.KEY_G
        val world = Minecraft.getMinecraft().theWorld
        if (Keyboard.isKeyDown(ghostBlockKeybind)) {
            val currentTime = System.currentTimeMillis()

            if (currentTime - lastGhostBlockTime >= 50) {
                val player = Minecraft.getMinecraft().thePlayer
                val lookingAt = player.rayTrace(5.toDouble(), 1f).blockPos
                val blockState: IBlockState = world.getBlockState(lookingAt)
                val block: Block = blockState.block

                if (block == Blocks.chest || block == Blocks.trapped_chest ||
                        block == Blocks.ender_chest || block == Blocks.lever ||
                        block == Blocks.stone_button || block == Blocks.wooden_button
                ) {
                    return
                }
                val x = lookingAt.x
                val y = lookingAt.y
                val z = lookingAt.z
                pos = BlockPos(x, y, z)
                world.setBlockState(pos!!, Blocks.air.defaultState)
                world.markBlockRangeForRenderUpdate(x, y, z, x, y, z)

                lastGhostBlockTime = currentTime
                scheduleBlockRemoval() // Schedule the removal after 1 second
            }
        }
    }

    private fun scheduleBlockRemoval() {
        executor.schedule({
            pos?.let {
                val world = Minecraft.getMinecraft().theWorld
                world.setBlockToAir(it)
            }
        }, 1, TimeUnit.SECONDS)
    }
}