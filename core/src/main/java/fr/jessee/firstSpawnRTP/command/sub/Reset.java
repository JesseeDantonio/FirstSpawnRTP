package fr.jessee.firstSpawnRTP.command.sub;

import fr.jessee.firstSpawnRTP.FirstSpawnRTP;
import fr.jessee.firstSpawnRTP.feature.AbstractSubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class Reset extends AbstractSubCommand {
    @Override
    public String getName() {
        return "reset";
    }

    public List<String> getAliases() {
        return List.of("");
    }
    @Override
    public String getDescription() {
        return "Reset the player's data";
    }
    @Override
    public String getUsage() {
        return "/rtp reset";
    }

    @Override
    public String getPermission() {
        return "rtp.reset";
    }

    @Override
    public void executePlayer(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) return;

        if (!p.hasPermission(getPermission())) {
            p.sendMessage(FirstSpawnRTP.getLangFile().getString("no_permission"));
            return;
        }

        FirstSpawnRTP.getInstance().getPlayerRepository().getCleanTableAsync().whenComplete((result, error) -> {
            if (error != null) {
                FirstSpawnRTP.getInstance().getLogger().severe(error.getMessage());
                return;
            }

            Bukkit.getScheduler().runTask(FirstSpawnRTP.getInstance(), () -> {
                p.sendMessage(FirstSpawnRTP.getLangFile().getString("reset_success"));
            });
        });
    }

    @Override
    public void executeConsole(CommandSender sender, String[] args) {
        if (sender instanceof Player) return;

        FirstSpawnRTP.getInstance().getPlayerRepository().getCleanTableAsync().whenComplete((result, error) -> {
            if (error != null) {
                FirstSpawnRTP.getInstance().getLogger().severe(error.getMessage());
                return;
            }

            sender.sendMessage(FirstSpawnRTP.getLangFile().getString("reset_success"));
        });
    }
}
