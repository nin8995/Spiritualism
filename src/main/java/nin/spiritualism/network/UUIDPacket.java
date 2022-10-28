package nin.spiritualism.network;

import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public abstract class UUIDPacket implements IPacket {
    protected UUID uuid;


    public UUIDPacket(UUID uuid) {
        this.uuid = uuid;
    }

    public UUIDPacket(FriendlyByteBuf buf) {
        this.uuid = buf.readUUID();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(uuid);
    }

}
