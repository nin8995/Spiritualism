package nin.spiritualism.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;
import nin.spiritualism.utils.ChatUtils;

import java.util.function.Supplier;

public class SpiritLeftPacket extends ComponentPacket {
    public SpiritLeftPacket(Component c) {
        super(c);
    }

    public SpiritLeftPacket(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> ChatUtils.showLeft(c));
        context.get().setPacketHandled(true);
    }
}
