package nin.spiritualism.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import nin.spiritualism.Spiritualism;
import nin.spiritualism.SpiritualismConfig;
import nin.spiritualism.capability.SpiritHandler;
import nin.spiritualism.network.ComponentPacket;
import nin.spiritualism.utils.ChatUtils;

public class SoulUsageCommand extends AbstractCommand {

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("soulUsage").executes(SoulUsageCommand::showSoulUsage)
                .then(Commands.argument("soulUsage", IntegerArgumentType.integer(0, SpiritualismConfig.soulDivision)).executes(SoulUsageCommand::setSoulUsage));
    }

    private static int showSoulUsage(CommandContext<CommandSourceStack> context) {
        return executable(context, sp -> SpiritHandler.readOnServer(sp, sh ->
                new ComponentPacket(ChatUtils.soulUsage(sh.getActualUsage())).toClient(sp)));
    }

    private static int setSoulUsage(CommandContext<CommandSourceStack> context) {
        return executable(context, sp ->
                new SetSoulUsageFunction(sp, IntegerArgumentType.getInteger(context, "soulUsage")).start());
    }

    private static class SetSoulUsageFunction extends Spiritualism.TickingFunction {
        ServerPlayer sp;
        int soulUsage;
        Vec3 ov;

        public SetSoulUsageFunction(ServerPlayer sp, int soulUsage) {
            super(100);
            this.sp = sp;
            this.soulUsage = soulUsage;
        }

        @Override
        public void function() {
            SpiritHandler.edit(sp, sh -> sh.soulUsage = soulUsage);
            new ComponentPacket(ChatUtils.controlled()).toClient(sp);
        }

        @Override
        public boolean isValid() {
            var nv = sp.position();
            if (sp.isDeadOrDying() || ov != null && nv != ov) {
                new ComponentPacket(ChatUtils.interrupted()).toClient(sp);
                return false;
            } else {
                ov = nv;
                var left = requiredTicks - currentTicks;
                if (left % 20 == 0)
                    new ComponentPacket(ChatUtils.controlling(left)).toClient(sp);
                return true;
            }
        }
    }
}
