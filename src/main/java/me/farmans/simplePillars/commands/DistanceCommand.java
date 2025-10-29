package me.farmans.simplePillars.commands;

import me.farmans.simplePillars.SimplePillars;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class DistanceCommand implements CommandExecutor, TabExecutor {
    SimplePillars plugin;

    public DistanceCommand(SimplePillars plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!label.equalsIgnoreCase("pdistance") || args.length == 0) return false;

        if (Integer.parseInt(args[0]) < 3) {
            commandSender.sendMessage("Moc malá vzdálenost");
            return true;
        }

        plugin.getConfig().set("Distance", Integer.parseInt(args[0]));
        plugin.saveConfig();
        commandSender.sendMessage(String.format("Distance byl nastaven na %s", args[0]));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        return Collections.singletonList(plugin.getConfig().getString("Distance"));
    }
}
