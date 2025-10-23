package lol.ollie.jiggy.client

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
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

        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick { client: MinecraftClient? ->
            while (emoteWheelBind.wasPressed()) {
                client!!.player!!.sendMessage(Text.literal("Key 1 was pressed!"), false)
            }
        })

    }
}
