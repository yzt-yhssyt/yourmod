package com.yourmod.logic;

import com.yourmod.enchant.ModEnchantments;
import com.yourmod.network.ThrustPayload;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class ThrustHandler {
	private static final long ITEM_COOLDOWN_TICKS_PER_BLOCK = 1200L;
	private static final long GLOBAL_COOLDOWN_TICKS_PER_BLOCK = 600L;

	private static final Map<UUID, IdentityHashMap<ItemStack, Long>> ITEM_COOLDOWNS = new HashMap<>();
	private static final Map<UUID, Long> GLOBAL_COOLDOWNS = new HashMap<>();

	private ThrustHandler() {
	}

	public static void init() {
		ServerPlayNetworking.registerGlobalReceiver(ThrustPayload.ID, (payload, context) ->
				context.server().execute(() -> tryThrust(context.player())));

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			if (handler.player != null) {
				clear(handler.player.getUuid());
			}
		});
	}

	public static void clear(UUID playerUuid) {
		ITEM_COOLDOWNS.remove(playerUuid);
		GLOBAL_COOLDOWNS.remove(playerUuid);
	}

	private static void tryThrust(ServerPlayerEntity player) {
		ServerWorld world = player.getServerWorld();
		ItemStack stack = player.getMainHandStack();
		int level = ModEnchantments.level(world, stack, ModEnchantments.THRUST);
		if (level <= 0) {
			return;
		}

		long now = world.getServer().getTicks();
		long globalEnd = GLOBAL_COOLDOWNS.getOrDefault(player.getUuid(), 0L);
		IdentityHashMap<ItemStack, Long> perItem = ITEM_COOLDOWNS.get(player.getUuid());
		long itemEnd = perItem == null ? 0L : perItem.getOrDefault(stack, 0L);
		if (now < itemEnd || now < globalEnd) {
			return;
		}

		Vec3d look = player.getRotationVec(1.0F);
		Vec3d direction = new Vec3d(look.x, 0.0, look.z);
		if (direction.lengthSquared() < 1.0E-6) {
			return;
		}
		direction = direction.normalize();

		int maxDistance = level >= 2 ? 10 : 5;
		Box startBox = player.getBoundingBox();
		int travelled = 0;

		for (int step = 1; step <= maxDistance; step++) {
			if (!world.isSpaceEmpty(player, startBox.offset(direction.multiply(step)))) {
				break;
			}
			travelled = step;
		}

		if (travelled <= 0) {
			return;
		}

		Vec3d destination = player.getPos().add(direction.multiply(travelled));
		player.teleport(world, destination.x, destination.y, destination.z, Set.<PositionFlag>of(), player.getYaw(), player.getPitch());

		if (stack.isDamageable() && !stack.contains(DataComponentTypes.UNBREAKABLE)) {
			stack.damage(travelled, player, EquipmentSlot.MAINHAND);
		}

		perItem = ITEM_COOLDOWNS.computeIfAbsent(player.getUuid(), key -> new IdentityHashMap<>());
		perItem.put(stack, now + travelled * ITEM_COOLDOWN_TICKS_PER_BLOCK);
		GLOBAL_COOLDOWNS.put(player.getUuid(), now + travelled * GLOBAL_COOLDOWN_TICKS_PER_BLOCK);
	}
}
