package pl.rehost.multiwhitelist;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.sql.*;

public class AsyncPlayerPreLoginEventHandler implements Listener {
    Connection con;

    final String kickReason;
    final String errorReason;
    final MultiWhitelist plugin;

    AsyncPlayerPreLoginEventHandler(MultiWhitelist plugin, Connection con, String kickReason, String errorReason) {
        this.con = con;
        this.kickReason = kickReason;
        this.errorReason = errorReason;
        this.plugin = plugin;
    }

    @EventHandler
    public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) {
        try {
            String name = event.getName();
            PreparedStatement ps = con.prepareStatement("SELECT count(*) FROM LinkedUsers WHERE minecraft_nickname = ? AND valid=1");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            rs.next();
            int c = rs.getInt("count(*)");
            rs.close();
            if (c == 0) {
                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, kickReason);
            }
        } catch (SQLException e) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, errorReason);
            plugin.getLogger().warning("Błąd połączenia z bazą danych. Pojedynczo to nie problem - jeżeli często się wydarza, poinformuj na discordzie Bopke#1337 i podaj to: " + e.getMessage());
            new RenewDatabaseConnectionTask(plugin, this).runTaskAsynchronously(plugin);
        }
    }

    void setConnection(Connection con) {
        this.con = con;
    }
}


