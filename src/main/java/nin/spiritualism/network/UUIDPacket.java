package nin.spiritualism.network;

import net.minecraft.network.FriendlyByteBuf;

import java.util.UUID;

public abstract class UUIDPacket implements IPacket {
    protected UUID uuid;

    public UUIDPacket() {

    }

    public UUIDPacket(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(uuid);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        this.uuid = buf.readUUID();
    }
}
