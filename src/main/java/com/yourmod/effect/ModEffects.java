package com.yourmod.effect;

import com.yourmod.YourMod;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public final class ModEffects {
	public static final RegistryEntry.Reference<StatusEffect> DIARRHEA = Registry.registerReference(
			Registries.STATUS_EFFECT,
			Identifier.of(YourMod.MOD_ID, "diarrhea"),
			new DiarrheaEffect()
	);

	public static final RegistryEntry.Reference<StatusEffect> EXPLORER_WEAK = Registry.registerReference(
			Registries.STATUS_EFFECT,
			Identifier.of(YourMod.MOD_ID, "explorer_marked_weak"),
			new ExplorerMarkedEffect(0x9A7B4F, -0.10, "explorer_marked_weak")
	);

	public static final RegistryEntry.Reference<StatusEffect> EXPLORER_STRONG = Registry.registerReference(
			Registries.STATUS_EFFECT,
			Identifier.of(YourMod.MOD_ID, "explorer_marked_strong"),
			new ExplorerMarkedEffect(0x6E4B2A, -0.15, "explorer_marked_strong")
	);

	private ModEffects() {
	}

	public static void register() {
		// Static initializers perform the registration.
	}

	private static final class DiarrheaEffect extends StatusEffect {
		private DiarrheaEffect() {
			super(StatusEffectCategory.HARMFUL, 0x6B4F2A);
			this.addAttributeModifier(
					EntityAttributes.GENERIC_MOVEMENT_SPEED,
					Identifier.of(YourMod.MOD_ID, "diarrhea_slow"),
					-0.30,
					EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
			);
		}

		@Override
		public boolean canApplyUpdateEffect(int duration, int amplifier) {
			return duration % 20 == 0;
		}

		@Override
		public boolean applyUpdateEffect(net.minecraft.entity.LivingEntity entity, int amplifier) {
			entity.damage(entity.getDamageSources().magic(), 0.25F);
			return true;
		}
	}

	private static final class ExplorerMarkedEffect extends StatusEffect {
		private ExplorerMarkedEffect(int color, double slow, String modifierName) {
			super(StatusEffectCategory.HARMFUL, color);
			this.addAttributeModifier(
					EntityAttributes.GENERIC_MOVEMENT_SPEED,
					Identifier.of(YourMod.MOD_ID, modifierName),
					slow,
					EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
			);
		}

		@Override
		public boolean canApplyUpdateEffect(int duration, int amplifier) {
			return false;
		}
	}
}
