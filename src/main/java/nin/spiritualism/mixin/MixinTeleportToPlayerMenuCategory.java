package nin.spiritualism.mixin;

import com.google.common.collect.Ordering;
import net.minecraft.client.gui.spectator.PlayerMenuItem;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;
import net.minecraft.client.gui.spectator.categories.TeleportToPlayerMenuCategory;
import net.minecraft.client.multiplayer.PlayerInfo;
import nin.spiritualism.capability.SpiritHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.List;

@Mixin(TeleportToPlayerMenuCategory.class)
public class MixinTeleportToPlayerMenuCategory {

    @Shadow
    @Final
    private static Ordering<PlayerInfo> PROFILE_ORDER;

    @Shadow
    @Final
    private List<SpectatorMenuItem> items;

    @Inject(method = "<init>(Ljava/util/Collection;)V", at = @At("TAIL"))
    public void spiritCanTeleportToDeathPoint(Collection<PlayerInfo> pis, CallbackInfo ci) {
        PROFILE_ORDER.sortedCopy(pis).stream().filter(p -> SpiritHandler.getFromClient(p.getProfile().getId()).isSpirit(p.getGameMode()))
                .forEach(p -> items.add(new PlayerMenuItem(p.getProfile())));
    }
}
