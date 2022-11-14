package nin.spiritualism.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import nin.spiritualism.Spiritualism;
import nin.spiritualism.capability.SpiritHandler;

import java.util.function.Supplier;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(Spiritualism.MODID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    private static int i = 0;

    public static void init() {
        register(SpiritHandler.SpiritPacket::new);
        register(TeleportToRespawnOfPacket::new);
        register(SetCameraOfPacket::new);
        register(ComponentPacket::new);
        register(TestPacket::new);
    }

    public static <MSG extends IPacket> void register(Supplier<MSG> ps) {
        var p = ps.get();
        INSTANCE.registerMessage(i++, (Class<MSG>) p.getClass(), IPacket::encode, (buf) -> {
            p.decode(buf);
            return p;
        }, IPacket::handle);
    }
}
