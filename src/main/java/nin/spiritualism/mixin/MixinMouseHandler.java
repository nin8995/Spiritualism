package nin.spiritualism.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.player.Abilities;
import nin.spiritualism.capability.SpiritHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MouseHandler.class)
public class MixinMouseHandler {

    @ModifyArg(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Abilities;setFlyingSpeed(F)V"))
    public float injected(float f) {
        var p = Minecraft.getInstance().player;
        if (p != null && SpiritHandler.getFromClient(p.getUUID()).isSpirit(p))
            return 0F;
        return f;
    }
}
