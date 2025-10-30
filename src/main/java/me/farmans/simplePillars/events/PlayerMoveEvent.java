package me.farmans.simplePillars.events;

import me.farmans.simplePillars.SimplePillars;
import me.farmans.simplePillars.commands.StartCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerMoveEvent implements Listener {
    SimplePillars plugin;

    public PlayerMoveEvent(SimplePillars plugin) {this.plugin = plugin;}

    @EventHandler
    public void onPlayerMove(org.bukkit.event.player.PlayerMoveEvent event) {
        if (plugin.getConfig().getInt("Schedule") != -1 && StartCommand.playingPlayers.contains(event.getPlayer().getUniqueId())) {
            int FALL_MAX = plugin.getConfig().getInt("FallMax");
            int HEIGHT = plugin.getConfig().getInt("Height");
            if (event.getPlayer().getLocation().getY() <= (HEIGHT - FALL_MAX)) {
                event.getPlayer().setHealth(0);
            }
        }
    }
}
