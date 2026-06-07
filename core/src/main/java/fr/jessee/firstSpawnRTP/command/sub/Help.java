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

        sendHelp(sender, args);
    }

    @Override
    public void executeConsole(CommandSender sender, String[] args) {
        if (sender instanceof Player) return;

        sendHelp(sender, args);
    }

    private List<String> prepareMsg(String[] args) {
        ConfigurationSection helpSection = getLangFile().getConfiguration().getConfigurationSection("help");
        if (helpSection == null) return new ArrayList<>();
        List<List<String>> help = new ArrayList<>();

        for (String key : helpSection.getKeys(false)) {
            help.add(getLangFile().getStringList("help." + key));
        }

        if (help.isEmpty()) return new ArrayList<>();

        if (args.length == 0) {
            return help.getFirst();
        }

        if (isInteger(args[0])) {
            int page = Integer.parseInt(args[0]);
            if (page > 0 && page <= help.size()) {
                return help.get(page - 1);
            }
        }
        return null;
    }

    private void sendHelp(CommandSender sender, String[] args) {
        List<String> messages = prepareMsg(args);

        if (messages == null) {
            sender.sendMessage(getLangFile().getString("help_invalid_page"));
            return;
        };

        for (String msg : messages) {
            sender.sendMessage(msg);
        }
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
