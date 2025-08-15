package me.wyndev.minigame.sideboard;

import me.wyndev.minigame.bedwars.util.Msg;
import me.wyndev.minigame.player.MinigamePlayer;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;

import java.util.List;

/**
 * The default implementation of {@link SideboardCreator}; creating a baseline sideboard for Cytosis.
 */
public class DefaultSidebarCreator implements SideboardCreator {

    @Override
    public Sideboard sideboard(MinigamePlayer player) {
        Sideboard sideboard = new Sideboard(player);
        sideboard.updateLines(lines(player));
        return sideboard;
    }

    @Override
    public List<Component> lines(MinigamePlayer player) {
        try {
            return List.of(
                    Msg.mm("<green>Players in world: " + MinecraftServer.getConnectionManager().getOnlinePlayers().size()),
                    Msg.mm(""),
                    Msg.mm("<green>Rank: ").append(player.getRank().prefixComponent()),
                    Msg.mm(""),
                    Msg.mm("<yellow>mc.cytonic.net")
            );
        } catch (Exception e) {
            return List.of(Msg.mm("<red>Failed to get server information!"));
        }
    }

    @Override
    public Component title(MinigamePlayer player) {
        return Msg.mm("<yellow><bold>Cytosis</bold></yellow>");
    }
}
