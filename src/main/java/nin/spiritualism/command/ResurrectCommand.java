package nin.spiritualism.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import nin.spiritualism.SpiritualismConfig;
import nin.spiritualism.capability.SpiritHandler;
import nin.spiritualism.utils.PlayerUtils;

public class ResurrectCommand extends AbstractCommand {

    @Override
    public LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("resurrect").executes(ResurrectCommand::resurrect);
    }

    public static int resurrect(CommandContext<CommandSourceStack> context) {
        return executable(context, sp -> SpiritHandler.edit(sp, sh -> {
            if (!sh.isDead) {
                sh.soulPower = SpiritualismConfig.soulDivision;
            } else {
                sh.soulPower = SpiritualismConfig.soulDivision;
                sh.isDead = false;
                sp.setRespawnPosition(sh.previousRespawnDimension, sh.getPreviousRespawnPosition(), 0, false, false);
                PlayerUtils.teleportToRespawn(sp);
                sp.getAbilities().setFlyingSpeed(sh.previousFlyingSpeed);
                sp.setGameMode(sh.previousGameType);
            }
        }));
    }
}
