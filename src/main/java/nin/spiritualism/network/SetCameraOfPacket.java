package nin.spiritualism.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SetCameraOfPacket extends UUIDPacket {

    public SetCameraOfPacket(UUID p) {
        super(p);
    }

    public SetCameraOfPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        if (context.get().getDirection() == NetworkDirection.PLAY_TO_SERVER)
            context.get().enqueueWork(() -> {
                var sp = context.get().getSender();
                if (sp.getCamera() != sp && sp.getCamera() instanceof ServerPlayer oc)
                    new SpiritLeftPacket(sp.getName()).toClient(oc);
                var np = sp.server.getPlayerList().getPlayer(uuid);
                sp.setCamera(np);
                new SpiritPossessedPacket(sp.getName()).toClient(np);
            });
        context.get().setPacketHandled(true);
    }
}
