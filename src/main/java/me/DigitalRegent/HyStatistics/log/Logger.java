package me.DigitalRegent.HyStatistics.log;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class Logger {


    @Getter
    private @NotNull
    String prefix = "HyStatistic";

    @Getter
    private @NotNull
    String delimiter = " :: ";

    // Prefix colors
    private @NotNull
    ChatColor[] prefixColors = {ChatColor.YELLOW};

    // Delimiter colors
    private @NotNull
    ChatColor[] delimiterColors = {ChatColor.GRAY};

    private boolean enableTrace = true;
    private boolean enableDebug = true;


    /**
     * Full constructor
     *
     * @param prefix          Prefix
     * @param delimiter       Prefix's delimiter
     * @param prefixColors    Prefix colors
     * @param delimiterColors Delimiter's colors
     */
    public Logger(@NotNull String prefix, @NotNull String delimiter, @NotNull ChatColor[] prefixColors, @NotNull ChatColor[] delimiterColors) {
        this.prefix = prefix;
        this.delimiter = delimiter;
        this.prefixColors = prefixColors;
        this.delimiterColors = delimiterColors;
    }

    /**
     * Constructor
     *
     * @param prefix       Prefix
     * @param prefixColors Prefix's colors
     */
    public Logger(@NotNull String prefix, @NotNull ChatColor[] prefixColors) {
        this.prefix = prefix;
        this.prefixColors = prefixColors;
    }

    /**
     * Empty constructor with default values
     */
    public Logger() {
    }


    /**
     * Sends {@param message} to the console as DEBUG log type.
     *
     * @param message Message
     */
    public void debug(@NotNull Object message) {
        debug(message, new Object());
    }

    /**
     * Sends {@param message} to the console as DEBUG log type. Formats {@param args} into the message if possible.
     *
     * @param message Message
     * @param args    Parameters
     */
    public void debug(@NotNull Object message, @Nullable Object... args) {
        if (enableDebug)
            send(LogType.DEBUG, message, args);
    }

    /**
     * Sends {@param message} to the console as INFO log type.
     *
     * @param message Message
     */
    public void info(@NotNull Object message) {
        info(message, new Object());
    }

    /**
     * Sends {@param message} to the console as INFO log type. Formats {@param args} into the message if possible.
     *
     * @param message Message
     * @param args    Parameters
     */
    public void info(@NotNull Object message, @Nullable Object... args) {
        send(LogType.INFO, message, args);
    }

    /**
     * Sends {@param message} to the console as WARN log type.
     *
     * @param message Message
     */
    public void warn(@NotNull Object message) {
        warn(message, new Object());
    }

    /**
     * Sends {@param message} to the console as WARN log type. Formats {@param args} into the message if possible.
     *
     * @param message Message
     * @param args    Parameters
     */
    public void warn(@NotNull Object message, @Nullable Object... args) {
        send(LogType.WARN, message, args);
    }

    /**
     * Sends {@param message} to the console as ERROR log type.
     *
     * @param message Message
     */
    public void error(@NotNull Object message) {
        error(message, new Object());
    }


    /**
     * Sends {@param message} to the console as ERROR log type. Formats {@param args} into the message if possible.
     *
     * @param message Message
     * @param args    Parameters
     */
    public void error(@NotNull Object message, @Nullable Object... args) {
        send(LogType.ERROR, message, args);
    }

    /**
     * Sends {@param message} to the console as ERROR log type.
     *
     * @param x       Exception
     * @param message Message
     */
    public void error(@NotNull Object message, @NotNull Exception x) {
        error(message, x, new Object());
    }


    /**
     * Sends {@param message} to the console as ERROR log type. Formats {@param args} into the message if possible.
     *
     * @param message Message
     * @param x       Exception
     * @param args    Parameters
     */
    public void error(@NotNull Object message, @NotNull Exception x, @Nullable Object... args) {
        send(LogType.ERROR, message, args);
        trace(x);
    }

    /**
     * Logs exception trace into console
     * @param x
     */
    private void trace(@NotNull Exception x) {
        if(enableTrace) {
            System.err.println("Exception: " + x);
            System.err.println("Stack trace: ");
            x.printStackTrace();
            System.out.println(" ");
        }
    }

    private void send(@NotNull LogType type, @NotNull Object message, @Nullable Object[] args) {
        String msg = message.toString();

        // If possible replace args in string
        if (args != null && args.length > 0)
            msg = String.format(msg, args);

        Bukkit.getServer().getConsoleSender().sendMessage(toColoredString(prefixColors) + prefix +
                toColoredString(delimiterColors) + delimiter +
                toColoredString(type.getColors()) + msg);
    }


    /**
     * Represents various log types
     */
    public enum LogType {
        DEBUG(ChatColor.GRAY), INFO(ChatColor.WHITE), WARN(ChatColor.YELLOW), ERROR(ChatColor.RED);

        @Getter
        private ChatColor[] colors;

        LogType(@NotNull ChatColor... colors) {
            this.colors = colors;
        }
    }

    /**
     * Enables debug
     */
    public void enableDebug() {
        enableDebug = true;
    }

    /**
     * Disables debug
     */
    public void disableDebug() {
        enableDebug = false;
    }

    /**
     * Sets enableDebug
     * @param b boolean value
     */
    public void setEnableDebug(boolean b) {
        enableDebug = b;
    }

    /**
     * Checks if debug is enabled
     * @return True/False
     */
    public boolean isDebugEnabled() {
        return enableDebug;
    }

    /**
     * Enables trace
     */
    public void enableTrace() {
        enableTrace = true;
    }

    /**
     * Disables trace
     */
    public void disableTrace() {
        enableTrace = false;
    }

    /**
     * Sets enableTrace.
     * @param b boolean value
     */
    public void setEnableTrace(boolean b) {
        enableTrace = b;
    }

    /**
     * Checks if trace is enabled
     * @return True/False
     */
    public boolean isTraceEnabled() {
        return enableTrace;
    }

    /**
     * Turns color array into colored String.
     * @param colors Color Array
     * @return Colored String
     */
    private String toColoredString(@NotNull ChatColor[] colors) {
        StringBuilder colored = new StringBuilder();
        Arrays.asList(colors).forEach(color -> colored.append("§" + org.bukkit.ChatColor.valueOf(color.getName().toUpperCase()).getChar()));
        return colored.toString();
    }

}
