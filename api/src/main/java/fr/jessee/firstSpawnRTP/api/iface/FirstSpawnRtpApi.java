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
     * @param world Le monde concerné
     * @param center La position du centre du monde
     * @param borderSize La taille du bordure du monde
     * @param attemptsLeft Tentative maximum
     * @return CompletableFuture<Location>
     */
    CompletableFuture<Location> findSafeLocationAsync(World world, Location center, double borderSize, int attemptsLeft);
}