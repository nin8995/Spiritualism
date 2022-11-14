package nin.spiritualism.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkEvent;
import nin.spiritualism.SpiritualismConfig;
import nin.spiritualism.ability.SpiritAbility;
import nin.spiritualism.network.AbstractCapabilityPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class SpiritHandler implements INBTSerializable<CompoundTag> {
    public int soulPower = SpiritualismConfig.soulsPerDay;
    public int soulUsage = 1;
    public boolean refusePossession = false;
    public GameType previousGameType = GameType.DEFAULT_MODE;
    public boolean isDead = false;
    public float previousFlyingSpeed = 0.05F;
    public ResourceKey<Level> previousRespawnDimension = Level.OVERWORLD;
    public BlockPos previousRespawnPosition = BlockPos.ZERO;
    public String sa = SpiritAbility.SPECTRAL_REFLECTION.getId();

    public static final Capability<SpiritHandler> SPIRIT = CapabilityManager.get(new CapabilityToken<>() {
    });
    private static final Map<UUID, SpiritHandler> playersSynced = new HashMap<>();

    public SpiritHandler() {
    }

    public SpiritHandler(CompoundTag nbt) {
        deserializeNBT(nbt);
    }

    public static LazyOptional<SpiritHandler> getFromServer(Player p) {
        return p.getCapability(SPIRIT);
    }

    public static SpiritHandler getFromClient(UUID p) {
        return playersSynced.get(p);
    }

    public static void readOnServer(ServerPlayer p, NonNullConsumer<SpiritHandler> consumer) {
        getFromServer(p).ifPresent(consumer);
    }

    public static void edit(ServerPlayer spMe, NonNullConsumer<SpiritHandler> consumer) {
        /*AtomicInteger pau = new AtomicInteger();
        AtomicInteger au = new AtomicInteger();
        AtomicReference<String> sa = new AtomicReference<>("");
        readOnServer(spMe, sh -> pau.set(sh.getActualUsage()));*/
        readOnServer(spMe, consumer);/*
        readOnServer(spMe, sh -> {
            au.set(sh.getActualUsage());
            sa.set(sh.sa);
        });
        if(pau.get() != au.get() && sa.get().equals(SpiritAbility.SPECTRAL_REFLECTION.getId())) {
            var hr = spMe.getHealth()/spMe.getMaxHealth();
            spMe.getAttribute(Attributes.MAX_HEALTH).removeModifier(SpectralReflection.MODIFIER);
            spMe.getAttribute(Attributes.MAX_HEALTH).addTransientModifier(new AttributeModifier(SpectralReflection.MODIFIER, "Horse armor bonus", (au.get() - 1D) / SpiritualismConfig.defaultSoulUsage, AttributeModifier.Operation.MULTIPLY_TOTAL));
            spMe.setHealth(hr * spMe.getMaxHealth());
        }*/
        getFromServer(spMe).ifPresent(sh -> sh.syncToClients(spMe));
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

    public boolean isSpirit(GameType p) {
        return isDead && p == GameType.SPECTATOR;
    }

    public int getUsingSouls() {
        return Math.min(soulPower, soulUsage);
    }

    public boolean hasExtraSoul() {
        return getUsingSouls() > 1;
    }

    public int getExtraSouls() {
        return hasExtraSoul() ? getUsingSouls() - 1 : 0;
    }

    public boolean isFullPower() {
        return getUsingSouls() == SpiritualismConfig.soulsPerDay;
    }

    public float spiritRate() {
        return 1 - (float) Mth.clamp(soulPower, 0, SpiritualismConfig.soulsPerDay) / SpiritualismConfig.soulsPerDay;
    }

    public float soulRate() {
        return soulPower > 0 ? (float) getUsingSouls() / soulPower : 0;
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
        nbt.putString("spiritAbility", sa);
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
        sa = nbt.getString("spiritAbility");
    }

    public void syncToClients(ServerPlayer spMe) {
        new SpiritPacket(spMe.getUUID(), this).toClients(spMe.getServer().getPlayerList());
    }

    public void syncFromServer(ServerPlayer spMe) {
        spMe.getServer().getPlayerList().getPlayers().forEach(sp -> SpiritHandler.readOnServer(sp, sh -> new SpiritPacket(sp.getUUID(), sh).toClient(spMe)));
    }

    public static class SpiritPacket extends AbstractCapabilityPacket {
        public SpiritPacket() {

        }

        public SpiritPacket(UUID uuid, SpiritHandler sh) {
            super(uuid, sh);
        }

        @Override
        public void handle(Supplier<NetworkEvent.Context> context) {
            switch (context.get().getDirection()) {
                case PLAY_TO_SERVER ->
                        context.get().enqueueWork(() -> SpiritHandler.readOnServer(context.get().getSender(), sh -> sh.deserializeNBT(this.nbt)));
                case PLAY_TO_CLIENT ->
                        context.get().enqueueWork(() -> SpiritHandler.playersSynced.put(uuid, new SpiritHandler(nbt)));
            }
            context.get().setPacketHandled(true);
        }
    }

    public static class SpiritEventHandler {
        public static Map<UUID, SpiritHandler> playersToClone = new HashMap<>();

        @SubscribeEvent
        public void onPlayerDeath(LivingDeathEvent e) {
            if (e.getEntity() instanceof ServerPlayer sp) {
                SpiritHandler.edit(sp, sh -> {
                    sh.soulPower -= sh.getUsingSouls();
                    if (!sh.isLiving() && !sh.isDead) {
                        sh.previousRespawnDimension = sp.getRespawnDimension();
                        sh.setPreviousRespawnPosition(sp.getRespawnPosition());
                        sp.setRespawnPosition(sp.getLevel().dimension(), new BlockPos(sp.position()), 0, true, false);
                    }
                    playersToClone.put(sp.getUUID(), sh);
                });
            }
        }

        @SubscribeEvent
        public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent e) {
            if (e.getEntity() instanceof ServerPlayer sp) {
                SpiritHandler.edit(sp, sh -> {
                    if (!sh.isLiving() && !sh.isDead) {
                        sh.isDead = true;
                        sh.previousGameType = sp.gameMode.getGameModeForPlayer();
                        sh.previousFlyingSpeed = sp.getAbilities().getFlyingSpeed();
                        sp.getAbilities().setFlyingSpeed(0F);
                        sp.setGameMode(GameType.SPECTATOR);
                    }
                    if (sh.isDead) {
                        sp.getAbilities().setFlyingSpeed(0F);
                        sp.onUpdateAbilities();
                        //これがないと死ぬと動けちゃう
                        //おそらくスペクテーターで死ぬと移動速度リセットされるせい
                    }
                });
            }
        }

        @SubscribeEvent
        public void onPlayerClone(PlayerEvent.Clone e) {
            if (e.isWasDeath() && e.getOriginal() instanceof ServerPlayer osp) {
                var osh = playersToClone.get(osp.getUUID());
                if (e.getEntity() instanceof ServerPlayer nsp)
                    SpiritHandler.readOnServer(nsp, nsh -> {
                        nsh.deserializeNBT(osh.serializeNBT());
                    });
            }
        }

        @SubscribeEvent
        public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent e) {
            if (e.getEntity() instanceof ServerPlayer sp) {
                SpiritHandler.readOnServer(sp, sh -> {
                    sh.syncToClients(sp);
                    sh.syncFromServer(sp);
                    playersToClone.put(sp.getUUID(), sh);
                });
            }
        }
    }
}

