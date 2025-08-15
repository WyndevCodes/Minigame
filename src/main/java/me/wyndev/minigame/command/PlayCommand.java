package me.wyndev.minigame.command;

import me.wyndev.minigame.Main;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.entity.Player;

public class PlayCommand extends MinigameCommand {
    public PlayCommand() {
        super("play");
    }

    @Override
    public void executeDefault(CommandSender sender, CommandContext context) {
        if (sender instanceof Player player) {
            player.sendMessage(Main.MINI_MESSAGE.deserialize("<gray>Joining bedwars!"));
            Main.getGameManager().queuePlayer(player);
        }
    }

    @Override
    public int requiredPermissionLevel() {
        return 0;
    }
}
