package nin.spiritualism;

import com.google.common.base.CaseFormat;
import com.google.common.base.Supplier;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import nin.spiritualism.capability.SpiritHandler;
import nin.spiritualism.command.AbstractCommand;
import nin.spiritualism.command.RefusePossessionCommand;
import nin.spiritualism.command.ResurrectCommand;
import nin.spiritualism.command.SoulUsageCommand;
import nin.spiritualism.network.NetworkHandler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mod(Spiritualism.MODID)
public class Spiritualism {
    public static final String MODID = "spiritualism";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final RegistryObject<Block> EXAMPLE_BLOCK = BLOCKS.register("example_block", () -> new Block(BlockBehaviour.Properties.of(Material.STONE)));
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> EXAMPLE_BLOCK_ITEM = ITEMS.register("example_block", () -> new BlockItem(EXAMPLE_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));
    public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item", () -> new TestItem(new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));
    private static final List<TickingFunction> tickingFunctions = new ArrayList<>();
    private static final List<TickingFunction> deposedTickingFunctions = new ArrayList<>();

    public Spiritualism() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new SpiritHandler.SpiritEventHandler());
    }

    public static <T extends INBTSerializable<CompoundTag>> void addCapability(AttachCapabilitiesEvent<Entity> event, Capability<T> cap, T backend) {
        event.addCapability(new ResourceLocation(Spiritualism.MODID, CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, backend.getClass().getSimpleName())), createProvider(cap, backend));
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

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
        event.enqueueWork(NetworkHandler::init);
    }

    @SubscribeEvent
    public void registerCaps(RegisterCapabilitiesEvent event) {
        event.register(SpiritHandler.class);
    }

    @SubscribeEvent
    public void attachCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof ServerPlayer)
            addCapability(event, SpiritHandler.SPIRIT, new SpiritHandler());
    }

    @SubscribeEvent
    public void onRegisterCommandEvent(RegisterCommandsEvent event) {
        var dispatcher = event.getDispatcher();
        var commands = new ArrayList<Supplier<? extends AbstractCommand>>();
        commands.add(ResurrectCommand::new);
        commands.add(RefusePossessionCommand::new);
        commands.add(SoulUsageCommand::new);
        commands.forEach(c -> dispatcher.register(c.get().register()));
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent e) {
        if (e.phase == TickEvent.Phase.START) {
            tickingFunctions.forEach(TickingFunction::tick);
            tickingFunctions.removeAll(deposedTickingFunctions);
            deposedTickingFunctions.clear();
        }
    }

    @Mod.EventBusSubscriber(modid = Spiritualism.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            Spiritualism.LOGGER.info("HELLO FROM CLIENT SETUP");
            Spiritualism.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }

    public static abstract class TickingFunction {
        protected int currentTicks;
        protected int requiredTicks;

        public TickingFunction(int requiredTicks) {
            this.requiredTicks = requiredTicks;
        }

        public void start() {
            tickingFunctions.add(this);
        }

        public void tick() {
            if (!isValid()) {
                depose();
            }
            currentTicks++;
            if (currentTicks >= requiredTicks) {
                function();
                depose();
            }
        }

        public void depose() {
            deposedTickingFunctions.add(this);
        }

        public abstract boolean isValid();

        public abstract void function();
    }
}
