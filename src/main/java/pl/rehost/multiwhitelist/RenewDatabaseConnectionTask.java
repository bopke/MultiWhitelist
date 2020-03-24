package pl.rehost.multiwhitelist;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.graalvm.compiler.core.common.type.ArithmeticOpTable;

import java.sql.Connection;
import java.util.concurrent.atomic.AtomicBoolean;

public class RenewDatabaseConnectionTask extends BukkitRunnable {
    AtomicBoolean isWorking = new AtomicBoolean(false);
    Plugin plugin;
    AsyncPlayerPreLoginEventHandler handler;

    RenewDatabaseConnectionTask(Plugin plugin, AsyncPlayerPreLoginEventHandler handler) {
        this.plugin = plugin;
        this.handler = handler;
    }

    @Override
    public void run() {
        if (!isWorking.getAndSet(true)) {
            return;
        }
        Connection con;
        do {
            con = MultiWhitelist.getNewConnection();
        } while (con == null);
        handler.setConnection(con);
        isWorking.set(false);

    }
}
