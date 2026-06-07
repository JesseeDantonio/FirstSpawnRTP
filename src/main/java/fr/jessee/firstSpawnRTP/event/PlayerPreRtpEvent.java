package fr.jessee.firstSpawnRTP.event;

import fr.jessee.firstSpawnRTP.util.RtpCause;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerPreRtpEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean cancelled;
    private final Location targetLocation;
    private final RtpCause cause;

    public PlayerPreRtpEvent(Player player, Location targetLocation, RtpCause cause) {
        super(player);
        this.player = player;
        this.targetLocation = targetLocation;
        this.cause = cause;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    public RtpCause getCause() {
        return cause;
    }
}
