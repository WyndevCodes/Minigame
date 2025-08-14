package me.wyndev.minigame.command;

import me.wyndev.minigame.Main;
import net.minestom.server.MinecraftServer;

public class StopCommand extends MinigameCommand {
    public StopCommand() {
        super("stop");

        setDefaultExecutor((sender, context) -> {
            if (!hasPermission(sender)) return;

            MinecraftServer.stopCleanly();
            sender.sendMessage(Main.MINI_MESSAGE.deserialize("<red>Stopping server..."));
        });
    }

    @Override
    public int requiredPermissionLevel() {
        return 4;
    }
}
