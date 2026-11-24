package com.yourmod;

import com.yourmod.effect.ModEffects;
import com.yourmod.item.ModItemGroups;
import com.yourmod.logic.DiarrheaHandler;
import com.yourmod.logic.ExplorerHandler;
import com.yourmod.logic.LightRepairHandler;
import com.yourmod.logic.StealerHandler;
import com.yourmod.logic.ThrustHandler;
import com.yourmod.network.ThrustPayload;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YourMod implements ModInitializer {
	public static final String MOD_ID = "yourmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModEffects.register();
		ModItemGroups.register();
		ThrustPayload.register();

		LightRepairHandler.init();
		StealerHandler.init();
		ThrustHandler.init();
		DiarrheaHandler.init();
		ExplorerHandler.init();

		LOGGER.info("Your Mod enchantments loaded.");
	}
}
