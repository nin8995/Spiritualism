package nin.spiritualism.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;
import nin.spiritualism.utils.ChatUtils;

import java.util.function.Supplier;

public class SpiritPossessedPacket extends ComponentPacket {
    public SpiritPossessedPacket(Component c) {
        super(c);
    }

    public SpiritPossessedPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> ChatUtils.showPossessed(c));
        context.get().setPacketHandled(true);
    }
}
