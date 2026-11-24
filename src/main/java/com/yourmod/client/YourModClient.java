package com.yourmod.client;

import com.yourmod.network.ThrustPayload;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.gui.screen.Screen;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import org.lwjgl.glfw.GLFW;

public class YourModClient implements ClientModInitializer {
	private KeyBinding thrustKey;

	@Override
	public void onInitializeClient() {
		thrustKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.yourmod.thrust",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_U,
				"key.categories.yourmod"
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (thrustKey.wasPressed()) {
				sendThrust(client);
			}
		});
	}

	private void sendThrust(MinecraftClient client) {
		if (client.player == null || client.currentScreen != null) {
			return;
		}
		if (!Screen.hasShiftDown()) {
			return;
		}
		if (!ClientPlayNetworking.canSend(ThrustPayload.ID)) {
			return;
		}
		ClientPlayNetworking.send(new ThrustPayload());
	}
}
