package nin.spiritualism.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;
import nin.spiritualism.util.ChatUtils;

import java.util.function.Supplier;

public class ComponentPacket implements IPacket {
    protected Component c;

    public ComponentPacket(String s) {
        this.c = Component.literal(s);
    }

    public ComponentPacket(Component c) {
        this.c = c;
    }

    public ComponentPacket(FriendlyByteBuf buf) {
        this.c = buf.readComponent();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeComponent(c);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> ChatUtils.showComponent(c));
        context.get().setPacketHandled(true);
    }
}
