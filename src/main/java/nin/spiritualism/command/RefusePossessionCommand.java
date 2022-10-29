package nin.spiritualism.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import nin.spiritualism.capability.SpiritHandler;
import nin.spiritualism.network.ComponentPacket;
import nin.spiritualism.utils.ChatUtils;

public class RefusePossessionCommand extends AbstractCommand {

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("refusePossession").executes(RefusePossessionCommand::showRefusePossession)
                .then(Commands.argument("refusePossession", BoolArgumentType.bool()).executes(RefusePossessionCommand::refusePossession));
    }

    private static int showRefusePossession(CommandContext<CommandSourceStack> context) {
        return executable(context, sp -> SpiritHandler.readOnServer(sp, sh -> new ComponentPacket(ChatUtils.refusePossession(sh.refusePossession)).toClient(sp)));
    }

    private static int refusePossession(CommandContext<CommandSourceStack> context) {
        return executable(context, sp -> SpiritHandler.edit(sp, sh -> {
            var b = BoolArgumentType.getBool(context, "refusePossession");
            sh.refusePossession = b;
            new ComponentPacket(ChatUtils.refusePossession(b)).toClient(sp);
        }));
    }
}
