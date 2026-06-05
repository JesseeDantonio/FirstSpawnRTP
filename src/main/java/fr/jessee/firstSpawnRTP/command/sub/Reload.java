package fr.jessee.firstSpawnRTP.command.sub;

import fr.jessee.firstSpawnRTP.FirstSpawnRTP;
import fr.jessee.firstSpawnRTP.feature.AbstractSubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Reload extends AbstractSubCommand {
    @Override
    public String getName() {
        return "reload";
    }
    @Override
    public List<String> getAliases() {
        return List.of("");
    }
    @Override
    public String getDescription() {
        return "Reload the plugin configuration";
    }
    @Override
    public String getUsage() {
        return "/rtp reload";
    }
    @Override
    public String getPermission() {
        return "rtp.reload";
    }

    @Override
    public void executePlayer(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) return;
        if (!p.hasPermission(getPermission())) {
            p.sendMessage(FirstSpawnRTP.getLangFile().getString("no_permission"));
            return;
        }

        reload();
        p.sendMessage(FirstSpawnRTP.getLangFile().getString("reload_success"));
    }

    @Override
    public void executeConsole(CommandSender sender, String[] args) {
        if (sender instanceof Player) return;

        reload();
        sender.sendMessage(FirstSpawnRTP.getLangFile().getString("reload_success"));
    }

    private void reload() {
        FirstSpawnRTP.getConfigFile().reload();
        FirstSpawnRTP.getLangFile().reload();
    }
}
