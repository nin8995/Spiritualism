package nin.spiritualism;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import nin.spiritualism.capability.SpiritHandler;
import nin.spiritualism.network.ComponentPacket;

public class TestItem extends Item {
    public static int i;

    public TestItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player p, InteractionHand p_41434_) {
        if (p instanceof ServerPlayer sp)
            SpiritHandler.edit(sp, sh -> {
                sh.soulPower += sp.isCrouching() ? -1 : 1;
                var status = sh.isLiving() ? "living" : sh.isSpirit(p) ? "spirit" : "undead";
                new ComponentPacket(sh.soulPower + "\n" + sh.getActualUsage() + "/" + sh.soulUsage + "\n" + sh.refusePossession + "\n" + status).toClient(sp);
            });
        return super.use(level, p, p_41434_);
    }
}
