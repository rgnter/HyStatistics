package me.DigitalRegent.HyStatistics;

import com.zaxxer.hikari.HikariConfig;
import lombok.Getter;
import me.DigitalRegent.HyStatistics.com.Supervisor;
import me.DigitalRegent.HyStatistics.log.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class Plugin extends JavaPlugin {

    @Getter
    private static @NotNull Plugin instance;
    {
        instance = this;
    }

    private @NotNull Logger logger;
    {
        logger = new Logger();
    }

    private @NotNull HikariConfig config;
    private @NotNull Supervisor supervisor;


    @Override
    public void onLoad() {
        logger.info("Loading plugin...");

        logger.debug("Constructing config...");
        this.getConfig().options().copyDefaults(true);
        this.saveDefaultConfig();

        logger.debug("Resolving config...");
        config = new HikariConfig();

        ConfigurationSection comConfigSection =getConfig().getConfigurationSection("com-config");
        if(comConfigSection != null) {
            config.setUsername(comConfigSection.getString("database-user", "root"));
            config.setPassword(comConfigSection.getString("database-password", ""));
            config.setJdbcUrl("jdbc:mysql://"
                    + comConfigSection.getString("database-host")
                    + ":" + comConfigSection.get("database-port") +
                    "/" + comConfigSection.getString("database-name") +
                    comConfigSection.getString("advanced-params"));

            config.setMaximumPoolSize(comConfigSection.getInt("connection-pool", 5));
            config.setConnectionTimeout(comConfigSection.getLong("database-timeout", 30)* 1000);
            config.setPoolName("SupervisorComPool");

            logger.debug("Database JDBC URL: " + config.getJdbcUrl());
            logger.debug("Pool size: %d, Timeout: %d", config.getMaximumPoolSize(), config.getConnectionTimeout());

        } else
            logger.warn("Couldn't resolve com-config section. Perhaps the config is damaged?");
    }

    @Override
    public void onEnable() {
        logger.info("Enabling plugin...");

        try {
            supervisor = new Supervisor(this, config);
        } catch (Exception x) {
            logger.error("Failed to construct com.Supervisor", x);
        }

        int delay = getConfig().getInt("com-update-delay");


        if(getConfig().getBoolean("com-run-async", true))
            Bukkit.getScheduler().runTaskTimerAsynchronously(this, supervisor::update, 20, delay * 60 * 20);
        else
            Bukkit.getScheduler().runTaskTimer(this, supervisor::update, 20, delay * 60 * 20);

        logger.debug("Updating every %s", (delay == 1 ? "minute" : delay + " minutes"));

    }

    @Override
    public void onDisable() {
        logger.info("Disabling plugin...");
        try {
            supervisor.halt();
        } catch (Exception x) {
            logger.error("Failed to halt com.Supervisor", x);
        }
    }


    /**
     * @return Custom Logger
     */
    public Logger logger() {
        return logger;
    }
}
