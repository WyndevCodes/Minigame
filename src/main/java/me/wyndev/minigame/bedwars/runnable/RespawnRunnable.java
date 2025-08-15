package me.wyndev.minigame.bedwars.runnable;

import me.wyndev.minigame.Main;
import me.wyndev.minigame.bedwars.player.BedwarsPlayer;
import me.wyndev.minigame.bedwars.util.Msg;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.Task;

import java.time.Duration;

public class RespawnRunnable {
    private final BedwarsPlayer player;
    private final LivingEntity playerWhoDied;
    private final Task task;
    private int timeLeft;

    public RespawnRunnable(int timeLeft, LivingEntity playerWhoDied, BedwarsPlayer player) {
        this.timeLeft = timeLeft;
        this.player = player;
        this.playerWhoDied = playerWhoDied;
        task = MinecraftServer.getSchedulerManager().buildTask(this::run).repeat(Duration.ofSeconds(1)).schedule();
    }


    public void run() {
        timeLeft--;
        if (timeLeft <= 0) {
            task.cancel();
            Main.getGameManager().respawnPlayer(playerWhoDied, player);
            return;
        }

        switch (timeLeft) {
            case 1, 2, 3 -> {
                if (playerWhoDied instanceof Player p) {
                    p.showTitle(Title.title(Msg.yellow("Respawning in " + timeLeft), Component.text(""), Title.Times.times(Duration.ofMillis(0), Duration.ofMillis(1150), Duration.ofSeconds(1))));
                    p.playSound(Sound.sound(SoundEvent.UI_BUTTON_CLICK, Sound.Source.AMBIENT, .8f, 1f));
                }
            }
        }
    }
}
