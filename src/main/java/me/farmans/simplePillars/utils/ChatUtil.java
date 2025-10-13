package me.farmans.simplePillars.utils;

import me.farmans.simplePillars.SimplePillars;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class ChatUtil {
    public static void sendAllMessage(SimplePillars plugin, String text, boolean fancyText) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            player.sendMessage(fancyText == true ?
                    gradient(plugin.getConfig().getString("Title")+" >", true, plugin.getConfig().getString("TitleHex").split(" ")) + ChatColor.RESET + " " + text
                    : text);
        }
    }

    public static String gradient(String text, boolean bold, String[] hexColors) {
        text = org.bukkit.ChatColor.stripColor(text);
        if (hexColors.length < 2) return text;

        StringBuilder sb = new StringBuilder();
        int sections = hexColors.length;
        int partLength = (int) Math.ceil((double) text.length() / sections);
        int index = 0;

        for (int i = 0; i < sections; i++) {
            String part = text.substring(index, Math.min(index + partLength, text.length()));
            sb.append(ChatColor.of(hexColors[i].substring(1)));
            if (bold) sb.append(org.bukkit.ChatColor.BOLD);
            sb.append(part);
            index += partLength;
            if (index >= text.length()) break;
        }

        return sb.toString();
    }
}
