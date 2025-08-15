package me.wyndev.minigame.bedwars.data.gamecomponent;

import lombok.NoArgsConstructor;
import me.wyndev.minigame.Main;
import me.wyndev.minigame.bedwars.manager.GameManager;
import me.wyndev.minigame.bedwars.runnable.GameRunnable;
import me.wyndev.minigame.bedwars.runnable.WaitingRunnable;
import me.wyndev.minigame.bedwars.util.Msg;
import me.wyndev.minigame.player.MinigamePlayer;
import me.wyndev.minigame.sideboard.Sideboard;
import me.wyndev.minigame.sideboard.SideboardCreator;
import net.kyori.adventure.text.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@NoArgsConstructor
public class Scoreboard implements SideboardCreator {

    @Override
    public Sideboard sideboard(MinigamePlayer player) {
        Sideboard sideboard = new Sideboard(player);
        sideboard.updateLines(lines(player));
        return sideboard;
    }

    @Override
    public List<Component> lines(MinigamePlayer p) {
        List<Component> list = new ArrayList<>();
        try {
            switch (Main.getGameManager().getGameState()) {
                case WAITING -> list = List.of(
                        topLine(),
                        Msg.mm(""),
                        Msg.mm("Map: <green>" + Main.getGameManager().getWorldConfig().mapName),
                        Msg.mm("Players: <green>" + Main.getGameManager().countPlayers() + "/" + Main.getGameManager().getWorldConfig().maxPlayers),
                        Msg.mm(""),
                        Msg.mm("Waiting..."),
                        Msg.mm(""),
                        Msg.mm("Mode: <green>" + Main.getGameManager().getWorldConfig().mode),
                        Msg.mm(""),
                        Msg.yellow("www.cytonic.net")
                );

                case STARTING -> list = List.of(
                        topLine(),
                        Msg.mm(""),
                        Msg.mm("Map: <green>" + Main.getGameManager().getWorldConfig().mapName),
                        Msg.mm("Players: <green>" + Main.getGameManager().countPlayers() + "/" + Main.getGameManager().getWorldConfig().maxPlayers),
                        Msg.mm(""),
                        Msg.mm("Starting in <green>%ds", WaitingRunnable.getTimeLeft()),
                        Msg.mm(""),
                        Msg.mm("Mode: <green>" + Main.getGameManager().getWorldConfig().mode),
                        Msg.mm(""),
                        Msg.yellow("www.cytonic.net")
                );

                case FROZEN -> list = List.of(
                        topLine(),
                        Msg.mm(""),
                        Msg.mm("Map: <green>" + Main.getGameManager().getWorldConfig().mapName),
                        Msg.mm(""),
                        Msg.aqua("<bold>FROZEN"),
                        Msg.mm("Mode: <green>" + Main.getGameManager().getWorldConfig().mode),
                        Msg.mm(""),
                        Msg.yellow("www.cytonic.net")
                );
                case PLAY, DIAMOND_2, EMERALD_2, DIAMOND_3, EMERALD_3, BED_DESTRUCTION, SUDDEN_DEATH -> {
                    List<Component> scoreboardArgs = new ArrayList<>();
                    scoreboardArgs.add(topLine());
                    scoreboardArgs.add(Msg.mm(""));
                    scoreboardArgs.add(Msg.mm("%s in: <green>%s", Main.getGameManager().getGameState().getNext().getDisplayName(), GameRunnable.getFormattedTimeLeft()));
                    scoreboardArgs.add(Msg.mm(""));
                    GameManager gameManager = Main.getGameManager();
                    Optional<Team> playerTeam = gameManager.getPlayerTeam(p);
                    Main.getGameManager().getWorldConfig().teams.values().forEach(team -> {
                        String s = team.getPrefix() + "<reset>" + team.getDisplayName();
                        if (gameManager.getTeamFromColor(team.getColor()).isPresent() && gameManager.getTeamFromColor(team.getColor()).get().isAlive()) {
                            if (playerTeam.isPresent() && playerTeam.get().equals(team)) {
                                scoreboardArgs.add(Msg.mm(s + " <gray>YOU"));
                                return;
                            }
                            if (team.hasBed()) {
                                scoreboardArgs.add(Msg.mm(s + " <green>✔"));
                            } else {
                                scoreboardArgs.add(Msg.mm(s + " <grey>" + team.getAlivePlayers().size()));
                            }
                        } else {
                            scoreboardArgs.add(Msg.mm(s + " <red>✘"));
                        }
                    });
                    scoreboardArgs.add(Msg.mm(""));
                    scoreboardArgs.add(Msg.yellow("www.cytonic.net"));
                    list = scoreboardArgs;
                }
                case ENDED -> list = List.of(
                        topLine(),
                        Msg.mm(""),
                        Msg.mm("Map: <green>" + Main.getGameManager().getWorldConfig().mapName),
                        Msg.mm(""),
                        Msg.mm("The game has ended!"),
                        Msg.mm(""),
                        Msg.mm("Mode: <green>" + Main.getGameManager().getWorldConfig().mode),
                        Msg.mm(""),
                        Msg.yellow("www.cytonic.net")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Component title(MinigamePlayer player) {
        return Msg.yellow("<bold>Bedwars");
    }

    private Component topLine() {
        return Msg.grey("%s <dark_gray>%s", new SimpleDateFormat("M/d/yy").format(Calendar.getInstance().getTime()));
    }
}
