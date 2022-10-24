package nin.spiritualism.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public interface IPacket {
    void encode(FriendlyByteBuf buf);

    void handle(Supplier<NetworkEvent.Context> context);


    default void syncToClient(ServerPlayer sp) {
        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> sp), this);
    }

    default void syncToServer() {
        NetworkHandler.INSTANCE.sendToServer(this);
    }
}
