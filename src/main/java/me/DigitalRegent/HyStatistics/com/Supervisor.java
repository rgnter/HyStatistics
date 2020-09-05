package me.DigitalRegent.HyStatistics.com;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.HikariPool;
import lombok.Getter;
import me.DigitalRegent.HyStatistics.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class Supervisor {

    private @NotNull Plugin instance;

    private @NotNull String table = "hyStatistics";

    @Getter
    private @NotNull HikariConfig config;

    @Getter
    private @NotNull HikariDataSource source;

    /**
     * Default constructor
     *
     * @param instance Instance of Plugin
     * @param config   Config of DataSource
     */
    public Supervisor(@NotNull Plugin instance, @NotNull HikariConfig config) {
        this.instance = instance;
        this.config = config;

        try {
            source = new HikariDataSource(config);
        } catch (HikariPool.PoolInitializationException x) {
            instance.logger().error("Failed to construct Data Source", x);
        }
    }

    /**
     * Updates data Source
     */
    public synchronized void update() {
        long start = System.nanoTime();

        if (source != null)
            try (Connection connection = source.getConnection()) {
                Statement tableStatement = connection.createStatement();
                tableStatement.execute("CREATE TABLE IF NOT EXISTS `" + table + "`" +
                        "(" +
                        "`uuid` varchar(36) primary key, " +
                        "`name` varchar(36)," +
                        "`stat_diamonds_mined` int," +
                        "`stat_killed_players` int," +
                        "`stat_deaths` int," +
                        "`stat_time_played` bigint, " +
                        "`stat_balance` bigint" +
                        ")");

                Bukkit.getOnlinePlayers().forEach(player -> {
                    int diamondsMined = player.getStatistic(Statistic.MINE_BLOCK, Material.DIAMOND_ORE);
                    int playerKills = player.getStatistic(Statistic.PLAYER_KILLS);
                    int playerDeaths = player.getStatistic(Statistic.DEATHS);
                    int time = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;
                    long balance = -1;

                    try {
                        Class.forName("com.earth2me.essentials.api.Economy");
                        try {
                            balance = com.earth2me.essentials.api.Economy.getMoneyExact(player.getName()).toBigInteger().longValue();
                        } catch (Exception ignored) {
                        }
                    } catch (ClassNotFoundException ignored) {
                    }

                    try (Statement playerData = connection.createStatement()) {
                        ResultSet set = playerData.executeQuery("SELECT * from `"
                                + table +
                                "` where `uuid` = '"
                                + player.getUniqueId().toString() +
                                "'");
                        // exists
                        if (set.next()) {
                            playerData.execute(
                                    "update `" + table + "` set " +
                                            "`name` = '" + player.getName() + "', " +
                                            "`stat_diamonds_mined` = '" + diamondsMined + "', " +
                                            "`stat_killed_players` = '" + playerKills + "'," +
                                            "`stat_deaths` = '" + playerDeaths + "'," +
                                            "`stat_time_played` = '" + time + "'," +
                                            "`stat_balance` = '" + balance + "'" +
                                            " WHERE `uuid`='" + player.getUniqueId().toString() + "'");
                        } else {
                            playerData.execute(
                                    "insert into `" + table + "` " + "values " +
                                            "('" +
                                            player.getUniqueId().toString() + "', '" +
                                            player.getName() + "', '" +
                                            diamondsMined + "', '" +
                                            playerKills + "', '" +
                                            playerDeaths + "', '" +
                                            time + "', '" +
                                            balance +
                                            "')");
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                });
                if (instance.getConfig().getBoolean("show-time-consumption", false))
                    instance.logger().debug("Update took: %.4fs", ((System.nanoTime() - start) / 1000000000f));
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    /**
     * Closes the data Source
     */
    public void halt() {
        if (source != null && !source.isClosed())
            source.close();
    }
}
