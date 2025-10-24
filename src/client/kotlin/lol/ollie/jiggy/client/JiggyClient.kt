package lol.ollie.jiggy.client

import com.zigythebird.playeranim.animation.PlayerAnimationController
import com.zigythebird.playeranim.api.PlayerAnimationAccess
import com.zigythebird.playeranim.api.PlayerAnimationFactory
import com.zigythebird.playeranimcore.animation.AnimationController
import com.zigythebird.playeranimcore.enums.PlayState
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.entity.PlayerLikeEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW


class JiggyClient : ClientModInitializer {

    override fun onInitializeClient() {

        val emoteWheelBind = KeyBindingHelper.registerKeyBinding(KeyBinding(
            "key.jiggy.wheel",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            KeyBinding.Category.create(Identifier.of("jiggy", "main"))
        ))

        val animationLayer = Identifier.of("jiggy", "emotes")

        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(animationLayer, 1000,
            PlayerAnimationFactory { player: PlayerLikeEntity? ->
                PlayerAnimationController(
                    player,
                    AnimationController.AnimationStateHandler { controller, state, animSetter -> PlayState.STOP }
                )
            }
        )

        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client: MinecraftClient? ->
            while (emoteWheelBind.wasPressed()) {

                client!!.player!!.sendMessage(Text.literal("emote!"), false)

                val controller = PlayerAnimationAccess.getPlayerAnimationLayer(
                    client.player!!, animationLayer
                ) as PlayerAnimationController?

                controller?.triggerAnimation(Identifier.of("jiggy", "test"))
            }
        })

    }
}
