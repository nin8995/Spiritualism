package nin.spiritualism.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.spectator.PlayerMenuItem;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import nin.spiritualism.capability.SpiritHandler;
import nin.spiritualism.network.SetCameraOfPacket;
import nin.spiritualism.network.TeleportToRespawnOfPacket;
import nin.spiritualism.utils.ChatUtils;
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
        var shMe = SpiritHandler.getFromClient(Minecraft.getInstance().player.getUUID());
        var sh = SpiritHandler.getFromClient(this.profile.getId());
        if (!shMe.isDead)
            return;
        if (sh.refusePossession) {
            ChatUtils.showComponent(ChatUtils.refused(profile.getName()));
            ci.cancel();
            return;
        }
        if (sh.isDead) {
            new TeleportToRespawnOfPacket(this.profile.getId()).toServer();
            ci.cancel();
            return;
        }
        new SetCameraOfPacket(this.profile.getId()).toServer();
        ci.cancel();
    }
}
