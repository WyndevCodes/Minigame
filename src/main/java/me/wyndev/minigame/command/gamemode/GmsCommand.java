package me.wyndev.minigame.command.gamemode;

import me.wyndev.minigame.Main;
import me.wyndev.minigame.command.MinigameCommand;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;

public class GmsCommand extends MinigameCommand {
    public GmsCommand() {
        super("gms");
    }

    @Override
    public void executeDefault(CommandSender sender, CommandContext context) {
        if (sender instanceof Player player) {
            player.setGameMode(GameMode.SURVIVAL);
            player.sendMessage(Main.MINI_MESSAGE.deserialize("<white>Your gamemode has been updated to <green>survival<white>!"));
        }
    }

    @Override
    public int requiredPermissionLevel() {
        return 4;
    }
}
