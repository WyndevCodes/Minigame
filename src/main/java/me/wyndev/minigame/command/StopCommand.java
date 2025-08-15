package me.wyndev.minigame.command;

import me.wyndev.minigame.Main;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.CommandContext;

public class StopCommand extends MinigameCommand {
    public StopCommand() {
        super("stop");
    }

    @Override
    public void executeDefault(CommandSender sender, CommandContext context) {
        sender.sendMessage(Main.MINI_MESSAGE.deserialize("<red>Stopping server..."));
        Main.shutdown();
        MinecraftServer.stopCleanly();
    }

    @Override
    public int requiredPermissionLevel() {
        return 4;
    }
}
