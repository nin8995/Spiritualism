package nin.spiritualism.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Abilities;
import nin.spiritualism.capability.SpiritHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(Abilities.class)
public class MixinAbilities {

    @Inject(method = "getFlyingSpeed", at = @At("RETURN"), cancellable = true)
    public void injected(CallbackInfoReturnable<Float> cir) {
        var p = Minecraft.getInstance().player;
        if (p == null) return;
        AtomicBoolean isSpirit = new AtomicBoolean(false);
        SpiritHandler.get(p).ifPresent(sh -> isSpirit.set(sh.isSpirit(p)));
        if (isSpirit.get()) cir.setReturnValue(0F);
    }
}
