package fr.jessee.firstSpawnRTP;

import fr.jessee.firstSpawnRTP.feature.ConfigFile;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class FirstSpawnRTP extends JavaPlugin {
    private static Plugin instance;
    private static ConfigFile configFile;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        saveDefaultConfig();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
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
}
