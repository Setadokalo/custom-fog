package setadokalo.customfog;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.codec.PacketDecoder;
import net.minecraft.network.codec.PacketEncoder;
import net.minecraft.network.codec.ValueFirstEncoder;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import com.google.gson.GsonBuilder;

import io.netty.buffer.ByteBuf;
import setadokalo.customfog.config.ConfigLoader;
import setadokalo.customfog.config.CustomFogConfig.FogType;
import setadokalo.customfog.config.DimensionConfig;

public class CustomFog implements ModInitializer {
	public static final String MOD_ID = CustomFogLogger.MOD_ID;

	public static final Identifier SERVER_CONFIG_PACKET_ID = new Identifier(CustomFog.MOD_ID, "server_config");
	public static final Identifier OP_UPDATE_CONFIG_PACKET_ID = new Identifier(CustomFog.MOD_ID, "op_update_config");

	public static final String WATER_CONFIG = "_customfog_internal:__/water/__";
	public static final String POWDER_SNOW_CONFIG = "_customfog_internal:__/snow/__";
	public static final String DEFAULT_CONFIG = "_customfog_internal:__/default/__";
	public static final String UNIVERSAL_CONFIG = "_customfog_internal:__/universal/__";

	@Override
	public void onInitialize() {
		CustomFogLogger.info( "Initializing Custom Fog");
		ConfigLoader.GSON = new GsonBuilder()
		.registerTypeAdapter(Identifier.class, new Identifier.Serializer())
		.enableComplexMapKeySerialization()
		.serializeNulls()
		.setPrettyPrinting()
		.create();
		PayloadTypeRegistry.playC2S().register(UpdateServerConfigPayload.ID, UpdateServerConfigPayload.CODEC);
		PayloadTypeRegistry.playS2C().register(CFServerConfigPayload.ID, CFServerConfigPayload.CODEC);
	}


	public record CFServerConfigPayload(String json) implements CustomPayload {
		public static final CustomPayload.Id<CFServerConfigPayload> ID = new CustomPayload.Id<>(SERVER_CONFIG_PACKET_ID);
		public static final PacketCodec<RegistryByteBuf, CFServerConfigPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING, CFServerConfigPayload::json, CFServerConfigPayload::new);
		@Override
		public CustomPayload.Id<? extends CustomPayload> getId() {
			return ID;
		}
	}
	public record UpdateServerConfigPayload(Identifier dimId, DimensionConfig config) implements CustomPayload {
		public static final CustomPayload.Id<UpdateServerConfigPayload> ID = new CustomPayload.Id<>(SERVER_CONFIG_PACKET_ID);
		public static final PacketCodec<RegistryByteBuf, UpdateServerConfigPayload> CODEC = PacketCodec.tuple(
			PacketCodec.of(
				(value, buf) -> buf.writeIdentifier(value), 
				buf -> buf.readIdentifier()
			), UpdateServerConfigPayload::dimId,
			PacketCodec.tuple(
				PacketCodecs.BOOL,  DimensionConfig::getEnabled,
				PacketCodec.of(
					(value, buf) -> buf.writeByte(value.ordinal()),
					buf -> FogType.values()[buf.readByte()]
				),                  DimensionConfig::getType,
				PacketCodecs.FLOAT, DimensionConfig::getLinearStart,
				PacketCodecs.FLOAT, DimensionConfig::getLinearEnd,
				PacketCodecs.FLOAT, DimensionConfig::getExp,
				PacketCodecs.FLOAT, DimensionConfig::getExp2,
				DimensionConfig::new
			), UpdateServerConfigPayload::config,
			UpdateServerConfigPayload::new
		);
		@Override
		public CustomPayload.Id<? extends CustomPayload> getId() {
			return ID;
		}
	}
}

