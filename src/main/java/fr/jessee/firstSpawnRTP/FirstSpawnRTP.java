package fr.jessee.firstSpawnRTP;

import fr.jessee.firstSpawnRTP.feature.ConfigFile;
import fr.jessee.firstSpawnRTP.feature.RandomTeleport;
import fr.jessee.firstSpawnRTP.feature.SQLiteDataSource;
import fr.jessee.firstSpawnRTP.feature.PlayerRepository;
import fr.jessee.firstSpawnRTP.listener.player.Join;
import fr.jessee.firstSpawnRTP.util.iface.ConnectionProvider;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;

public final class FirstSpawnRTP extends JavaPlugin {
    private static Plugin instance;
    private static ConfigFile configFile;
    private static ConfigFile langFile;
    private static World world;
    private PlayerRepository playerRepository;
    private RandomTeleport randomTeleport;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        saveDefaultConfig();

        configFile = new ConfigFile("config");
        langFile = new ConfigFile("lang");

        world = Bukkit.getWorld(getConfigFile().getString("world"));
        if (world == null) {
            getLogger().severe("World not found. Please check the configuration.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        try {
            File dbFile = new File(getDataFolder(), "sqlite.db");
            ConnectionProvider connectionProvider = new SQLiteDataSource(dbFile);
            connectionProvider.connect();

            playerRepository = new PlayerRepository(connectionProvider);
        } catch (Exception e) {
            getLogger().severe(e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
        }

        randomTeleport = new RandomTeleport();
        Bukkit.getPluginManager().registerEvents(new Join(), this);
        PluginCommand command = getCommand("rtp");

        if (command == null) {
            getLogger().severe("NullPointerException: Command 'rtp' not found.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        command.setExecutor(new fr.jessee.firstSpawnRTP.command.RTPCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        try {
            if (this.playerRepository != null && this.playerRepository.getConnectionProvider() != null) {
                if (!this.playerRepository.getConnectionProvider().getConnection().isClosed()) {
                    this.playerRepository.getConnectionProvider().getConnection().close();
                }
            }
        } catch (SQLException e) {
            getLogger().severe(e.getMessage());
        }
    }

    public static FirstSpawnRTP getInstance() {
        if (instance == null) {
            throw new IllegalStateException("The FirstSpawnRTP plugin is not yet initialized.");
        }
        return (FirstSpawnRTP) instance;
    }

    public static ConfigFile getConfigFile() {
        return configFile;
    }

    public static ConfigFile getLangFile() {
        return langFile;
    }

    public static World getWorld() {
        return world;
    }

    public PlayerRepository getPlayerRepository() {
        return playerRepository;
    }

    public RandomTeleport getRandomTeleport() {
        return randomTeleport;
    }
}
