package nin.spiritualism;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;

public class SpiritHandler implements INBTSerializable<CompoundTag> {
    public int soulPower = SpiritualismConfig.soulDivision;
    public int soulUsage = SpiritualismConfig.defaultSoulUsage;
    public boolean refusePossession = false;
    public GameType previousGameType = GameType.DEFAULT_MODE;
    public BlockPos previousRespawnPosition = BlockPos.ZERO;
    public ResourceKey<Level> previousRespawnDimension = Level.OVERWORLD;
    public ServerPlayer pl;

    SpiritHandler(ServerPlayer pl) {
        this.pl = pl;
    }

    public boolean isUndead() {
        return soulPower <= 0 && !pl.isSpectator();
    }

    public boolean isSpirit() {
        return soulPower <= 0 && pl.isSpectator();
    }

    @Override
    public CompoundTag serializeNBT() {
        var nbt = new CompoundTag();
        nbt.putInt("soulPower", soulPower);
        nbt.putInt("soulUsage", soulUsage);
        nbt.putBoolean("refusePossession", refusePossession);
        nbt.putInt("previousGameType", previousGameType.getId());
        nbt.putString("previousRespawnDimension", previousRespawnDimension.location().toString());
        var pos = new CompoundTag();
        pos.putInt("x", previousRespawnPosition.getX());
        pos.putInt("y", previousRespawnPosition.getY());
        pos.putInt("z", previousRespawnPosition.getZ());
        nbt.put("previousRespawnPosition", pos);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        soulPower = nbt.getInt("soulPower");
        soulUsage = nbt.getInt("soulUsage");
        refusePossession = nbt.getBoolean("refusePossession");
        previousGameType = GameType.byId(nbt.getInt("previousGameType"));
        previousRespawnDimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(nbt.getString("previousRespawnDimension")));
        var pos = (CompoundTag) nbt.get("previousRespawnPosition");
        previousRespawnPosition = new BlockPos(pos.getInt("x"), pos.getInt("y"), pos.getInt("z"));
    }
}
