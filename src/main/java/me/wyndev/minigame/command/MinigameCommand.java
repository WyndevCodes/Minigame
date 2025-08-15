package me.wyndev.minigame.command;

import me.wyndev.minigame.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class MinigameCommand extends Command {

    public MinigameCommand(@NotNull String name, @Nullable String... aliases) {
        super(name, aliases);
        setCondition((sender, commandString) -> sender instanceof Player player && player.getPermissionLevel() >= requiredPermissionLevel());

        setDefaultExecutor((sender, context) -> {
            if (!hasPermission(sender)) return;

            executeDefault(sender, context);
        });
    }

    public abstract void executeDefault(CommandSender sender, CommandContext context);

    /**
     * Gets the required permission level to use this command.
     * Number between 0 and 4, where 4 is operator status and
     * 0 is base player status.
     */
    public abstract int requiredPermissionLevel();

    public boolean hasPermission(CommandSender sender) {
        if (sender instanceof Player player && player.getPermissionLevel() < requiredPermissionLevel()) {
            sender.sendMessage(Component.text("You do not have permission to perform this command!").color(TextColor.color(0xFFFFFF)));
            return false;
        }
        return true;
    }

    public boolean hasPermission(CommandSender sender, String noPermMessage) {
        if (sender instanceof Player player && player.getPermissionLevel() < requiredPermissionLevel()) {
            sender.sendMessage(Main.MINI_MESSAGE.deserialize(noPermMessage));
            return false;
        }
        return true;
    }

}
