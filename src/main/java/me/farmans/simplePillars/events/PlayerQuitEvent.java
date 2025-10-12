package me.farmans.simplePillars.events;

import me.farmans.simplePillars.SimplePillars;
import me.farmans.simplePillars.commands.StartCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.Scoreboard;

public class PlayerQuitEvent implements Listener {
    public SimplePillars plugin;

    public PlayerQuitEvent(SimplePillars plugin) {this.plugin = plugin;}

    @EventHandler
    public void onQuit(org.bukkit.event.player.PlayerQuitEvent event) {
        if (StartCommand.playingPlayers.contains(event.getPlayer().getUniqueId())) {
            StartCommand.playingPlayers.remove(event.getPlayer().getUniqueId());
            for (Scoreboard sb : StartCommand.scoreboards.values()) {
                for (String entry : sb.getEntries()) {
                    if (ChatColor.stripColor(entry).startsWith("Alive:")) {
                        sb.resetScores(entry);
                    }
                }
                sb.getObjective("rilypillar").getScore(ChatColor.BOLD + "Alive: " + ChatColor.RESET + StartCommand.playingPlayers.size()).setScore(1);
            }
            if (StartCommand.playingPlayers.size() == 1) {
                plugin.getServer().getBossBar(new NamespacedKey(plugin, "rilypillartimer")).removeAll();

                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage(String.format("%s vyhrál!", plugin.getServer().getPlayer(StartCommand.playingPlayers.get(0)).getName()));
                }
                plugin.getServer().getScheduler().cancelTask(plugin.getConfig().getInt("Schedule"));
            }
        }
    }
}
