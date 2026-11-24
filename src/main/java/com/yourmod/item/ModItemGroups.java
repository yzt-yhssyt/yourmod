package com.yourmod.item;

import com.yourmod.YourMod;
import com.yourmod.enchant.ModEnchantments;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class ModItemGroups {
	public static final RegistryKey<ItemGroup> ENCHANTMENTS = RegistryKey.of(
			RegistryKeys.ITEM_GROUP,
			Identifier.of(YourMod.MOD_ID, "enchantments")
	);

	private ModItemGroups() {
	}

	public static void register() {
		Registry.register(Registries.ITEM_GROUP, ENCHANTMENTS, FabricItemGroup.builder()
				.displayName(Text.translatable("itemGroup.yourmod.enchantments"))
				.icon(() -> new ItemStack(Items.ENCHANTED_BOOK))
				.entries((context, entries) -> {
					addBook(context, entries, ModEnchantments.SUNLIGHT_MENDING, 3);
					addBook(context, entries, ModEnchantments.MOONLIGHT_MENDING, 3);
					addBook(context, entries, ModEnchantments.STEALER, 4);
					addBook(context, entries, ModEnchantments.THRUST, 2);
					addBook(context, entries, ModEnchantments.DIARRHEA, 2);
					addBook(context, entries, ModEnchantments.EXPLORER, 2);
				})
				.build());
	}

	private static void addBook(ItemGroup.DisplayContext context, ItemGroup.Entries entries,
			RegistryKey<Enchantment> key, int level) {
		RegistryEntry.Reference<Enchantment> enchantment = context.lookup()
				.getWrapperOrThrow(RegistryKeys.ENCHANTMENT)
				.getOrThrow(key);

		ItemEnchantmentsComponent.Builder enchantments = new ItemEnchantmentsComponent.Builder(
				ItemEnchantmentsComponent.DEFAULT);
		enchantments.add(enchantment, level);

		ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
		book.set(DataComponentTypes.STORED_ENCHANTMENTS, enchantments.build());
		entries.add(book);
	}
}
