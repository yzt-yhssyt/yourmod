package com.yourmod.enchant;

import com.yourmod.YourMod;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public final class ModEnchantments {
	public static final RegistryKey<Enchantment> SUNLIGHT_MENDING = key("sunlight_mending");
	public static final RegistryKey<Enchantment> MOONLIGHT_MENDING = key("moonlight_mending");
	public static final RegistryKey<Enchantment> STEALER = key("stealer");
	public static final RegistryKey<Enchantment> THRUST = key("thrust");
	public static final RegistryKey<Enchantment> DIARRHEA = key("diarrhea");
	public static final RegistryKey<Enchantment> EXPLORER = key("explorer");

	private ModEnchantments() {
	}

	private static RegistryKey<Enchantment> key(String name) {
		return RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of(YourMod.MOD_ID, name));
	}

	public static RegistryEntry<Enchantment> entry(World world, RegistryKey<Enchantment> key) {
		return world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(key).orElse(null);
	}

	public static int level(World world, ItemStack stack, RegistryKey<Enchantment> key) {
		RegistryEntry<Enchantment> entry = entry(world, key);
		return entry == null ? 0 : EnchantmentHelper.getLevel(entry, stack);
	}

	public static int equipmentLevel(World world, LivingEntity entity, RegistryKey<Enchantment> key) {
		RegistryEntry<Enchantment> entry = entry(world, key);
		return entry == null ? 0 : EnchantmentHelper.getEquipmentLevel(entry, entity);
	}
}
