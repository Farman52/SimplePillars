package me.farmans.simplePillars.events;

import me.farmans.simplePillars.SimplePillars;
import me.farmans.simplePillars.commands.StartCommand;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DamageEvent implements Listener {
    public SimplePillars plugin;
    public static Map<UUID, Integer> kills = new HashMap<>();

    public DamageEvent(SimplePillars plugin) {this.plugin = plugin;}

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (plugin.getConfig().getInt("Schedule") != -1 && event.getEntity() instanceof Player) {
            this.handleDamage(event);
        }
    }

    public void handleDamage(EntityDamageEvent event) {
        Player player = (Player) event.getEntity();
        if (player.getHealth() - event.getFinalDamage() <= 0 || event.getEntity().getLocation().getY() < -65) {
            event.setCancelled(true);
            if (StartCommand.playingPlayers.contains(player.getUniqueId())) {
                StartCommand.playingPlayers.remove(player.getUniqueId());
            }
            player.setGameMode(GameMode.SPECTATOR);
            player.getInventory().clear();
            player.teleport(new Location(player.getWorld(), Integer.parseInt(plugin.getConfig().getString("Center").split(" ")[0]), plugin.getConfig().getInt("Height"), Integer.parseInt(plugin.getConfig().getString("Center").split(" ")[1])));

            for (Scoreboard sb : StartCommand.scoreboards.values()) {
                this.deleteScoreboardLine(sb, "Alive:");
                sb.getObjective("rilypillar").getScore(ChatColor.BOLD + "Alive: " + ChatColor.RESET + StartCommand.playingPlayers.size()).setScore(1);
            }

            if (StartCommand.playingPlayers.size() == 1) {
                plugin.getServer().getScheduler().cancelTask(plugin.getConfig().getInt("Schedule"));
                plugin.getServer().getBossBar(new NamespacedKey(plugin, "rilypillartimer")).removeAll();

                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage(String.format("%s vyhrál!", plugin.getServer().getPlayer(StartCommand.playingPlayers.get(0)).getName()));
                }
            }

            if (event instanceof EntityDamageByEntityEvent byEntity) {
                if (byEntity.getDamager() instanceof Player) {
                    kills.put(byEntity.getDamager().getUniqueId(), kills.containsKey(byEntity.getDamager().getUniqueId()) ? kills.get(byEntity.getDamager().getUniqueId()) + 1 : 1);

                    Scoreboard scoreboard = StartCommand.scoreboards.get(byEntity.getDamager().getUniqueId());
                    this.deleteScoreboardLine(scoreboard, "Kills:");
                    scoreboard.getObjective("rilypillar").getScore(ChatColor.BOLD + "Kills: " + ChatColor.RESET + this.kills.get(byEntity.getDamager().getUniqueId())).setScore(0);

                }
            }
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
