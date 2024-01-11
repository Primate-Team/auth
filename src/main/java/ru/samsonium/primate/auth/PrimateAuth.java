package ru.samsonium.primate.auth;

import org.bukkit.plugin.java.JavaPlugin;
import ru.samsonium.primate.auth.commands.LoginCmd;

import java.sql.SQLException;
import java.util.Objects;
import java.util.logging.Level;

public final class PrimateAuth extends JavaPlugin {

    @Override
    public void onEnable() {
        try {
            DB.init();
        } catch (SQLException e) {
            getLogger().log(Level.SEVERE, "Connection to database failed. Disabling plugin");
            getServer().getPluginManager().disablePlugin(this);
        }

        Objects.requireNonNull(getCommand("login")).setExecutor(new LoginCmd());
    }

    @Override
    public void onDisable() {
        try {
            DB.get().close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get plugin class instance
     * @return PrimateAuth instance
     */
    public static PrimateAuth get() {
        return getPlugin(PrimateAuth.class);
    }
}
