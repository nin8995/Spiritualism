package nin.spiritualism.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TestPacket implements IPacket {
    float scale;
    float x;
    float y;
    public static float ss = 1;
    public static float xs = 1;
    public static float ys = 1;

    public TestPacket() {

    }

    public TestPacket(float scale, float x, float y) {
        this.scale = scale;
        this.x = x;
        this.y = y;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeFloat(scale);
        buf.writeFloat(x);
        buf.writeFloat(y);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        scale = buf.readFloat();
        x = buf.readFloat();
        y = buf.readFloat();
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        if (context.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT)
            context.get().enqueueWork(this::test);
        context.get().setPacketHandled(true);
    }

    public void test() {
        ss = scale;
        xs = x;
        ys = y;
    }
}
