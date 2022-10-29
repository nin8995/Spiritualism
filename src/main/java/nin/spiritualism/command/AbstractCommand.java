package nin.spiritualism.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Consumer;

public abstract class AbstractCommand {
    public abstract LiteralArgumentBuilder<CommandSourceStack> register();

    static int executable(CommandContext<CommandSourceStack> context, Consumer<ServerPlayer> consumer) {
        if (context.getSource().getEntity() instanceof ServerPlayer sp)
            consumer.accept(sp);
        return 1;
    }
}
