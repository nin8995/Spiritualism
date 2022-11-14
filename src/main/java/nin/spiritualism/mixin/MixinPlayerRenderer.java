package nin.spiritualism.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import nin.spiritualism.client.SpiritLayer;
import nin.spiritualism.client.SpiritModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class MixinPlayerRenderer extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public MixinPlayerRenderer(EntityRendererProvider.Context p_174289_, PlayerModel<AbstractClientPlayer> p_174290_, float p_174291_) {
        super(p_174289_, p_174290_, p_174291_);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void addSpiritLayer(EntityRendererProvider.Context c, boolean isSlim, CallbackInfo ci) {
        this.addLayer(new SpiritLayer(this, c.getModelSet(), isSlim));
    }

    @Redirect(method = "renderHand", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/ModelPart;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;II)V"))
    private void translucentFPSHand(ModelPart instance, PoseStack pose, VertexConsumer buffer, int p_104304_, int p_104305_) {
        instance.render(pose, buffer, p_104304_, p_104305_, 1, 1, 1, -114514);
    }

    @Inject(method = "renderHand", at = @At("TAIL"))
    private void renderFPSSpiritLayer(PoseStack pose, MultiBufferSource mbs, int light, AbstractClientPlayer p, ModelPart arm, ModelPart sleeve, CallbackInfo ci) {
        SpiritModel.render(arm, p, pose, mbs);
    }
}
