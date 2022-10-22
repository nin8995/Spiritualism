package nin.spiritualism;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.*;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Spiritualism.MODID)
public class Spiritualism {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "spiritualism";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "spiritualism" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "spiritualism" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    // Creates a new Block with the id "spiritualism:example_block", combining the namespace and path
    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new Block(BlockBehaviour.Properties.of(Material.STONE)));
    // Creates a new BlockItem with the id "spiritualism:example_block", combining the namespace and path
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));
    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () -> new TestItem(new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));

    public Spiritualism() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }

    public static final Capability<SpiritHandler> SPIRIT = CapabilityManager.get(new CapabilityToken<>() {
    });

    @SubscribeEvent
    public void registerCaps(RegisterCapabilitiesEvent event) {
        event.register(SpiritHandler.class);
    }


    @SubscribeEvent
    public void onPlayerDeath(LivingDeathEvent e) {
        if (e.getEntity() instanceof ServerPlayer sp) {
            getSpirit(sp).ifPresent(sh -> {
                sh.soulPower -= sh.getActualUsage();
                if (sh.isDead() && !sp.isSpectator()) {
                    sh.previousGameType = sp.gameMode.getGameModeForPlayer();
                    sp.setGameMode(GameType.SPECTATOR);
                    sh.previousRespawnDimension = sp.getRespawnDimension();
                    sh.previousRespawnPosition = sp.getRespawnPosition();
                    sp.setRespawnPosition(sp.getLevel().dimension(), new BlockPos(sp.position()), 0, true, false);
                }
            });
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone e) {
        getSpirit((ServerPlayer) e.getOriginal()).ifPresent(op ->
            e.getEntity().getCapability(Spiritualism.SPIRIT).ifPresent(np -> {
                np.deserializeNBT(op.serializeNBT());
                players.put(e.getEntity().getUUID(), LazyOptional.of(() -> np));
            })
        );
    }

    @SubscribeEvent
    public void onRegisterCommandEvent(RegisterCommandsEvent event) {
        var dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("resurrect").executes(context -> {
            if (context.getSource().getEntity() instanceof ServerPlayer sp)
                getSpirit(sp).ifPresent(sh -> {
                    if(sh.isLiving())
                        return;
                    sh.soulPower += SpiritualismConfig.soulDivision;
                    if (sh.isLiving()) {
                        sp.setGameMode(sh.previousGameType);
                        sp.setRespawnPosition(sh.previousRespawnDimension, sh.previousRespawnPosition, 0, false, false);
                        /*if(!sp.getLevel().isClientSide)
                            sp.getServer().getPlayerList().respawn(sp, false);*/
                    }
                });
            return 1;
        }));
    }

    private static Map<UUID, LazyOptional<SpiritHandler>> players = new HashMap<>();

    public static LazyOptional<SpiritHandler> getSpirit(ServerPlayer sp) {
        if (!players.containsKey(sp.getUUID()))
            players.put(sp.getUUID(), sp.getCapability(SPIRIT));
        return players.get(sp.getUUID());
    }

    @SubscribeEvent
    public void onAttachingCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof ServerPlayer)
            event.addCapability(new ResourceLocation("spiritualism", "spirit_handler"), createProvider(SPIRIT, new SpiritHandler()));
    }

    public static ICapabilitySerializable<CompoundTag> createProvider(Capability<? extends INBTSerializable<CompoundTag>> cap, INBTSerializable<CompoundTag> backend) {
        return new ICapabilitySerializable<>() {
            @Override
            public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capIn, @Nullable Direction direction) {
                if (capIn == cap) {
                    return LazyOptional.of(() -> backend).cast();
                }
                return LazyOptional.empty();
            }

            @Override
            public CompoundTag serializeNBT() {
                return backend.serializeNBT();
            }

            @Override
            public void deserializeNBT(CompoundTag tag) {
                backend.deserializeNBT(tag);
            }
        };
    }
}
