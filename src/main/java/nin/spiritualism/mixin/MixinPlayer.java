package nin.spiritualism.mixin;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import nin.spiritualism.SpiritualismConfig;
import nin.spiritualism.capability.SpiritHandler;
import nin.spiritualism.util.ColorUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class MixinPlayer extends LivingEntity {

    protected MixinPlayer(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Shadow(remap = false)
    public abstract <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction facing);

    @Shadow
    public abstract boolean isSpectator();

    @Inject(method = "wantsToStopRiding", at = @At("HEAD"), cancellable = true)
    private void injected(CallbackInfoReturnable<Boolean> cir) {
        this.getCapability(SpiritHandler.SPIRIT, null).ifPresent(sh -> {
            if (sh.isDead && this.isSpectator())
                cir.setReturnValue(false);
        });
    }

    @Inject(method = "decorateDisplayNameComponent", at = @At("RETURN"))
    private void injected(MutableComponent mc, CallbackInfoReturnable<MutableComponent> cir) {
        var color = mc.getStyle().getColor() != null ? mc.getStyle().getColor().getValue() : 0xFFFFFF;
        var spirit = SpiritualismConfig.spiritColor;
        if (this.level instanceof ServerLevel sl)
            sl.players().stream().filter(p -> p.getUUID().equals(this.getUUID())).forEach(sp -> SpiritHandler.readOnServer(sp, sh ->
                    mc.setStyle(mc.getStyle().withColor(ColorUtils.blend(color, spirit, sh.spiritRate())))));
    }
}
