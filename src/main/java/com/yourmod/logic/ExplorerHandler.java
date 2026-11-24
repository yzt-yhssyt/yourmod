package com.yourmod.logic;

import com.yourmod.YourMod;
import com.yourmod.effect.ModEffects;
import com.yourmod.enchant.ModEnchantments;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public final class ExplorerHandler {
	private static final Identifier SELF_MODIFIER_ID = Identifier.of(YourMod.MOD_ID, "explorer_self");
	private static final double RANGE = 16.0;
	private static final int EFFECT_DURATION_TICKS = 40;

	private ExplorerHandler() {
	}

	public static void init() {
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (server.getTicks() % 5 != 0) {
				return;
			}

			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
				tick(player);
			}
		});

		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
			if (handler.player != null) {
				removeSelfModifier(handler.player);
			}
		});
	}

	private static void tick(ServerPlayerEntity player) {
		ServerWorld world = player.getServerWorld();
		int level = Math.max(
				ModEnchantments.level(world, player.getEquippedStack(EquipmentSlot.HEAD), ModEnchantments.EXPLORER),
				ModEnchantments.level(world, player.getEquippedStack(EquipmentSlot.FEET), ModEnchantments.EXPLORER)
		);

		if (level <= 0) {
			removeSelfModifier(player);
			return;
		}

		LivingEntity target = findTarget(player);
		if (target == null) {
			removeSelfModifier(player);
			return;
		}

		applySelfModifier(player, level);
		applyTargetEffect(player, target, level);
	}

	private static LivingEntity findTarget(ServerPlayerEntity player) {
		Vec3d eye = player.getEyePos();
		Vec3d direction = player.getRotationVec(1.0F);
		Vec3d end = eye.add(direction.multiply(RANGE));
		Box searchBox = player.getBoundingBox().expand(RANGE);

		EntityHitResult hit = ProjectileUtil.raycast(
				player,
				eye,
				end,
				searchBox,
				entity -> entity instanceof Monster && entity != player && entity.isAlive(),
				RANGE * RANGE
		);

		if (hit == null || !(hit.getEntity() instanceof LivingEntity target)) {
			return null;
		}
		if (player.squaredDistanceTo(target) > RANGE * RANGE) {
			return null;
		}
		if (!player.canSee(target)) {
			return null;
		}
		return target;
	}

	private static void applySelfModifier(ServerPlayerEntity player, int level) {
		EntityAttributeInstance attribute = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
		if (attribute == null) {
			return;
		}

		attribute.removeModifier(SELF_MODIFIER_ID);
		attribute.addTemporaryModifier(new EntityAttributeModifier(
				SELF_MODIFIER_ID,
				level >= 2 ? 0.15 : 0.10,
				EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
		));
	}

	private static void removeSelfModifier(ServerPlayerEntity player) {
		EntityAttributeInstance attribute = player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
		if (attribute != null) {
			attribute.removeModifier(SELF_MODIFIER_ID);
		}
	}

	private static void applyTargetEffect(ServerPlayerEntity player, LivingEntity target, int level) {
		if (level >= 2) {
			target.removeStatusEffect(ModEffects.EXPLORER_WEAK);
			target.addStatusEffect(
					new StatusEffectInstance(ModEffects.EXPLORER_STRONG, EFFECT_DURATION_TICKS, 0, false, false, false),
					player
			);
			return;
		}

		if (!target.hasStatusEffect(ModEffects.EXPLORER_STRONG)) {
			target.addStatusEffect(
					new StatusEffectInstance(ModEffects.EXPLORER_WEAK, EFFECT_DURATION_TICKS, 0, false, false, false),
					player
			);
		}
	}
}
