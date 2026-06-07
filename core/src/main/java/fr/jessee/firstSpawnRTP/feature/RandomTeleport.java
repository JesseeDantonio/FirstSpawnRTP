package fr.jessee.firstSpawnRTP.feature;

import fr.jessee.firstSpawnRTP.FirstSpawnRTP;
import fr.jessee.firstSpawnRTP.api.event.PlayerPostRtpEvent;
import fr.jessee.firstSpawnRTP.api.event.PlayerPreRtpEvent;
import fr.jessee.firstSpawnRTP.api.iface.RtpCause;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class RandomTeleport {

    private final Random random = new Random();

    private boolean isSafeSpot(final Location LOC) {
        final Block blockCenter = LOC.getBlock();
        final Block blockAbove = blockCenter.getRelative(BlockFace.UP);
        final Block blockBelow = blockCenter.getRelative(BlockFace.DOWN);

        // Le joueur doit avoir de l'espace pour respirer (pas de blocs solides, pas de lave, pas d'eau au niveau de la tête)
        boolean isSafeCenter = !blockCenter.getType().isSolid() && blockCenter.getType() != Material.LAVA && blockCenter.getType() != Material.WATER;
        boolean isSafeAbove = !blockAbove.getType().isSolid() && blockAbove.getType() != Material.LAVA && blockAbove.getType() != Material.WATER;

        // Le bloc en dessous doit être solide (terre, pierre...) OU de l'eau (si tu autorises l'atterrissage dans l'eau)
        boolean isSolidBelow = blockBelow.getType().isSolid();
        boolean isWaterBelow = blockBelow.getType() == Material.WATER;

        boolean isSafeBelow = isSolidBelow || isWaterBelow;

        // On exclut les blocs solides mais dangereux
        Material belowType = blockBelow.getType();
        if (belowType == Material.MAGMA_BLOCK || belowType == Material.CACTUS || belowType == Material.CAMPFIRE || belowType == Material.FIRE) {
            isSafeBelow = false;
        }

        return isSafeBelow && isSafeCenter && isSafeAbove;
    }

    /**
     * Searches a safe spot in the given location.
     *
     * @param location The location where to find a safe spot.
     * @return Location
     */
    private Location searchSafeSpot(Location location) {
        final World world = location.getWorld();
        final int maxHeight = (world.getEnvironment() == World.Environment.NETHER) ? 125 : world.getMaxHeight() - 2;
        final int minHeight = world.getMinHeight() + 1; // En 1.21, le monde descend à -64, on ne s'arrête pas à 1.

        int yGrow = location.getBlockY();
        int yDecr = location.getBlockY() - 1; // On commence un bloc en dessous pour éviter de vérifier deux fois le même bloc

        // On s'assure que les variables de départ sont dans les limites pour éviter tout blocage
        if (yGrow > maxHeight) yGrow = maxHeight;
        if (yDecr < minHeight) yDecr = minHeight;

        while (yDecr >= minHeight || yGrow <= maxHeight) {
            // Chercher vers le haut
            if (yGrow <= maxHeight) {
                Location spot = new Location(world, location.getX(), yGrow, location.getZ());
                if (isSafeSpot(spot)) {
                    spot.setPitch(location.getPitch());
                    spot.setYaw(location.getYaw());
                    return spot; // On retourne directement, plus besoin de break
                }
                yGrow++;
            }

            // Chercher vers le bas
            if (yDecr >= minHeight) {
                Location spot = new Location(world, location.getX(), yDecr, location.getZ());
                if (isSafeSpot(spot)) {
                    spot.setPitch(location.getPitch());
                    spot.setYaw(location.getYaw());
                    return spot;
                }
                yDecr--;
            }
        }

        return null;
    }

    /**
     * Teleports the player to a random safe location inside the world border.
     *
     * @param player The player to be teleported.
     * @return CompetableFuture<Boolean>
     */
    public CompletableFuture<Boolean> p(Player player, RtpCause cause) {
        World world = player.getWorld();
        WorldBorder border = world.getWorldBorder();

        double borderSize = border.getSize() / 2;
        Location center = border.getCenter();

        // On lance la boucle asynchrone avec 10 tentatives maximum
        return findSafeLocationAsync(world, center, borderSize, 10)
                .thenCompose(safeLocation -> {
                    CompletableFuture<Boolean> syncFuture = new CompletableFuture<>();

                    if (safeLocation == null) {
                        syncFuture.complete(false); // Échec de la recherche
                        return syncFuture;
                    }

                    Bukkit.getScheduler().runTask(FirstSpawnRTP.getInstance(), () -> {
                        Location playerLoc = player.getLocation();

                        PlayerPreRtpEvent preEvent = new PlayerPreRtpEvent(
                                player,
                                safeLocation,
                                cause
                        );

                        Bukkit.getPluginManager().callEvent(preEvent);

                        if (preEvent.isCancelled()) {
                            syncFuture.complete(false);
                            return;
                        }

                        player.teleport(preEvent.getTargetLocation());

                        PlayerPostRtpEvent postEvent = new PlayerPostRtpEvent(
                                player,
                                cause,
                                playerLoc,
                                safeLocation
                        );

                        Bukkit.getPluginManager().callEvent(postEvent);

                        syncFuture.complete(true);
                    });

                    return syncFuture;
                });
    }

    /**
     * Finds a safe location inside the world border.
     *
     * @param world
     * @param center
     * @param borderSize
     * @param attemptsLeft
     * @return CompetableFuture<Location>
     */
    private CompletableFuture<Location> findSafeLocationAsync(World world, Location center, double borderSize, int attemptsLeft) {
        if (attemptsLeft <= 0) {
            return CompletableFuture.completedFuture(null); // Échec après 10 tentatives
        }

        // 1. Calcul mathématique (Thread-Safe)
        double x = center.getX() + (random.nextDouble() * 2 - 1) * borderSize;
        double z = center.getZ() + (random.nextDouble() * 2 - 1) * borderSize;

        // 2. Chargement du chunk en asynchrone (API Paper). C'est ce qui évite les lags !
        return world.getChunkAtAsync((int) x >> 4, (int) z >> 4).thenCompose(chunk -> {

            // 3. Une fois le chunk chargé, on vérifie les blocs.
            // Pour être 100% safe avec Bukkit, la lecture des blocs se fait sur le thread principal.
            CompletableFuture<Location> syncCheckFuture = new CompletableFuture<>();

            Bukkit.getScheduler().runTask(FirstSpawnRTP.getInstance(), () -> {
                int y = world.getHighestBlockYAt((int) x, (int) z);
                Location potentialLocation = new Location(world, x, y, z);
                Location safeLocation = searchSafeSpot(potentialLocation);
                syncCheckFuture.complete(safeLocation);
            });

            // 4. On analyse le résultat de la vérification
            return syncCheckFuture.thenCompose(safeLocation -> {
                if (safeLocation != null) {
                    return CompletableFuture.completedFuture(safeLocation); // On a trouvé !
                } else {
                    // Pas sûr, on retente sa chance en rappelant la méthode (tentative - 1)
                    return findSafeLocationAsync(world, center, borderSize, attemptsLeft - 1);
                }
            });
        });
    }
}
