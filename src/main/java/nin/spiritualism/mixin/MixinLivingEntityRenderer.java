package nin.spiritualism.mixin;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import nin.spiritualism.capability.SpiritHandler;
import nin.spiritualism.client.SpiritLayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(LivingEntityRenderer.class)
public class MixinLivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> {

    @Shadow
    @Final
    protected List<RenderLayer<T, M>> layers;
    private static float spiritRate;
    private static boolean isSpirit;

    @ModifyArg(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getOverlayCoords(Lnet/minecraft/world/entity/LivingEntity;F)I"))
    private LivingEntity getSpiritRate(LivingEntity e) {
        if (e instanceof Player p)
            spiritRate = SpiritHandler.getFromClient(p.getUUID()).spiritRate();
        else
            spiritRate = 0;
        return e;
    }

    @ModifyArg(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/EntityModel;renderToBuffer(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"), index = 7)
    private float translucentPlayer(float par5) {
        return 1 - spiritRate;
    }

    @Redirect(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;isSpectator()Z"))
    private boolean getIsSpirit(T e) {
        if (e instanceof AbstractClientPlayer p)
            if (SpiritHandler.getFromClient(p.getUUID()).isSpirit(p)) {
                isSpirit = true;
                return false;
            }
        return e.isSpectator();
    }

    @Redirect(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;layers:Ljava/util/List;"))
    private List<RenderLayer<T, M>> renderSpiritLayerWhenSpirit(LivingEntityRenderer instance) {
        if (isSpirit) {
            isSpirit = false;
            return layers.stream().filter(l -> l instanceof SpiritLayer).toList();
        }
        return layers;
    }
}
