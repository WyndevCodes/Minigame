package me.wyndev.minigame.command.gamemode;

import me.wyndev.minigame.Main;
import me.wyndev.minigame.command.MinigameCommand;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;

public class GmsCommand extends MinigameCommand {
    public GmsCommand() {
        super("gms");

        setDefaultExecutor((sender, context) -> {
            if (!hasPermission(sender)) return;

            if (sender instanceof Player player) {
                player.setGameMode(GameMode.SURVIVAL);
                player.sendMessage(Main.MINI_MESSAGE.deserialize("<white>Your gamemode has been updated to <green>survival<white>!"));
            }
        });
    }

    @Override
    public int requiredPermissionLevel() {
        return 4;
    }
}
