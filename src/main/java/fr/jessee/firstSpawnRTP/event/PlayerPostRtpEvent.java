package fr.jessee.firstSpawnRTP.event;

import fr.jessee.firstSpawnRTP.util.RtpCause;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerPostRtpEvent extends PlayerEvent {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Location from;
    private final Location to;
    private final RtpCause cause;

    public PlayerPostRtpEvent(Player player, RtpCause rtpCause ,Location from, Location to) {
        super(player);
        this.from = from;
        this.to = to;
        this.cause = rtpCause;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }

    public RtpCause getCause() {
        return cause;
    }
}
