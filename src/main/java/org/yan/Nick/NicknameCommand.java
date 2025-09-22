package org.yan.Nick;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class NicknameCommand implements CommandExecutor {
    private final Nick plugin;
    public static String convertHexColorToMinecraftFormat(String hexColor) {
        if (hexColor.startsWith("#") && hexColor.length() == 7) {
            StringBuilder colorBuilder = new StringBuilder("§x");
            for (int i = 1; i < hexColor.length(); i++) {
                colorBuilder.append("§").append(hexColor.charAt(i));
            }
            return colorBuilder.toString();
        }
        return hexColor;
    }
    public NicknameCommand(Nick plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("只有玩家可以使用此指令");
            return true;
        }

        Player player = (Player) sender;
        if (!player.hasPermission("nick.set")) {
            player.sendMessage(ChatColor.RED + "你沒有權限使用此指令！");
            return true;
        }

        if (args.length == 0) {
            try (Connection connection = plugin.getConnection();
                 PreparedStatement stmt = connection.prepareStatement("DELETE FROM player_nicks WHERE player_uuid = ?")) {
                stmt.setString(1, player.getUniqueId().toString());
                stmt.executeUpdate();

                player.setDisplayName(player.getName());  // 將顯示名稱重置為原始名稱
                player.sendMessage(ChatColor.GREEN + "你的暱稱已重置為：" + player.getName());
            } catch (SQLException e) {
                plugin.getLogger().severe("重置暱稱時出錯：" + e.getMessage());
                player.sendMessage(ChatColor.RED + "無法重置暱稱，請聯絡管理員！");
            }
            return true;
        }

        String nickname = args[0];

        if (nickname.matches("#[a-fA-F0-9]{6}")) {
            nickname = convertHexColorToMinecraftFormat(nickname);
        }
        if (!player.hasPermission("nick.usecolor")) {
            nickname = ChatColor.stripColor(nickname);
        }

        try (Connection connection = plugin.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "REPLACE INTO player_nicks (player_uuid, player_name, nickname) VALUES (?, ?, ?)"
             )){
             stmt.setString(1, player.getUniqueId().toString());
             stmt.setString(2, player.getName());
             stmt.setString(3, nickname);
             stmt.executeUpdate();
            player.setDisplayName(ChatColor.translateAlternateColorCodes('&', nickname)+"("+player.getName()+")");
            player.sendMessage(ChatColor.GREEN + "你的暱稱已更改為：" + ChatColor.translateAlternateColorCodes('&', nickname)+"("+player.getName()+")");
        } catch (SQLException e) {
            plugin.getLogger().severe("儲存暱稱時出錯：" + e.getMessage());
            player.sendMessage(ChatColor.RED + "無法保存暱稱，請聯絡管理員！");
        }
        return true;
    }
}
