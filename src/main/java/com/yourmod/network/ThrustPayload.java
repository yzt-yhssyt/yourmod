package com.yourmod.network;

import com.yourmod.YourMod;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public record ThrustPayload() implements CustomPayload {
	public static final CustomPayload.Id<ThrustPayload> ID =
			new CustomPayload.Id<>(Identifier.of(YourMod.MOD_ID, "thrust"));
	public static final PacketCodec<RegistryByteBuf, ThrustPayload> CODEC =
			PacketCodec.unit(new ThrustPayload());

	@Override
	public CustomPayload.Id<? extends CustomPayload> getId() {
		return ID;
	}

	public static void register() {
		PayloadTypeRegistry.playC2S().register(ID, CODEC);
	}
}
