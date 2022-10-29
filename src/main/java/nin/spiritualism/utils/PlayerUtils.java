package nin.spiritualism.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class PlayerUtils {
    public static void teleportToRespawn(ServerPlayer sp) {
        teleportToRespawn(sp, sp);
    }

    public static void teleportToRespawn(ServerPlayer spMe, ServerPlayer sp) {
        if (sp.isRespawnForced()) {
            teleport(spMe, sp.getRespawnDimension(), sp.getRespawnPosition());
        } else {
            Optional<Vec3> ovec = sp.getRespawnPosition() != null ? Player.findRespawnPositionAndUseSpawnBlock(sp.server.getLevel(sp.getRespawnDimension()), sp.getRespawnPosition(), 0, false, false) : Optional.empty();
            var pos = ovec.map(BlockPos::new).orElse(sp.server.overworld().getSharedSpawnPos());
            teleport(spMe, ovec.isPresent() ? sp.getRespawnDimension() : Level.OVERWORLD, pos);
        }
    }

    public static void teleport(ServerPlayer p, GlobalPos gp) {
        teleport(p, gp.dimension(), gp.pos());
    }

    public static void teleport(ServerPlayer p, ResourceKey<Level> dim, BlockPos pos) {
        var exp = p.experienceLevel;
        p.teleportTo(p.getServer().getLevel(dim), pos.getX(), pos.getY(), pos.getZ(), 0, 0);
        p.setExperienceLevels(exp);
    }
}
