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
    public void executePlayer(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) return;

        FirstSpawnRTP.getConfigFile().reload();
        p.sendMessage(FirstSpawnRTP.getLangFile().getString("reload_success"));
    }

    @Override
    public void executeConsole(CommandSender sender, String[] args) {
        if (sender instanceof Player) return;

        FirstSpawnRTP.getConfigFile().reload();
        sender.sendMessage(FirstSpawnRTP.getLangFile().getString("reload_success"));
    }
}
