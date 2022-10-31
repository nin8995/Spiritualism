package nin.spiritualism.ability;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import nin.spiritualism.SpiritualismConfig;
import nin.spiritualism.capability.SpiritHandler;
import nin.spiritualism.util.ParticleUtils;

import java.util.stream.IntStream;

public class SpectralReflection {

    @SubscribeEvent
    public void onEntityDamaged(LivingHurtEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp && event.getSource().getEntity() != null)
            SpiritHandler.readOnServer(sp, sh -> {
                if (sh.hasExtraSouls() && sh.sa.equals(SpiritAbility.SPECTRAL_REFLECTION.getId()) && event.getSource().getEntity() instanceof LivingEntity enemy && !(event.getSource() instanceof ReflectionDamageSource)) {
                    var rds = new ReflectionDamageSource(event.getSource(), enemy, sp);
                    enemy.hurt(sh.isFullPower() ? rds.bypassArmor().bypassInvul() : rds, event.getAmount() * sh.getExtraSouls() / SpiritualismConfig.defaultSoulUsage);
                    IntStream.range(0, (int) (16 * sh.getSoulRate())).forEach(i -> {
                        var circle = new Vec3(0.5, 0, 0).yRot((float) (2 * Math.PI * i / 8));
                        var v = new Vec3(0, 0.05, 0);
                        ParticleUtils.addWithServerEntity(enemy, ParticleTypes.SOUL_FIRE_FLAME, circle.scale(2), v.scale(2.2).add(circle.scale(-0.05)));
                        ParticleUtils.addWithServerEntity(enemy, sh.isFullPower() ? ParticleTypes.SCULK_SOUL : ParticleTypes.SOUL, circle, v);
                        ParticleUtils.addWithServerEntity(enemy, ParticleTypes.SOUL_FIRE_FLAME, circle.scale(0.5), v.scale(1.7));
                    });
                }
            });
    }
}
