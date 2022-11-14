package nin.spiritualism.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;

public class SpiritLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private final SpiritModel model;

    public SpiritLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> p, EntityModelSet p_174555_, boolean isSlim) {
        super(p);
        this.model = new SpiritModel(p_174555_.bakeLayer(ModelLayers.PLAYER), isSlim);
    }


    @Override
    public void render(PoseStack pose, MultiBufferSource mbs, int light, AbstractClientPlayer p, float anim1, float anim2, float partialTick, float bob, float yRot, float xRot) {
/*
        if (SpiritShader.item == null)
            try {
                var mc = Minecraft.getInstance();
                var postchain = new PostChain(mc.getTextureManager(), mc.getResourceManager(), mc.getMainRenderTarget(), new ResourceLocation("shaders/post/transparency.json"));
                postchain.resize(mc.getWindow().getWidth(), mc.getWindow().getHeight());
                SpiritShader.item = postchain.getTempTarget("itemEntity");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        SpiritShader.item.clear(Minecraft.ON_OSX);
        SpiritShader.item.copyDepthFrom(Minecraft.getInstance().getMainRenderTarget());
        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
       */
        model.prepareMobModel(p, anim1, anim2, partialTick);
        this.getParentModel().copyPropertiesTo(model);
        model.setupAnim(p, anim1, anim2, bob, yRot, xRot);
        model.render(p, pose, mbs);
    }
}
