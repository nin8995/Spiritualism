package nin.spiritualism.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import nin.spiritualism.utils.PlayerUtils;

import java.util.UUID;
import java.util.function.Supplier;

public class TeleportToRespawnOfPacket implements IPacket {

    private final UUID uuid;

    public TeleportToRespawnOfPacket(UUID uuid) {
        this.uuid = uuid;
    }

    public TeleportToRespawnOfPacket(FriendlyByteBuf buf) {
        uuid = buf.readUUID();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(uuid);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        if (context.get().getDirection() == NetworkDirection.PLAY_TO_SERVER)
            context.get().enqueueWork(() -> PlayerUtils.teleportToRespawn(context.get().getSender(), context.get().getSender().server.getPlayerList().getPlayer(uuid)));
        context.get().setPacketHandled(true);
    }
}
