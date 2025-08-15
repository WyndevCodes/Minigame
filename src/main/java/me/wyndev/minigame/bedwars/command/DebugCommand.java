package me.wyndev.minigame.bedwars.command;

import me.wyndev.minigame.Main;
import me.wyndev.minigame.bedwars.data.state.GameState;
import me.wyndev.minigame.bedwars.menu.shop.BlocksShopMenu;
import me.wyndev.minigame.bedwars.runnable.WaitingRunnable;
import me.wyndev.minigame.bedwars.util.Msg;
import me.wyndev.minigame.command.MinigameCommand;
import me.wyndev.minigame.player.MinigamePlayer;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;

/**
 * Bedwars debug command.
 * @author webhead1104/Cytonic
 */
public class DebugCommand extends MinigameCommand {

    public DebugCommand() {
        super("debug");

        var debugArgument = ArgumentType.Word("debug").from("start", "forceStart", "end", "cleanup", "listteams", "freeze", "f", "itemshop", "teaminfo");
        debugArgument.setCallback((sender, exception) -> sender.sendMessage(Msg.whoops("The command " + exception.getInput() + " is invalid!")));
        debugArgument.setSuggestionCallback((commandSender, commandContext, suggestion) -> {
            suggestion.addEntry(new SuggestionEntry("start", Msg.green("Starts the game!")));
            suggestion.addEntry(new SuggestionEntry("forceStart", Msg.green("Force starts the game!")));
            suggestion.addEntry(new SuggestionEntry("end", Msg.green("Ends the game!")));
            suggestion.addEntry(new SuggestionEntry("cleanup", Msg.green("Cleans up the game!")));
            suggestion.addEntry(new SuggestionEntry("listteams", Msg.green("Lists all the teams!")));
            suggestion.addEntry(new SuggestionEntry("freeze", Msg.green("Freezes the game!")));
            suggestion.addEntry(new SuggestionEntry("f", Msg.green("Freezes the game!")));
            suggestion.addEntry(new SuggestionEntry("itemshop", Msg.green("Opens the item shop!")));
            suggestion.addEntry(new SuggestionEntry("teaminfo", Msg.green("Shows information about the teams!")));
        });

        addSyntax((sender, context) -> {
            if (!hasPermission(sender)) return;

            if (sender instanceof MinigamePlayer player) {
                String command = context.get(debugArgument);

                switch (command.toLowerCase()) {
                    case "start" -> {
                        if (Main.getGameManager().STARTED) {
                            player.sendMessage(Msg.red("The game has already been started! Use '/debug stop' to end it!"));
                            player.sendMessage(Msg.red("Starting the game anyway!"));
                        }
                        Main.getGameManager().setGameState(GameState.STARTING);
                        Main.getGameManager().setWaitingRunnable(new WaitingRunnable());
                    }
                    case "forcestart" -> {
                        if (Main.getGameManager().STARTED) {
                            player.sendMessage(Msg.red("The game has already been started! Use '/debug stop' to end it!"));
                        }
                        if (Main.getGameManager().getWaitingRunnable() != null) {
                            Main.getGameManager().getWaitingRunnable().stop();
                            Main.getGameManager().setWaitingRunnable(null);
                        }
                        Main.getGameManager().start();
                    }
                    case "end" -> {
                        player.sendMessage(Msg.green("Ending game!"));
                        Main.getGameManager().end();
                    }
                    case "cleanup" -> {
                        player.sendMessage(Msg.green("Cleaning up game!"));
                        Main.getGameManager().cleanup();
                    }
                    case "listteams" ->
                            Main.getGameManager().getTeams().forEach(team -> player.sendMessage(Msg.mm(team.getPrefix() + team.getDisplayName())));
                    case "freeze", "f" -> {
                        if (Main.getGameManager().getGameState() != GameState.FROZEN) {
                            Main.getGameManager().forEachBedwarsMinigamePlayer((player1) -> player1.sendMessage(Msg.yellow("The game is now <aqua><bold>FROZEN<reset><yellow>!")));
                            Main.getGameManager().freeze();
                        } else {
                            Main.getGameManager().forEachBedwarsMinigamePlayer((player1) -> player1.sendMessage(Msg.yellow("The game is now <gold><bold>THAWED<reset><yellow>!")));
                            Main.getGameManager().thaw();
                        }
                    }
                    case "itemshop" -> {
                        if (!Main.getGameManager().STARTED) {
                            player.sendMessage(Msg.redSplash("!! WARNING !!", "The game has not been started. Some shop pages may not work!"));
                        }
                        new BlocksShopMenu().open(player);
                    }
                    case "teaminfo" -> Main.getGameManager().getTeams().forEach(team -> {
                        player.sendMessage(Msg.mm("<%s><b>Team:</b> %s", team.getColor(), team.getName()));
                        player.sendMessage(Msg.mm("Alive: %s", team.isAlive()));
                        player.sendMessage(Msg.mm("Bed: %s", team.hasBed()));
                        player.sendMessage(Msg.mm("MCTeam: %s", team.getMcTeam().getTeamName()));
                        player.sendMessage(Msg.mm("Players:"));
                        team.getPlayers().forEach(teamPlayer -> {
                            player.sendMessage(Msg.mm(" Name: %s", teamPlayer.getUsername()));
                            player.sendMessage(Msg.mm(" Armor Level: %s", teamPlayer.getArmorLevel().name()));
                            player.sendMessage(Msg.mm(" Axe Level: %s", teamPlayer.getAxeLevel().name()));
                            player.sendMessage(Msg.mm(" Pickaxe Level: %s", teamPlayer.getPickaxeLevel().name()));
                            player.sendMessage(Msg.mm(" Shears: %s", teamPlayer.hasShears()));
                            player.sendMessage(Msg.mm(" Alive: %s", teamPlayer.isAlive()));
                            player.sendMessage(Msg.mm(" Respawning: %s", teamPlayer.isRespawning()));
                        });
                    });
                }
            }
        }, debugArgument);
    }

    @Override
    public void executeDefault(CommandSender sender, CommandContext context) {
        sender.sendMessage(Msg.whoops("You must specify a command!"));
    }

    @Override
    public int requiredPermissionLevel() {
        return 4;
    }
}
