package org.yan.Nick;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerJoinListener implements Listener {

    private final Nick plugin;

    public PlayerJoinListener(Nick plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        try (Connection connection = plugin.getConnection();
             PreparedStatement stmt = connection.prepareStatement("SELECT * FROM player_nicks WHERE player_uuid = ?")) {
            stmt.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                String nickname = resultSet.getString("nickname");
                String playername = resultSet.getString("player_name");
                player.setDisplayName(ChatColor.translateAlternateColorCodes('&', nickname)+"("+playername+")");
                player.sendMessage("歡迎回來，" + ChatColor.translateAlternateColorCodes('&', nickname)+"("+player.getName()+")");
            } else {
                player.setDisplayName(player.getName());
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("讀取玩家暱稱時出錯：" + e.getMessage());
        }
    }
}
