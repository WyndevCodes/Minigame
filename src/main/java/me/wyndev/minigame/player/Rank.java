package me.wyndev.minigame.player;

import me.wyndev.minigame.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.scoreboard.Team;
import net.minestom.server.scoreboard.TeamManager;

public record Rank(String id, String prefix, NamedTextColor teamColor, int sortOrder, int permissionLevel) {

    public static Rank DEFAULT = new Rank(
            "default",
            "<dark_gray>[<gray>Default<dark_gray>] <gray>",
            NamedTextColor.GRAY,
            0,
            0
    );

    public Component prefixComponent() {
        return Main.MINI_MESSAGE.deserialize(prefix);
    }

    public Team getTeam(TeamManager teamManager) {
        return getTeam(teamManager, TeamsPacket.CollisionRule.NEVER, TeamsPacket.NameTagVisibility.ALWAYS);
    }

    public Team getTeam(TeamManager teamManager, TeamsPacket.CollisionRule collisionRule, TeamsPacket.NameTagVisibility tagVisibility) {
        Team existing = teamManager.getTeam(id);
        if (existing != null) return existing;
        return teamManager.createBuilder(id)
                .teamColor(teamColor)
                .prefix(prefixComponent())
                .collisionRule(collisionRule)
                .updateNameTagVisibility(tagVisibility)
                .build();
    }

}
