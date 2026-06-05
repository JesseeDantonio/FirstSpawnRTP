package fr.jessee.firstSpawnRTP.command.sub;

import fr.jessee.firstSpawnRTP.feature.AbstractSubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static fr.jessee.firstSpawnRTP.FirstSpawnRTP.getLangFile;

public class Help extends AbstractSubCommand {
    @Override
    public String getName() {
        return "help";
    }

    @Override
    public List<String> getAliases() {
        return List.of("h");
    }

    @Override
    public String getDescription() {
        return "Show the help message";
    }

    @Override
    public String getUsage() {
        return "/rtp help";
    }

    @Override
    public void executePlayer(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) return;

        String msg = prepareMsg(args);
        p.sendMessage(msg);
    }

    @Override
    public void executeConsole(CommandSender sender, String[] args) {
        if (sender instanceof Player) return;

        String msg = prepareMsg(args);
        sender.sendMessage(msg);
    }

    private String prepareMsg(String[] args) {
        ConfigurationSection helpSection = getLangFile().getConfiguration().getConfigurationSection("help");
        if (helpSection == null) return "";
        List<List<String>> help = new ArrayList<>();

        for (String key : helpSection.getKeys(false)) {
            help.add(getLangFile().getStringList("help." + key));
        }

        if (args.length == 0) {
            for (String msg : help.getFirst()) {
                return msg;
            }
        }

        if (isInteger(args[0])) {
            int page = Integer.parseInt(args[0]);
            if (page > 0 && page <= help.size()) {
                for (String msg : help.get(page - 1)) {
                    return msg;
                }
            }
        }
        return "";
    }

    private static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
