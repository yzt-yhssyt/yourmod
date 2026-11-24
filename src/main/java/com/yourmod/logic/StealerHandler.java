package com.yourmod.logic;

import com.yourmod.enchant.ModEnchantments;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.WardenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;

public final class StealerHandler {
	private StealerHandler() {
	}

	public static void init() {
		ServerLivingEntityEvents.AFTER_DAMAGE.register(StealerHandler::afterDamage);
	}

	private static void afterDamage(LivingEntity target, DamageSource source, float baseDamage, float damage, boolean blocked) {
		if (blocked || damage <= 0.0F || !(target.getWorld() instanceof ServerWorld world)) {
			return;
		}

		Entity attackerEntity = source.getAttacker();
		if (!(attackerEntity instanceof ServerPlayerEntity player) || source.getSource() != attackerEntity) {
			return;
		}

		if (target instanceof PlayerEntity || isBoss(target)) {
			return;
		}

		ItemStack weapon = player.getMainHandStack();
		int level = ModEnchantments.level(world, weapon, ModEnchantments.STEALER);
		if (level <= 0) {
			return;
		}

		RegistryKey<LootTable> lootKey = target.getLootTable();
		if (lootKey == null) {
			return;
		}

		LootTable table = world.getServer().getReloadableRegistries().getLootTable(lootKey);
		if (table == LootTable.EMPTY) {
			return;
		}

		LootContextParameterSet parameters = new LootContextParameterSet.Builder(world)
				.add(LootContextParameters.THIS_ENTITY, target)
				.add(LootContextParameters.ORIGIN, target.getPos())
				.add(LootContextParameters.DAMAGE_SOURCE, source)
				.addOptional(LootContextParameters.ATTACKING_ENTITY, player)
				.addOptional(LootContextParameters.DIRECT_ATTACKING_ENTITY, player)
				.addOptional(LootContextParameters.TOOL, weapon)
				.addOptional(LootContextParameters.LAST_DAMAGE_PLAYER, player)
				.build(LootContextTypes.ENTITY);

		ObjectArrayList<ItemStack> loot = table.generateLoot(parameters, world.getRandom());
		if (loot.isEmpty()) {
			// An empty loot table does not consume the trigger chance.
			return;
		}

		float chance = 0.05F + 0.03F * (level - 1);
		if (world.getRandom().nextFloat() >= chance) {
			return;
		}

		ItemStack chosen = loot.get(world.getRandom().nextInt(loot.size())).copy();
		if (chosen.isEmpty()) {
			return;
		}

		if (!player.getInventory().insertStack(chosen)) {
			ItemEntity item = new ItemEntity(world, player.getX(), player.getY() + 1.0, player.getZ(), chosen);
			item.setToDefaultPickupDelay();
			world.spawnEntity(item);
		}
	}

	private static boolean isBoss(LivingEntity entity) {
		return entity instanceof WitherEntity
				|| entity instanceof EnderDragonEntity
				|| entity instanceof WardenEntity;
	}
}
