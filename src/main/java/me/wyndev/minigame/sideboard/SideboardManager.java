package me.wyndev.minigame.sideboard;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.wyndev.minigame.player.MinigamePlayer;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A manager class for sideboards
 */
@NoArgsConstructor
public class SideboardManager {
    private final Map<MinigamePlayer, Sideboard> sideboards = new ConcurrentHashMap<>();
    @Getter
    @Nullable
    private Task task = null;
    @Getter
    @Setter
    private SideboardCreator sideboardCreator = new DefaultSidebarCreator();

    /**
     * Adds a player to the sideboard manager
     *
     * @param player the player
     */
    public void addPlayer(MinigamePlayer player) {
        sideboards.put(player, sideboardCreator.sideboard(player));
    }

    /**
     * Removes a player from the sideboard manager
     *
     * @param player the player
     */
    public void removePlayer(MinigamePlayer player) {
        sideboards.remove(player);
    }

    public void updatePlayersNow() {
        sideboards.forEach((player, sideboard) -> {
            if (!player.isOnline()) {
                sideboard.delete();
                removePlayer(player);
                return;
            }
            sideboard.updateLines(sideboardCreator.lines(player));
            sideboard.updateTitle(sideboardCreator.title(player));
        });
    }

    /**
     * schedule the sideboard updater.
     */
    public void autoUpdateBoards(TaskSchedule schedule) {
        task = MinecraftServer.getSchedulerManager()
                .buildTask(this::updatePlayersNow)
                .repeat(schedule)
                .schedule();
    }

    /**
     * Shuts down the repeating task. It can be reenabled with {@link SideboardManager#autoUpdateBoards(TaskSchedule)}
     */
    public void cancelUpdates() {
        if (task == null) return;
        task.cancel();
        task = null;
    }
}
