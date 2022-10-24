package nin.spiritualism.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import nin.spiritualism.SpiritualismConfig;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpiritHandler extends SyncableCapability {
    public static final Capability<SpiritHandler> SPIRIT = CapabilityManager.get(new CapabilityToken<>() {
    });

    public int soulPower = SpiritualismConfig.soulDivision;
    public int soulUsage = SpiritualismConfig.defaultSoulUsage;
    public boolean refusePossession = false;
    public GameType previousGameType = GameType.DEFAULT_MODE;
    public boolean isDead = false;
    public float previousFlyingSpeed = 0.05F;
    public ResourceKey<Level> previousRespawnDimension = Level.OVERWORLD;
    public BlockPos previousRespawnPosition = BlockPos.ZERO;

    public SpiritHandler() {
        super();
    }

    public SpiritHandler(CompoundTag tag) {
        super(tag);
    }

    public SpiritHandler(FriendlyByteBuf buf) {
        super(buf);
    }

    public BlockPos getPreviousRespawnPosition() {
        return previousRespawnPosition != BlockPos.ZERO ? previousRespawnPosition : null;
    }

    public void setPreviousRespawnPosition(BlockPos pos) {
        previousRespawnPosition = pos != null ? pos : BlockPos.ZERO;
    }

    public boolean isLiving() {
        return soulPower > 0;
    }

    public boolean isSpirit(Player p) {
        return isDead && p.isSpectator();
    }

    public int getActualUsage() {
        return Math.min(soulPower, soulUsage);
    }

    @Override
    public CompoundTag serializeNBT() {
        var nbt = new CompoundTag();
        nbt.putInt("soulPower", soulPower);
        nbt.putInt("soulUsage", soulUsage);
        nbt.putBoolean("refusePossession", refusePossession);
        nbt.putBoolean("isDead", isDead);
        nbt.putFloat("previousFlyingSpeed", previousFlyingSpeed);
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
        isDead = nbt.getBoolean("isDead");
        previousFlyingSpeed = nbt.getFloat("previousFlyingSpeed");
        previousGameType = GameType.byId(nbt.getInt("previousGameType"));
        previousRespawnDimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(nbt.getString("previousRespawnDimension")));
        var pos = (CompoundTag) nbt.get("previousRespawnPosition");
        previousRespawnPosition = new BlockPos(pos.getInt("x"), pos.getInt("y"), pos.getInt("z"));
    }

    public static Map<UUID, LazyOptional<SpiritHandler>> players = new HashMap<>();

    public static LazyOptional<SpiritHandler> get(Player p) {
        if (!players.containsKey(p.getUUID()))
            players.put(p.getUUID(), p.getCapability(SPIRIT));
        return players.get(p.getUUID());
    }
}
