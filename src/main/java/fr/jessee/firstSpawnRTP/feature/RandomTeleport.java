package fr.jessee.firstSpawnRTP.feature;

import fr.jessee.firstSpawnRTP.FirstSpawnRTP;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class RandomTeleport {

    private final Random random = new Random();

    private boolean isSafeSpot(final Location LOCATION) {
        final World WORLD = LOCATION.getWorld();
        final int BLOCK_X = LOCATION.getBlockX();
        final int BLOCK_Y = LOCATION.getBlockY();
        final int BLOCK_Z = LOCATION.getBlockZ();

        final Block BLOCK_CENTER = WORLD.getBlockAt(BLOCK_X, BLOCK_Y, BLOCK_Z);
        final Block BLOCK_ABOVE = WORLD.getBlockAt(BLOCK_X, BLOCK_Y + 1, BLOCK_Z);
        final Block BLOCK_BELOW = WORLD.getBlockAt(BLOCK_X, BLOCK_Y - 1, BLOCK_Z);

        boolean isTransparentCenter = BLOCK_CENTER.getType().isOccluding();
        boolean isLiquidCenter = BLOCK_CENTER.isLiquid() && !BLOCK_CENTER.getType().equals(Material.LAVA);

        boolean isTransparentAbove = BLOCK_ABOVE.getType().isOccluding();
        boolean isLiquidAbove = BLOCK_ABOVE.isLiquid() && !BLOCK_ABOVE.getType().equals(Material.LAVA);
//        boolean isSolidAbove = BLOCK_ABOVE.isAir();

        boolean isSolidBelow = BLOCK_BELOW.getType().isSolid();
        boolean isWaterBelow = BLOCK_BELOW.getType().equals(Material.WATER);

        if ((isTransparentCenter || (isLiquidCenter && !BLOCK_CENTER.getType().equals(Material.LAVA)))
                && (isTransparentAbove || (isLiquidAbove && !BLOCK_ABOVE.getType().equals(Material.LAVA)))) {
            // Deux blocs respirables : OK

            // Le bloc en dessous est solide ou un liquide autre que de la lave
            return isSolidBelow || isWaterBelow;
        } else {
            return false;
        }
    }

    /**
     * Searches a safe spot in the given location.
     *
     * @param location The location where to find a safe spot.
     * @return Location
     */
    private Location searchSafeSpot(Location location) {
        Location safeSpot = null;
        final World world = location.getWorld();
        final int maxHeight = (world.getEnvironment() == World.Environment.NETHER) ? 125 : world.getMaxHeight() - 2;

        int yGrow = location.getBlockY();
        int yDecr = location.getBlockY();

        while (yDecr >= 1 || yGrow <= maxHeight) {
            // Above?
            if (yGrow < maxHeight) {
                Location spot = new Location(world, location.getBlockX(), yGrow, location.getBlockZ());
                if (isSafeSpot(spot)) {
                    safeSpot = spot;
                    break;
                }
                yGrow++;
            }

            // Below?
            if (yDecr > 1 && yDecr != yGrow) {
                Location spot = new Location(world, location.getX(), yDecr, location.getZ());
                if (isSafeSpot(spot)) {
                    safeSpot = spot;
                    break;
                }
                yDecr--;
            }
        }

        if (safeSpot != null) {
            safeSpot.setPitch(location.getPitch());
            safeSpot.setYaw(location.getYaw());
        }

        return safeSpot;
    }

    /**
     * Teleports the player to a random safe location inside the world border.
     *
     * @param player The player to be teleported.
     * @return CompetableFuture<Boolean>
     */
    public CompletableFuture<Boolean> p(Player player) {
        World world = player.getWorld();
        WorldBorder border = world.getWorldBorder();

        double borderSize = border.getSize() / 2;
        Location center = border.getCenter();

        // On lance la boucle asynchrone avec 10 tentatives maximum
        return findSafeLocationAsync(world, center, borderSize, 10)
                .thenApply(safeLocation -> {
                    if (safeLocation != null) {
                        player.teleport(safeLocation);
                        return true;
                    }
                    return false;
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
