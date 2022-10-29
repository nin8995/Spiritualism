package nin.spiritualism.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import nin.spiritualism.SpiritualismConfig;
import nin.spiritualism.capability.SpiritHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class MixinPlayer {

    @Shadow
    public abstract <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing);

    @Shadow
    public abstract boolean isSpectator();

    @Shadow public abstract GameProfile getGameProfile();

    @Inject(method = "wantsToStopRiding", at = @At("HEAD"), cancellable = true)
    private void injected(CallbackInfoReturnable<Boolean> cir) {
        this.getCapability(SpiritHandler.SPIRIT, null).ifPresent(sh -> {
            if (sh.isDead && this.isSpectator())
                cir.setReturnValue(false);
        });
    }

    @ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private  float injected1(float f){
        var sh = SpiritHandler.getFromClient(this.getGameProfile().getId());
        return f * sh.getActualUsage() / SpiritualismConfig.defaultSoulUsage;
    }

    @ModifyArg(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private float injected2(float f){
        var sh = SpiritHandler.getFromClient(this.getGameProfile().getId());
        return f * sh.getActualUsage() / SpiritualismConfig.defaultSoulUsage;
    }
}
