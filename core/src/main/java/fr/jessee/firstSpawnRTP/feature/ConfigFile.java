package fr.jessee.firstSpawnRTP.feature;

import fr.jessee.firstSpawnRTP.FirstSpawnRTP;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.bukkit.Bukkit.getConsoleSender;

public class ConfigFile {
    private final File file;
    private YamlConfiguration configuration;

    public ConfigFile(String name) {
        boolean result;
        file = new File(FirstSpawnRTP.getInstance().getDataFolder(), name + ".yml");
        if (!file.getParentFile().exists()) {
            result = file.getParentFile().mkdirs();
        }

        if (!file.exists()) {
            FirstSpawnRTP.getInstance().saveResource(name + ".yml", false);
        }

        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public boolean containsInSection(String section, String key) {
        org.bukkit.configuration.ConfigurationSection sec = configuration.getConfigurationSection(section);
        return sec != null && sec.contains(key);
    }

    public double getDouble(String path) {
        return configuration.getDouble(path, 0);
    }

    public int getInt(String path) {
        return configuration.getInt(path, 0);
    }

    public boolean getBoolean(String path) {
        return configuration.getBoolean(path, false);
    }

    public long getLong(String path) {
        return configuration.getLong(path, 0);
    }

    public String getString(String path) {
        String value = configuration.getString(path);
        return value != null ? ChatColor.translateAlternateColorCodes('&', value) : null;
    }

    public List<String> getStringList(String path) {
        if (configuration.contains(path)) {
            return configuration.getStringList(path)
                    .stream()
                    .map(s -> ChatColor.translateAlternateColorCodes('&', s))
                    .toList();
        }
        return List.of();
    }

    public List<String> getReversedStringList(String path) {
        List<String> list = getStringList(path);
        List<String> reversed = new ArrayList<>(list);
        Collections.reverse(reversed);
        return reversed;
    }

    public InputStream getInputStream() {
        try {
            return new FileInputStream(this.file);
        } catch (IOException e) {
            getConsoleSender().sendMessage("Error getting InputStream: " + e.getMessage());
            return null;
        }
    }

    public void reload() {
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public YamlConfiguration getConfiguration() {
        return configuration;
    }
}
