package nin.spiritualism;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import nin.spiritualism.capability.SpiritHandler;

public class TestItem extends Item {
    public TestItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level p_41432_, Player p, InteractionHand p_41434_) {
        if (p instanceof ServerPlayer sp)
            SpiritHandler.get(sp).ifPresent(sh -> {
                sh.soulPower += sp.isCrouching() ? -1 : 1;
                System.out.println(sh.soulPower);
                System.out.println(!sh.isDead ? "living" : sh.isSpirit(p) ? "spirit" : "undead");
            });
        return super.use(p_41432_, p, p_41434_);
    }
}
