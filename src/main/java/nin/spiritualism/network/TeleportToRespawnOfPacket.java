package nin.spiritualism.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import nin.spiritualism.util.PlayerUtils;

import java.util.UUID;
import java.util.function.Supplier;

public class TeleportToRespawnOfPacket extends UUIDPacket {

    public TeleportToRespawnOfPacket(UUID uuid) {
        super(uuid);
    }

    public TeleportToRespawnOfPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        if (context.get().getDirection() == NetworkDirection.PLAY_TO_SERVER)
            context.get().enqueueWork(() -> PlayerUtils.teleportToRespawn(context.get().getSender(), context.get().getSender().server.getPlayerList().getPlayer(uuid)));
        context.get().setPacketHandled(true);
    }
}
