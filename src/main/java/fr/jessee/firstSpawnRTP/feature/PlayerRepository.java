package fr.jessee.firstSpawnRTP.feature;

import fr.jessee.firstSpawnRTP.util.iface.ConnectionProvider;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.bukkit.Bukkit.getLogger;

public class PlayerRepository {
    private final ConnectionProvider connectionProvider;
    private final String TABLE_NAME = "player_list";

    public PlayerRepository(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;

        // Création de la table si elle n'existe pas
        try (Statement stmt = connectionProvider.getConnection().createStatement()) {
            stmt.executeUpdate(String.format("""
                CREATE TABLE IF NOT EXISTS %s (
                    uuid TEXT NOT NULL,
                    created_at INTEGER NOT NULL,
                    PRIMARY KEY (uuid)
                );
            """, TABLE_NAME));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void addPlayer(UUID playerId) {
        try (PreparedStatement ps = connectionProvider.getConnection().prepareStatement(
                String.format("INSERT INTO %s (uuid, created_at) VALUES (?, ?)", TABLE_NAME)
        )) {
            ps.setString(1, playerId.toString());
            ps.setLong(2, System.currentTimeMillis());
            ps.executeUpdate();
        } catch (SQLException e) {
            getLogger().severe(e.getMessage());
        }
    }

    private boolean isPlayerExist(UUID playerId) {
        try (PreparedStatement ps = connectionProvider.getConnection().prepareStatement(
                String.format("SELECT 1 FROM %s WHERE uuid=?", TABLE_NAME)
        )) {
            ps.setString(1, playerId.toString());

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            getLogger().severe(e.getMessage());
        }

        return false;
    }

    public CompletableFuture<Void> getAddAsync(UUID playerId) {
        return CompletableFuture.runAsync(() -> addPlayer(playerId));
    }

    public CompletableFuture<Boolean> getIsPlayerExistAsync(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> isPlayerExist(playerId));
    }
}
