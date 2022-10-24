package nin.spiritualism.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FlySpeedPacket implements IPacket {
    private float f;

    public FlySpeedPacket(FriendlyByteBuf buf) {
        this(buf.readFloat());
    }

    public FlySpeedPacket(float f) {
        this.f = f;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeFloat(f);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> Minecraft.getInstance().player.getAbilities().setFlyingSpeed(f));
        context.get().setPacketHandled(true);
    }
}
