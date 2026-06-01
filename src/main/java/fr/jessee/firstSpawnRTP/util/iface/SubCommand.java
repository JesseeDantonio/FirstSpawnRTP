package fr.jessee.firstSpawnRTP.util.iface;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface SubCommand {
    String getName(); // ex: "invite"
    List<String> getAliases(); // ex: Arrays.asList("inv")
    String getDescription();
    String getUsage();
    void executePlayer(CommandSender sender, String[] args);
    void executeConsole(CommandSender sender, String[] args);
    String getPermission();
}
