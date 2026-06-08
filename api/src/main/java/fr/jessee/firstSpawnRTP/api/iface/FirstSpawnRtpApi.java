package fr.jessee.firstSpawnRTP.api.iface;

import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public interface FirstSpawnRtpApi {

    /**
     * Téléporte un joueur aléatoirement.
     * @param player Le joueur à téléporter
     * @param cause La cause de la téléportation
     * @return true si la téléportation a commencé, false sinon
     */
    CompletableFuture<Boolean> teleportPlayer(Player player, RtpCause cause);
}