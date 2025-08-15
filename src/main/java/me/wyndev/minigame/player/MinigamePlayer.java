package me.wyndev.minigame.player;

import io.github.togar2.pvp.player.CombatPlayerImpl;
import lombok.Getter;
import lombok.Setter;
import me.wyndev.minigame.Main;
import net.minestom.server.MinecraftServer;
import net.minestom.server.network.player.GameProfile;
import net.minestom.server.network.player.PlayerConnection;
import org.jetbrains.annotations.NotNull;

public class MinigamePlayer extends CombatPlayerImpl {

    @Getter
    @Setter
    private Rank rank;

    public MinigamePlayer(@NotNull PlayerConnection playerConnection, GameProfile profile) {
        super(playerConnection, profile);
        this.rank = Rank.DEFAULT;

        setTeam(this.rank.getTeam(MinecraftServer.getTeamManager()));
    }

    public void sendToLobby() {
        Main.getGameManager().removePlayer(this);
        setInstance(Main.getHub(), Main.getHubSpawn());
    }
}
