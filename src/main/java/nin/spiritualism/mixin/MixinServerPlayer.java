package nin.spiritualism.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import nin.spiritualism.capability.SpiritHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayer.class)
public class MixinServerPlayer {

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;setCamera(Lnet/minecraft/world/entity/Entity;)V"))
    private void injected(ServerPlayer sp, Entity e) {
        SpiritHandler.getFromServer(sp).ifPresent(sh -> {
            if (!sh.isDead)
                sp.setCamera(e);
        });
    }
}
