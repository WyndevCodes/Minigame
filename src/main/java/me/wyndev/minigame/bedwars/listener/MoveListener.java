package me.wyndev.minigame.bedwars.listener;

import me.wyndev.minigame.Main;
import me.wyndev.minigame.bedwars.player.BedwarsPlayer;
import me.wyndev.minigame.bedwars.util.Msg;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MoveListener extends BedwarsEvent<PlayerMoveEvent> {

    public MoveListener(Instance gameWorld) {
        super(gameWorld);
    }

    public void onMove(PlayerMoveEvent event) {
        if (!Main.getGameManager().isPlayerInBedwars(event.getPlayer())) return;
        if (!Main.getGameManager().STARTED) return;
        if (event.getNewPosition().y() <= -40) {
            if (Main.getGameManager().getSpectators().contains(event.getPlayer().getUuid())) {
                event.getPlayer().teleport(Main.getGameManager().getWorldConfig().spawnPlatformCenter);
                event.setCancelled(true);
            } else {
                Main.getGameManager().kill(event.getPlayer(), null, DamageType.OUT_OF_WORLD);
                event.setCancelled(true);
            }
        }
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        Pos spawn = Main.getGameManager().getWorldConfig().spawnPlatformCenter;
        if (distanceSquared(event.getNewPosition().x(), spawn.x(), event.getNewPosition().z(), spawn.z()) > 150 * 150 || event.getNewPosition().y() >= 50) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(Msg.whoops("You cannot travel too far from the map!"));
        }
    }

    private static double distanceSquared(double x1, double x2, double z1, double z2) {
        return Math.pow(x1 - x2, 2) + Math.pow(z1 - z2, 2);
    }

    @Override
    public void onEvent(PlayerMoveEvent event) {
        onMove(event);
    }

    @Override
    public @Nullable Instance instanceFromEvent(PlayerMoveEvent event) {
        return event.getInstance();
    }

    @Override
    public @NotNull Class<PlayerMoveEvent> eventType() {
        return PlayerMoveEvent.class;
    }
}
