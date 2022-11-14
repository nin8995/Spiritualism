package nin.spiritualism.command;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import nin.spiritualism.network.TestPacket;

public class TestCommand extends AbstractCommand {
    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("tests")
                .then(Commands.argument("scale", FloatArgumentType.floatArg())
                        .then(Commands.argument("x", FloatArgumentType.floatArg())
                                .then(Commands.argument("y", FloatArgumentType.floatArg())
                                        .executes(c -> {
                                            new TestPacket(FloatArgumentType.getFloat(c, "scale"), FloatArgumentType.getFloat(c, "x"), FloatArgumentType.getFloat(c, "y")).toClient(c.getSource().getPlayer());
                                            return 1;
                                        }))));
    }
}
