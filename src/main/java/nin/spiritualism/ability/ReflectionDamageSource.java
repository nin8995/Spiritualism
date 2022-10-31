package nin.spiritualism.ability;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ReflectionDamageSource extends IndirectEntityDamageSource {
    public ReflectionDamageSource(DamageSource d, Entity p_19395_, Player pl) {
        super(d.getMsgId(), p_19395_, pl);
        if (d.isProjectile()) setProjectile();
        if (d.isExplosion()) setExplosion();
        if (d.isBypassArmor()) bypassArmor();
        if (d.isDamageHelmet()) damageHelmet();
        if (d.isBypassInvul()) bypassInvul();
        if (d.isBypassMagic()) bypassMagic();
        if (d.isBypassEnchantments()) bypassEnchantments();
        if (d.isFire()) setIsFire();
        if (d.isNoAggro()) setNoAggro();
        if (d.scalesWithDifficulty()) setScalesWithDifficulty();
        if (d.isMagic()) setMagic();
        if (d.isFall()) setIsFall();
        if (d instanceof EntityDamageSource ed && ed.isThorns()) setThorns();
    }

    @Override
    public Component getLocalizedDeathMessage(LivingEntity p_19410_) {
        Component component = getEntity() == null ? getDirectEntity().getDisplayName() : Component.literal(getEntity().getDisplayName().getString() + "の魂");
        ItemStack itemstack = getEntity() instanceof LivingEntity ? ((LivingEntity) getEntity()).getMainHandItem() : ItemStack.EMPTY;
        String s = "death.attack." + this.msgId;
        String s1 = s + ".item";
        return !itemstack.isEmpty() && itemstack.hasCustomHoverName() ? Component.translatable(s1, p_19410_.getDisplayName(), component, itemstack.getDisplayName()) : Component.translatable(s, p_19410_.getDisplayName(), component);
    }
}
