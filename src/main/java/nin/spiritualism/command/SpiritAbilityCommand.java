package nin.spiritualism.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import nin.spiritualism.ability.SpiritAbility;
import nin.spiritualism.capability.SpiritHandler;
import nin.spiritualism.network.ComponentPacket;
import nin.spiritualism.util.ChatUtils;

public class SpiritAbilityCommand extends AbstractCommand {

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        var c = Commands.literal("spiritAbility").executes(SpiritAbilityCommand::showSpiritAbility);
        SpiritAbility.sas.forEach(sa -> c.then(Commands.literal(sa).executes((cc) -> setSpiritAbility(cc, sa))));
        return c;
    }

    private static int setSpiritAbility(CommandContext<CommandSourceStack> c, String sa) {
        return executable(c, sp -> SpiritHandler.edit(sp, sh -> {
            sh.sa = sa;
            new ComponentPacket(ChatUtils.spiritAbility(sh.sa)).toClient(sp);
        }));
    }

    private static int showSpiritAbility(CommandContext<CommandSourceStack> c) {
        return executable(c, sp -> SpiritHandler.readOnServer(sp, sh -> new ComponentPacket(ChatUtils.spiritAbility(sh.sa)).toClient(sp)));
    }
}
