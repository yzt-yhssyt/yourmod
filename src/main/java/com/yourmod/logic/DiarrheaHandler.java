package com.yourmod.logic;

import com.yourmod.effect.ModEffects;
import com.yourmod.enchant.ModEnchantments;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;

public final class DiarrheaHandler {
	private DiarrheaHandler() {
	}

	public static void init() {
		ServerLivingEntityEvents.AFTER_DAMAGE.register(DiarrheaHandler::afterDamage);
	}

	private static void afterDamage(LivingEntity wearer, DamageSource source, float baseDamage, float damage, boolean blocked) {
		if (blocked || damage <= 0.0F || !(wearer.getWorld() instanceof ServerWorld world)) {
			return;
		}

		int level = Math.max(
				ModEnchantments.level(world, wearer.getEquippedStack(EquipmentSlot.CHEST), ModEnchantments.DIARRHEA),
				ModEnchantments.level(world, wearer.getEquippedStack(EquipmentSlot.LEGS), ModEnchantments.DIARRHEA)
		);
		if (level <= 0) {
			return;
		}

		Entity attackerEntity = source.getAttacker();
		if (!(attackerEntity instanceof LivingEntity attacker) || attacker == wearer) {
			return;
		}
		if (source.getSource() != attackerEntity) {
			return;
		}

		float chance = level >= 2 ? 0.15F : 0.08F;
		if (world.getRandom().nextFloat() >= chance) {
			return;
		}

		attacker.removeStatusEffect(ModEffects.DIARRHEA);
		attacker.addStatusEffect(
				new StatusEffectInstance(ModEffects.DIARRHEA, 200, 0, false, false, false),
				wearer
		);
	}
}
