package nin.spiritualism.mixin;

import net.minecraft.client.model.Model;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Model.class)
public class MixinTest {
    /*@ModifyVariable(method = "<init>", at=@At("HEAD"),index = 1,argsOnly = true)
    private static Function<ResourceLocation, RenderType> test(Function<ResourceLocation, RenderType> p_103110_){
        return RenderType::entityTranslucent;
    }*/
}
