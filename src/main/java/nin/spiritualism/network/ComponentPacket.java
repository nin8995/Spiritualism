package nin.spiritualism.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

public abstract class ComponentPacket implements IPacket {
    protected Component c;


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
}
