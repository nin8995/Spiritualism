package nin.spiritualism.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.NetworkEvent;
import nin.spiritualism.network.IPacket;

import java.util.function.Supplier;

public abstract class SyncableCapability implements INBTSerializable<CompoundTag>, IPacket {
    public SyncableCapability() {
    }

    public SyncableCapability(CompoundTag tag) {
        deserializeNBT(tag);
    }

    public SyncableCapability(FriendlyByteBuf buf) {
        deserializeNBT(buf.readNbt());
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeNbt(serializeNBT());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> SpiritHandler.get(context.get().getSender()).ifPresent(sh -> sh.deserializeNBT(serializeNBT())));
        context.get().setPacketHandled(true);
    }
}
