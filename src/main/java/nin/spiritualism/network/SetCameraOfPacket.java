package nin.spiritualism.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import nin.spiritualism.util.ChatUtils;

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
                    new ComponentPacket(ChatUtils.left(sp.getName().getString())).toClient(oc);
                var np = sp.server.getPlayerList().getPlayer(uuid);
                sp.setCamera(np);
                new ComponentPacket(ChatUtils.possessed(sp.getName().getString())).toClient(np);
            });
        context.get().setPacketHandled(true);
    }
}
