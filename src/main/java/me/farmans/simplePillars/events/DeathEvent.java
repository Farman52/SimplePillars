package me.farmans.simplePillars.events;

import me.farmans.simplePillars.SimplePillars;
import me.farmans.simplePillars.commands.StartCommand;
import me.farmans.simplePillars.utils.ChatUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DeathEvent implements Listener {
    public SimplePillars plugin;
    public static Map<UUID, Integer> kills = new HashMap<>();

    public DeathEvent(SimplePillars plugin) {this.plugin = plugin;}

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (plugin.getConfig().getInt("Schedule") != -1) {
            Player player = event.getPlayer();
            if (StartCommand.playingPlayers.contains(player.getUniqueId())) {
                StartCommand.playingPlayers.remove(player.getUniqueId());

                player.setGameMode(GameMode.SPECTATOR);

                for (Scoreboard sb : StartCommand.scoreboards.values()) {
                    deleteScoreboardLine(sb, "Alive:");
                    sb.getObjective("simplepillars").getScore(ChatColor.BOLD + "Alive: " + ChatColor.RESET + StartCommand.playingPlayers.size()).setScore(1);
                }

                if (StartCommand.playingPlayers.size() == 1) {
                    plugin.getServer().getScheduler().cancelTask(plugin.getConfig().getInt("Schedule"));
                    plugin.getServer().getBossBar(new NamespacedKey(plugin, "simplepillars")).removeAll();

                    plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                        ChatUtil.sendAllMessage(
                                plugin,
                                String.format("%s vyhrál!", plugin.getServer().getPlayer(StartCommand.playingPlayers.get(0)).getName()),
                                true
                        );
                    }, 2L);
                }

                if (player.getKiller() instanceof Player) {
                    Player killer = player.getKiller();
                    kills.put(killer.getUniqueId(), kills.containsKey(killer.getUniqueId()) ? kills.get(killer.getUniqueId()) + 1 : 1);

                    Scoreboard scoreboard = StartCommand.scoreboards.get(killer.getUniqueId());
                    deleteScoreboardLine(scoreboard, "Kills:");
                    scoreboard.getObjective("simplepillars").getScore(ChatColor.BOLD + "Kills: " + ChatColor.RESET + this.kills.get(killer.getUniqueId())).setScore(0);
                }
            } else {
                event.setDeathMessage(null);
            }
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (plugin.getConfig().getInt("Schedule") != -1) {
            event.setRespawnLocation(new Location(event.getPlayer().getWorld(), Integer.parseInt(plugin.getConfig().getString("Center").split(" ")[0]), plugin.getConfig().getInt("Height"), Integer.parseInt(plugin.getConfig().getString("Center").split(" ")[1])));
        }
    }

    public void deleteScoreboardLine(Scoreboard sb, String line) {
        for (String entry : sb.getEntries()) {
            if (ChatColor.stripColor(entry).startsWith(line)) {
                sb.resetScores(entry);
            }
        }
    }
}
