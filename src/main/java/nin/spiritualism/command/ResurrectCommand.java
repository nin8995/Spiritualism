package nin.spiritualism.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import nin.spiritualism.SpiritualismConfig;
import nin.spiritualism.capability.SpiritHandler;

import java.util.Optional;

public class ResurrectCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("resurrect").executes(ResurrectCommand::resurrect);
    }

    public static int resurrect(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getEntity() instanceof ServerPlayer sp)
            SpiritHandler.get(sp).ifPresent(sh -> {
                if (!sh.isDead) {
                    sh.soulPower = SpiritualismConfig.soulDivision;
                    sh.syncToClient(sp);
                    return;
                }
                sh.soulPower = SpiritualismConfig.soulDivision;
                sh.isDead = false;
                sh.syncToClient(sp);
                sp.setRespawnPosition(sh.previousRespawnDimension, sh.getPreviousRespawnPosition(), 0, false, false);
                Optional<Vec3> ovec = sh.getPreviousRespawnPosition() != null ? Player.findRespawnPositionAndUseSpawnBlock(sp.server.getLevel(sh.previousRespawnDimension), sh.getPreviousRespawnPosition(), 0, false, false) : Optional.empty();
                var vec = ovec.map(BlockPos::new).orElseGet(() -> sp.server.overworld().getSharedSpawnPos());
                sp.teleportTo(ovec.isPresent() ? sp.server.getLevel(sh.previousRespawnDimension) : sp.server.overworld(), vec.getX(), vec.getY(), vec.getZ(), 0, 0);
                sp.setGameMode(sh.previousGameType);
                sp.getAbilities().setFlyingSpeed(sh.previousFlyingSpeed);
                sp.onUpdateAbilities();
            });
        return 1;
    }
}
