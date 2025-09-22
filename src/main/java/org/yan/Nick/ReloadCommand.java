package org.yan.Nick;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static org.bukkit.Bukkit.getLogger;

public class ReloadCommand implements CommandExecutor {
    private final Nick plugin;

    public ReloadCommand(Nick plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            // 执行重新加载配置的逻辑
            plugin.reloadConfig();
            sender.sendMessage("配置已重新加载。");
            getLogger().info(ChatColor.translateAlternateColorCodes('&', "插件已重新加載"));
            return true;
        }
        return false;
    }
}
