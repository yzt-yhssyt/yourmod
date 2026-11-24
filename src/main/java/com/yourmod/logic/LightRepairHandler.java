package com.yourmod.logic;

import com.yourmod.enchant.ModEnchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.util.math.MathHelper;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public final class LightRepairHandler {
	private static final int INTERVAL_TICKS = 20;

	private LightRepairHandler() {
	}

	public static void init() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getTicks() % INTERVAL_TICKS != 0) {
				return;
			}

			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				tick(player);
			}
		});
	}

	private static void tick(ServerPlayerEntity player) {
		ServerWorld world = player.getServerWorld();
		boolean daytime = world.getTimeOfDay() % 24000L < 13000L;
		RegistryKey<Enchantment> key = daytime ? ModEnchantments.SUNLIGHT_MENDING : ModEnchantments.MOONLIGHT_MENDING;

		if (tryRepair(world, player.getMainHandStack(), key)) {
			return;
		}

		tryRepair(world, player.getOffHandStack(), key);
	}

	private static boolean tryRepair(ServerWorld world, ItemStack stack, RegistryKey<Enchantment> key) {
		int level = ModEnchantments.level(world, stack, key);
		if (level <= 0) {
			return false;
		}

		if (!stack.isDamageable() || stack.contains(DataComponentTypes.UNBREAKABLE)) {
			return true;
		}

		int damage = stack.getDamage();
		if (damage <= 0) {
			return true;
		}

		int amount = 1 << (MathHelper.clamp(level - 1, 0, 2));
		stack.setDamage(Math.max(0, damage - amount));
		return true;
	}
}
