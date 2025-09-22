package org.yan.Nick;

import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Nick extends JavaPlugin {
    private Connection connection;
    private File configFile;
    @Override
    public void onEnable() {
        saveDefaultConfig(); // 保存默認 config.yml
        String host = getConfig().getString("mysql.host");
        int port = getConfig().getInt("mysql.port");
        String database = getConfig().getString("mysql.database");
        String username = getConfig().getString("mysql.username");
        String password = getConfig().getString("mysql.password");
// 初始化 MySQL 連接
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false",
                    username,
                    password
            );
            getLogger().info("成功連接 MySQL 資料庫！");
        } catch (SQLException e) {
            getLogger().severe("無法連接到 MySQL 資料庫: " + e.getMessage());
        }

        // 註冊事件與指令
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getCommand("nick").setExecutor(new NicknameCommand(this));
        getCommand("nickreload").setExecutor(new ReloadCommand(this));

    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                String host = getConfig().getString("mysql.host");
                int port = getConfig().getInt("mysql.port");
                String database = getConfig().getString("mysql.database");
                String username = getConfig().getString("mysql.username");
                String password = getConfig().getString("mysql.password");

                connection = DriverManager.getConnection(
                        "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true",
                        username,
                        password
                );
            }
        } catch (SQLException e) {
            getLogger().severe("無法建立 MySQL 連接: " + e.getMessage());
        }
        return connection;
    }

    @Override
    public void onDisable() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                getLogger().info("MySQL 連接已關閉！");
            }
        } catch (SQLException e) {
            getLogger().severe("關閉 MySQL 連接時出錯：" + e.getMessage());
        }
        getLogger().info("插件正在關閉...");
        // Plugin shutdown logic
    }
}
