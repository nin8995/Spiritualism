package nin.spiritualism.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public abstract class SpiritShader extends RenderStateShard {
    /*public static RenderTarget item;
    protected static final RenderStateShard.OutputStateShard ITEM_ENTITY_TARGETs = new RenderStateShard.OutputStateShard("item_entity_targets", () -> {
        item.bindWrite(false);
    }, () -> {
        Minecraft.getInstance().getMainRenderTarget().bindWrite(false);
    });*/

    public SpiritShader(String p_110161_, Runnable p_110162_, Runnable p_110163_) {
        super(p_110161_, p_110162_, p_110163_);
    }

    public static final Function<ResourceLocation, RenderType> SPIRIT_LAYER = Util.memoize((rl) ->
            RenderType.create("spirit_layer", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, false, RenderType.CompositeState.builder()
                    /*.setShaderState(new ShaderStateShard(ForgeHooksClient.ClientEvents::getEntityTranslucentUnlitShader))*/
                    .setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
                    .setTextureState(new RenderStateShard.TextureStateShard(rl, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setDepthTestState(NO_DEPTH_TEST)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(false)));
}
