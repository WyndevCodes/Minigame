package me.wyndev.minigame.player;

import io.github.togar2.pvp.player.CombatPlayerImpl;
import net.minestom.server.MinecraftServer;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;

public class MinigamePlayer extends CombatPlayerImpl {

    private Rank rank;

    public MinigamePlayer(@NotNull PlayerConnection playerConnection, GameProfile profile) {
        super(playerConnection, profile);
        this.rank = Rank.DEFAULT;

        setTeam(this.rank.getTeam(MinecraftServer.getTeamManager()));
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }
}
