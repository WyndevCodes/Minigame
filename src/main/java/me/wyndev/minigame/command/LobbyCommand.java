package me.wyndev.minigame.command;

import me.wyndev.minigame.Main;
import me.wyndev.minigame.player.MinigamePlayer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.CommandContext;

public class LobbyCommand extends MinigameCommand {
    public LobbyCommand() {
        super("lobby", "hub");
    }

    @Override
    public void executeDefault(CommandSender sender, CommandContext context) {
        if (sender instanceof MinigamePlayer player) {
            player.sendMessage(Main.MINI_MESSAGE.deserialize("<gray>Sending you to the main lobby..."));
            player.sendToLobby();
        }
    }

    @Override
    public int requiredPermissionLevel() {
        return 0;
    }
}
