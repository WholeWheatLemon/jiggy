package lol.ollie.jiggy.client

import com.zigythebird.playeranim.animation.PlayerAnimationController
import com.zigythebird.playeranim.api.PlayerAnimationAccess
import com.zigythebird.playeranim.api.PlayerAnimationFactory
import com.zigythebird.playeranimcore.enums.PlayState
import lol.ollie.jiggy.client.menu.EditWheelScreen
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.render.RenderTickCounter
import net.minecraft.client.util.InputUtil
import net.minecraft.entity.PlayerLikeEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW


class JiggyClient : ClientModInitializer {

    var wheelOpened = false

    var selectedSegment = -1

    override fun onInitializeClient() {

        HudElementRegistry.attachElementAfter(VanillaHudElements.SUBTITLES, Identifier.of("jiggy", "before_chat"), ::renderWheel)

        val emoteWheelBind = KeyBindingHelper.registerKeyBinding(KeyBinding(
            "key.jiggy.wheel",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            KeyBinding.Category.create(Identifier.of("jiggy", "main"))
        ))

        val debugScreenBind = KeyBindingHelper.registerKeyBinding(KeyBinding(
            "key.jiggy.debugscreen",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_P,
            KeyBinding.Category(Identifier.of("jiggy", "main"))
        ))

        val animationLayer = Identifier.of("jiggy", "emotes")

        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(animationLayer, 1000) {
            player: PlayerLikeEntity? ->
                PlayerAnimationController(player) { _, _, _ -> PlayState.STOP
                }
        }

        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client: MinecraftClient? ->
            if (client?.player != null) {

                val player = client.player!!

                val controller = PlayerAnimationAccess.getPlayerAnimationLayer(
                    player, animationLayer
                ) as PlayerAnimationController?

                if (emoteWheelBind.isPressed && !wheelOpened) {
                    MinecraftClient.getInstance().mouse.unlockCursor()
                    wheelOpened = true
                } else if (!emoteWheelBind.isPressed && wheelOpened) {
                    wheelOpened = false
                    MinecraftClient.getInstance().mouse.lockCursor()
                    if (selectedSegment != -1) controller?.stopTriggeredAnimation()
                    if (selectedSegment == 0) controller?.triggerAnimation(Identifier.of("jiggy", "test"))
                }

                if (player.isSneaking) {
                    controller?.stopTriggeredAnimation()
                }

                if (debugScreenBind.isPressed && MinecraftClient.getInstance().currentScreen == null) {
                    MinecraftClient.getInstance().setScreen(EditWheelScreen(Text.literal("cool screen wow!")))
                }
            }
        })
    }

    private fun renderWheel(context: DrawContext, tickCounter: RenderTickCounter) {
        if (wheelOpened) {

            val segmentTexture = Identifier.of("jiggy", "textures/gui/wheel/segment.png")
            val selectedSegmentTexture = Identifier.of("jiggy", "textures/gui/wheel/selected_segment.png")

            val guiScale = MinecraftClient.getInstance().window.scaleFactor

            val mouse = MinecraftClient.getInstance().mouse

            val centerX = context.scaledWindowWidth / 2
            val centerY = context.scaledWindowHeight / 2

            val mouseX = mouse.x/guiScale
            val mouseY = mouse.y/guiScale

            selectedSegment = -1
            if (mouseY < centerY-32) {
                selectedSegment = if (mouseX < centerX-32) 0
                else if (mouseX > centerX+32) 2
                else 1
            } else if (mouseY > centerY+32) {
                selectedSegment = if (mouseX < centerX-32) 5
                else if (mouseX > centerX+32) 7
                else 6
            } else {
                if (mouseX < centerX-32) selectedSegment = 3
                else if (mouseX > centerX+32) selectedSegment = 4
            }

            var segment = 0
            for (y in -1..1) {
                for (x in -1..1) {

                    if (x == 0 && y == 0) continue

                    var texture = segmentTexture
                    if (segment == selectedSegment) texture = selectedSegmentTexture

                    context.drawTexture(
                        RenderPipelines.GUI_TEXTURED,
                        texture,
                        centerX - 32 + x * 56,
                        centerY - 32 + y * 56,
                        0F, 0F, 64, 64, 64, 64)

                    segment++
                }
            }
        }
    }
}
