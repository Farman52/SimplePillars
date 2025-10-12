package me.farmans.simplePillars.commands;

import me.farmans.simplePillars.SimplePillars;
import me.farmans.simplePillars.events.BlockEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class StartCommand implements CommandExecutor, TabExecutor {
    SimplePillars plugin;

    public static List<UUID> playingPlayers = new ArrayList<>();
    public static Map<UUID, Scoreboard> scoreboards = new HashMap<>();

    public StartCommand(SimplePillars plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!label.equalsIgnoreCase("pstart")) return false;

        if (plugin.getConfig().getInt("Schedule") != -1) {
            commandSender.sendMessage("Pillary jsou již zapnutý");
            return true;
        }

        if (args.length > 2) {
            commandSender.sendMessage("Zadávej pouze X a Z coords, Y je nastavený v /pheight");
            return true;
        }

        Player sender = (Player) commandSender;

        int x = args.length > 1 ? Integer.parseInt(args[0]) : (int) sender.getLocation().getX();
        int z = args.length > 1 ? Integer.parseInt(args[1]) : (int) sender.getLocation().getZ();

        plugin.getConfig().set("Center", String.format("%s %s", x, z));
        plugin.saveConfig();

        Object[] materials = Arrays.stream(Material.values()).filter(p -> p.isItem()).toArray();
        Object[] players = plugin.getServer().getOnlinePlayers().toArray();

        BossBar bossbar = plugin.getServer().createBossBar(new NamespacedKey(plugin, "rilypillartimer"), "TIMER", BarColor.GREEN, BarStyle.SOLID);



        int radius = plugin.getConfig().getInt("Radius");
        int height = plugin.getConfig().getInt("Height");
        for (int i = 0; i < players.length; i++) {
            double angle = 2 * Math.PI * i / players.length;
            int xAngle = (int) Math.round(Math.cos(angle) * radius);
            int yAngle = (int) Math.round(Math.sin(angle) * radius);

            Player player = (Player) players[i];
            Location location = new Location(player.getWorld(), xAngle + x, height, yAngle + z);
            for (int j = height; j >= -64; j--) {
                Block block = location.add(0, -1, 0).getBlock();
                block.setType(Material.BEDROCK);
                BlockEvent.blocks.add(block.getLocation());
            }
            player.teleport(location.add(0.5, height + 65, 0.5));
            player.setGameMode(GameMode.SURVIVAL);
            player.getInventory().clear();
            bossbar.addPlayer(player);

            Scoreboard scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();
            Objective obj = scoreboard.registerNewObjective("rilypillar", Criteria.DUMMY, ChatColor.BOLD + "" + ChatColor.AQUA + "SirYakari Pillars");
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            obj.getScore(ChatColor.BOLD + "Name: " + ChatColor.RESET + player.getName()).setScore(2);
            obj.getScore(ChatColor.BOLD + "Alive: " + ChatColor.RESET + plugin.getServer().getOnlinePlayers().size()).setScore(1);
            obj.getScore(ChatColor.BOLD + "Kills: " + ChatColor.RESET + "0").setScore(0);

            player.setScoreboard(scoreboard);
            scoreboards.put(player.getUniqueId(), scoreboard);

            playingPlayers.add(player.getUniqueId());
        }
        commandSender.sendMessage("Hráči teleportnuti na místa");

        final double INTERVAL = plugin.getConfig().getDouble("Interval");
        int schedule = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            private int ticks = 0;

            @Override
            public void run() {
                ticks++;
                bossbar.setProgress(INTERVAL / 100 * ticks);
                if (ticks == 100 / INTERVAL) {
                    for (int i = 0; i < playingPlayers.size(); i++) {
                        int num = new Random().nextInt(materials.length);

                        Player player = Bukkit.getPlayer(playingPlayers.get(i));

                        player.getInventory().addItem(new ItemStack((Material) materials[num]));
                    }
                    ticks = 0;
                }
            }
        }, 100/*WAIT*/, (long) INTERVAL);

        plugin.getConfig().set("Schedule", schedule);
        plugin.saveConfig();

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.sendMessage("Hra započala");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player player = (Player) commandSender;

        return switch (args.length) {
            case 1 -> Collections.singletonList((int)player.getLocation().getX() + "");
            case 2 -> Collections.singletonList((int)player.getLocation().getZ() + "");
            default -> List.of();
        };
    }
}
