package fr.jessee.firstSpawnRTP.feature;

import fr.jessee.firstSpawnRTP.util.iface.SubCommand;

import java.util.Collections;
import java.util.List;

public abstract class AbstractSubCommand implements SubCommand {
    public List<String> getAliases() { return Collections.emptyList(); }
    public String getDescription() { return ""; }
    public String getUsage() { return ""; }
    public String getPermission() { return ""; }
}
