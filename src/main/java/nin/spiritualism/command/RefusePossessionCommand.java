package nin.spiritualism.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import nin.spiritualism.capability.SpiritHandler;

public class RefusePossessionCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("refusePossession").then(Commands.argument("refusePossession", BoolArgumentType.bool()).executes(RefusePossessionCommand::refusePossession));
    }

    private static int refusePossession(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() instanceof ServerPlayer sp)
            SpiritHandler.getFromServer(sp).ifPresent(sh -> {
                sh.refusePossession = BoolArgumentType.getBool(context, "refusePossession");
                sh.syncToClients(sp);
            });
        return 1;
    }
}
