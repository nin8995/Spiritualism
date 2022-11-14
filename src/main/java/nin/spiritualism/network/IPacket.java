package nin.spiritualism.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public interface IPacket {
    void encode(FriendlyByteBuf buf);

    void decode(FriendlyByteBuf buf);

    void handle(Supplier<NetworkEvent.Context> context);

    default void toClient(ServerPlayer sp) {
        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> sp), this);
    }

    default void toClients(PlayerList sps) {
        sps.getPlayers().forEach(sp -> NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> sp), this));
    }

    default void toServer() {
        NetworkHandler.INSTANCE.sendToServer(this);
    }
}
