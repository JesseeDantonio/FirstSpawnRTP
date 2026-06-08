package fr.jessee.firstSpawnRTP.api.iface;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

public interface FirstSpawnRtpApi {

    /**
     * Téléporte un joueur aléatoirement.
     * @param player Le joueur à téléporter
     * @param cause La cause de la téléportation
     * @return CompletableFuture<Boolean>
     */
    CompletableFuture<Boolean> teleportPlayer(Player player, RtpCause cause);

    /**
     * Trouver un endroit sûr dans le monde.
     * @param world
     * @param center
     * @param borderSize
     * @param attemptsLeft
     * @return CompletableFuture<Location>
     */
    CompletableFuture<Location> findSafeLocationAsync(World world, Location center, double borderSize, int attemptsLeft);
}