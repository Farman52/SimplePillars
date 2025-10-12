package me.farmans.simplePillars.events;

import me.farmans.simplePillars.SimplePillars;
import me.farmans.simplePillars.commands.StartCommand;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerJoinEvent implements Listener {
    public SimplePillars plugin;

    public PlayerJoinEvent(SimplePillars plugin) {this.plugin = plugin;}

    @EventHandler
    public void onPlayerJoin(org.bukkit.event.player.PlayerJoinEvent event) {
        if (plugin.getConfig().getInt("Schedule") == -1) {
            BossBar bossbar = plugin.getServer().getBossBar(new NamespacedKey(plugin, "rilypillartimer"));
            if (bossbar != null) bossbar.removePlayer(event.getPlayer());
        } else {
            if (StartCommand.scoreboards.containsKey(event.getPlayer().getUniqueId())) {
                event.getPlayer().setScoreboard(StartCommand.scoreboards.get(event.getPlayer().getUniqueId()));
            }
            if (!StartCommand.playingPlayers.contains(event.getPlayer().getUniqueId())) {
                Player player = event.getPlayer();
                player.setGameMode(GameMode.SPECTATOR);
                player.getInventory().clear();
                player.teleport(new Location(player.getWorld(), Integer.parseInt(plugin.getConfig().getString("Center").split(" ")[0]), plugin.getConfig().getInt("Height"), Integer.parseInt(plugin.getConfig().getString("Center").split(" ")[1])));
            }
        }
    }
}
