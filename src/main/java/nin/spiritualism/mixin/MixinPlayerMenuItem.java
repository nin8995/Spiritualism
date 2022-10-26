package nin.spiritualism.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.gui.spectator.PlayerMenuItem;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import nin.spiritualism.capability.SpiritHandler;
import nin.spiritualism.network.TeleportToRespawnOfPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerMenuItem.class)
public class MixinPlayerMenuItem {

    @Shadow
    @Final
    private GameProfile profile;

    @Inject(method = "selectItem", at = @At("HEAD"), cancellable = true)
    private void injected(SpectatorMenu p_101762_, CallbackInfo ci) {
        if (SpiritHandler.getFromClient(this.profile.getId()).isDead) {
            new TeleportToRespawnOfPacket(this.profile.getId()).toServer();
            ci.cancel();
        }
    }
}
