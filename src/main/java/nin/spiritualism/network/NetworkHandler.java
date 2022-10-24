package nin.spiritualism.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import nin.spiritualism.Spiritualism;
import nin.spiritualism.capability.SpiritHandler;

import java.util.function.Function;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Spiritualism.MODID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    private static int i = 0;

    public static void init() {
        register(SpiritHandler.class, SpiritHandler::new);
        register(FlySpeedPacket.class, FlySpeedPacket::new);
    }

    public static <MSG extends IPacket> void register(Class<MSG> c, Function<FriendlyByteBuf, MSG> decoder) {
        INSTANCE.registerMessage(i++, c, IPacket::encode, decoder, IPacket::handle);
    }
}
