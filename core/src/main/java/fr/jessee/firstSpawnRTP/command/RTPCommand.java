package fr.jessee.firstSpawnRTP.command;

import fr.jessee.firstSpawnRTP.FirstSpawnRTP;
import fr.jessee.firstSpawnRTP.api.iface.RtpCause;
import fr.jessee.firstSpawnRTP.util.iface.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Modifier;
import java.util.*;

public class RTPCommand implements CommandExecutor {
    private final Map<String, SubCommand> subCommands = new HashMap<>();

    public RTPCommand() {
        // Scan le package des sous-commandes
        Reflections reflections = new Reflections(
                "fr.jessee.firstSpawnRTP.command.sub",
                Scanners.SubTypes
        );
        Set<Class<? extends SubCommand>> commandClasses = reflections.getSubTypesOf(SubCommand.class);

        for (Class<? extends SubCommand> cmdClass : commandClasses) {
            try {
                // ⚠️ Ignore les classes abstraites ou interfaces
                if (Modifier.isAbstract(cmdClass.getModifiers()) || Modifier.isInterface(cmdClass.getModifiers())) {
                    continue;
                }

                SubCommand sub = cmdClass.getDeclaredConstructor().newInstance();
                subCommands.put(sub.getName().toLowerCase(), sub);
                for (String alias : sub.getAliases()) {
                    subCommands.put(alias.toLowerCase(), sub);
                }
            } catch (Exception e) {
                FirstSpawnRTP.getInstance().getLogger().warning("Unable to instantiate the subcommand: " + cmdClass.getName());
                FirstSpawnRTP.getInstance().getLogger().severe(e.getMessage());
            }
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NonNull [] args) {
        if (sender instanceof Player p) {
            if (args.length == 0) {
                subCommands.get("help").executePlayer(sender, args);
                return true;
            }

            Player player = Bukkit.getPlayer(args[0]);
            if (player != null) {
                if (!sender.hasPermission("rtp.other")) {
                    p.sendMessage(FirstSpawnRTP.getLangFile().getString("no_permission"));
                    return true;
                };
                teleportOther(sender, player);
                return true;
            }

            SubCommand sub = subCommands.get(args[0].toLowerCase());
            if (sub == null) {
                p.sendMessage(FirstSpawnRTP.getLangFile().getString("unknown_subcommand"));
                return true;
            }
            sub.executePlayer(sender, Arrays.copyOfRange(args, 1, args.length));
        } else {
            if (args.length == 0) {
                subCommands.get("help").executeConsole(sender, args);
                return true;
            }

            Player player = Bukkit.getPlayer(args[0]);
            if (player != null) {
                teleportOther(sender, player);
                return true;
            }

            SubCommand sub = subCommands.get(args[0].toLowerCase());
            if (sub == null) {
                Bukkit.getConsoleSender().sendMessage(FirstSpawnRTP.getLangFile().getString("unknown_subcommand"));
                return true;
            }
            sub.executeConsole(sender, Arrays.copyOfRange(args, 1, args.length));
        }

        return true;
    }

    private void teleportOther(CommandSender sender, Player player) {
        player.sendMessage(FirstSpawnRTP.getLangFile().getString("search_safe_loc"));
        FirstSpawnRTP.getInstance().getRandomTeleport().p(player, RtpCause.COMMAND).thenAccept((result) -> {
            if (result) {
                sender.sendMessage("Teleported " + player.getName() + " to a safe location.");
                player.sendMessage(FirstSpawnRTP.getLangFile().getString("success_player_rtp"));
            } else {
                sender.sendMessage("Failed to teleport " + player.getName() + ".");
            }
        });
    }
}

