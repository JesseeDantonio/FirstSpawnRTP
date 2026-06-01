package fr.jessee.firstSpawnRTP.feature;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Random;

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
     * @return A Location object representing the safe spot, or null if no safe spot is available.
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
     */
    public boolean p(Player player) {
        World world = player.getWorld();
        WorldBorder border = world.getWorldBorder();

        // Obtenir les limites de la bordure
        double borderSize = border.getSize() / 2;
        Location center = border.getCenter();

        Location randomLocation = null;
        for (int i = 0; i < 10; i++) { // Tentatives limitées pour trouver une position sûre
            double x = center.getX() + (random.nextDouble() * 2 - 1) * borderSize;
            double z = center.getZ() + (random.nextDouble() * 2 - 1) * borderSize;
            int y = world.getHighestBlockYAt((int) x, (int) z);

            Location potentialLocation = new Location(world, x, y, z);
            randomLocation = searchSafeSpot(potentialLocation);

            if (randomLocation != null) {
                break; // Si une position sûre est trouvée, on arrête les tentatives
            }
        }

        if (randomLocation != null) {
            player.teleport(randomLocation.add(0, 2, 0));
            return true;
        }

        return false;
    }
}
