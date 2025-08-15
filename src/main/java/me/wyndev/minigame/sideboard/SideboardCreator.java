package me.wyndev.minigame.sideboard;

import me.wyndev.minigame.player.MinigamePlayer;
import net.kyori.adventure.text.Component;

import java.util.List;

/**
 * An interface for creating sideboards.
 */
public interface SideboardCreator {
    /**
     * Creates the sideboard for the player
     *
     * @param player The player to create the sideboard for
     * @return The Sideboard for the player
     */
    Sideboard sideboard(MinigamePlayer player);

    /**
     * A method to create the lines for the sideboard
     *
     * @param player The player
     * @return The list of components
     */
    List<Component> lines(MinigamePlayer player);

    /**
     * Creates the title for the sideboard
     *
     * @param player The player
     * @return The title in Component form
     */
    Component title(MinigamePlayer player);
}