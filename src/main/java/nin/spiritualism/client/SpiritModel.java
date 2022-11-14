package nin.spiritualism.client;

import com.google.common.collect.Iterables;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import nin.spiritualism.capability.SpiritHandler;

import java.util.stream.IntStream;

public class SpiritModel extends PlayerModel<AbstractClientPlayer> {
    public static final ResourceLocation SPIRIT_LAYER_LOCATION = new ResourceLocation("spiritualism", "textures/spirit_layer.png");

    public SpiritModel(ModelPart p_170821_, boolean p_170822_) {
        super(p_170821_, p_170822_);
    }

    public static VertexConsumer toBuffer(MultiBufferSource mbs) {
        return mbs.getBuffer(SpiritShader.SPIRIT_LAYER.apply(SPIRIT_LAYER_LOCATION));
    }

    public void render(AbstractClientPlayer p, PoseStack pose, MultiBufferSource mbs) {
        render(p, pose, toBuffer(mbs));
    }

    public void render(AbstractClientPlayer p, PoseStack pose, VertexConsumer vc) {
        Iterables.concat(this.bodyParts(), this.headParts()).forEach((m) -> {
            if (m == head || m == body || m == leftArm || m == rightArm || m == leftLeg || m == rightLeg)
                render(m, p, pose, vc);
        });
    }

    public static void render(ModelPart m, AbstractClientPlayer p, PoseStack pose, MultiBufferSource mbs) {
        render(m, p, pose, toBuffer(mbs));
    }

    public static void render(ModelPart m, AbstractClientPlayer p, PoseStack pose, VertexConsumer vc) {
        var max = 8;
        var sh = SpiritHandler.getFromClient(p.getUUID());
        IntStream.range(0, max).forEach(i ->
                new ScalableRenderer(m, 1 + (float) i / (max * 2), pose, vc, LightTexture.pack(15, 15), OverlayTexture.NO_OVERLAY, 1, 1, 1, Mth.clamp((Math.max(sh.spiritRate(), sh.soulRate()) - (float) i / max) / (max + 1), 0, 1)).render());
    }

    public static class ScalableRenderer {
        ModelPart m;
        float scale;
        PoseStack pose;
        VertexConsumer vc;
        int light;
        int overlay;
        float r;
        float g;
        float b;
        float a;

        public ScalableRenderer(ModelPart m, float scale, PoseStack pose, VertexConsumer vc, int light, int overlay, float r, float g, float b, float a) {
            this.m = m;
            this.scale = scale;
            this.pose = pose;
            this.vc = vc;
            this.light = light;
            this.overlay = overlay;
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }

        public void render() {

            var cube = m.getRandomCube(new LegacyRandomSource(0));
            pose.pushPose();

            //ideal way except it expand arm when fps spirit layer rendered
            /*m.xScale=scale;
            m.yScale=scale;
            m.zScale=scale;*/
            //pose.mulPoseMatrix(Matrix4f.createTranslateMatrix((scale - 1) * (cube.maxX + cube.minX) / (-2F), (scale - 1) * (cube.maxY + cube.minY) / (32F), (scale - 1) * (cube.maxZ + cube.minZ) / (-2F)));

            //idk why this works with these TestPacket's fields
            pose.translate((scale - 1) * (cube.maxX + cube.minX) / (2 * -2), (scale - 1) * (cube.maxY + cube.minY) / (2 * -6), (scale - 1) * (cube.maxZ + cube.minZ) / (2 * 1));
            //pose.translate((scale - 1) * (cube.maxX + cube.minX) / (2*TestPacket.ss), (scale - 1) * (cube.maxY + cube.minY) / (2*TestPacket.xs), (scale - 1) * (cube.maxZ + cube.minZ) / (2*TestPacket.ys));

            pose.scale(scale, scale, scale);

            m.render(pose, vc, light, overlay, r, g, b, a);
            pose.popPose();
        }
    }
}
