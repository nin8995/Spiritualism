package nin.spiritualism.network;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.UUID;

public abstract class AbstractCapabilityPacket implements IPacket {
    public UUID uuid;
    public CompoundTag nbt;

    public AbstractCapabilityPacket() {

    }

    public AbstractCapabilityPacket(UUID uuid, INBTSerializable<CompoundTag> capability) {
        this.nbt = capability.serializeNBT();
        this.uuid = uuid;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(nbt);
        buf.writeUUID(uuid);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        this.nbt = buf.readNbt();
        this.uuid = buf.readUUID();
    }
}
