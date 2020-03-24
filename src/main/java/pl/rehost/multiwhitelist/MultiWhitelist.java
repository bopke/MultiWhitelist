package pl.rehost.multiwhitelist;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MultiWhitelist extends JavaPlugin {
    static MultiWhitelist selfInstance;

    @Override
    public void onEnable() {
        selfInstance = this;
        try {
            this.saveDefaultConfig();
            Class.forName("com.mysql.jdbc.Driver");
            FileConfiguration c = this.getConfig();
            Connection con = getNewConnection();
            if (con == null) {
                this.getLogger().severe("Nieodratowywalny błąd pluginu, wyłączam serwer.");
                this.getServer().shutdown();
            }
            this.getServer().getPluginManager().registerEvents(new AsyncPlayerPreLoginEventHandler(this, con, c.getString("kick_reason"), c.getString("error_reason")), this);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            this.getLogger().severe("Nieodratowywalny błąd pluginu, wyłączam serwer.");
            this.getServer().shutdown();
        }
    }

    static Connection getNewConnection() {
        FileConfiguration c = selfInstance.getConfig();
        try {
            return DriverManager.getConnection(
                    "jdbc:mysql://" + c.getString("mysql_addr") + ":" + c.getInt("mysql_port") + "/" + c.getString("mysql_name") + "?autoReconnect=true", c.getString("mysql_user"), c.getString("mysql_pass"));
        } catch (SQLException e) {
            return null;
        }

    }
}