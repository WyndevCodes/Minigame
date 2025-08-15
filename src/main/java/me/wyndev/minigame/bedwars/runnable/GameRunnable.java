package me.wyndev.minigame.bedwars.runnable;

import lombok.Getter;
import me.wyndev.minigame.Main;
import me.wyndev.minigame.bedwars.data.state.GameState;
import me.wyndev.minigame.bedwars.util.Msg;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.Task;

import java.time.Duration;

public class GameRunnable {
    private final Task task;
    @Getter
    private static int timeLeft = GameState.PLAY.getDuration() + 1;

    public GameRunnable() {
        task = MinecraftServer.getSchedulerManager().buildTask(this::run).repeat(Duration.ofSeconds(1)).schedule();
    }


    public void run() {
        timeLeft--;
        if (timeLeft <= -1) {
            timeLeft = Main.getGameManager().nextGameState().getDuration();
        }
        if (Main.getGameManager().getGameState() == GameState.BED_DESTRUCTION && timeLeft == 59) {
            Main.getGameManager().forEachBedwarsMinigamePlayer(player -> player.sendMessage(Msg.yellow("All beds will be destroyed in <red>60 seconds!")));
        }
    }

    public void stop() {
        task.cancel();
    }

    public static String getFormattedTimeLeft() {
        Duration duration = Duration.ofSeconds(timeLeft);
        long minutes = duration.toMinutes();
        long seconds = duration.minusMinutes(minutes).getSeconds();
        return minutes + ":" + (seconds < 10 ? "0" + seconds : seconds);
    }
}
