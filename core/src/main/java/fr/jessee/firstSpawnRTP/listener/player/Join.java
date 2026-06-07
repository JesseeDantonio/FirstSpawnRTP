package fr.jessee.firstSpawnRTP.listener.player;

import fr.jessee.firstSpawnRTP.FirstSpawnRTP;
import fr.jessee.firstSpawnRTP.api.iface.RtpCause;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Join implements Listener {

    @EventHandler
    public void onJoin(@NotNull final PlayerJoinEvent EVENT) {
        final Player PLAYER = EVENT.getPlayer();
        final UUID UUID_P = PLAYER.getUniqueId();

        FirstSpawnRTP.getInstance().getPlayerRepository().getIsPlayerExistAsync(UUID_P).thenAccept(playerExist -> {
            if (!playerExist) {
                FirstSpawnRTP.getInstance().getPlayerRepository().getAddAsync(UUID_P);
                FirstSpawnRTP.getInstance().getRandomTeleport().p(PLAYER, RtpCause.JOIN);
            }
        });
    }
}
