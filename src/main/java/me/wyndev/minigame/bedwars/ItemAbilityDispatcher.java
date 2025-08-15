package me.wyndev.minigame.bedwars;

import lombok.NoArgsConstructor;
import me.wyndev.minigame.bedwars.menu.spectator.SpectatorSelectMenu;
import me.wyndev.minigame.bedwars.menu.spectator.SpectatorSpeedMenu;
import me.wyndev.minigame.player.MinigamePlayer;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.metadata.item.FireballMeta;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.item.ItemStack;

@NoArgsConstructor
public class ItemAbilityDispatcher {

    public void dispatch(String ability, LivingEntity entity, PlayerUseItemEvent event) {
        if (ability.equalsIgnoreCase("FIREBALL")) {
            ItemStack item = entity.getItemInHand(event.getHand());
            entity.setItemInHand(event.getHand(), item.withAmount(item.amount() - 1));
            Entity fireball = new Entity(EntityType.FIREBALL);
            FireballMeta fb = (FireballMeta) fireball.getEntityMeta();
            fb.setShooter(entity);
            fireball.setInstance(entity.getInstance(), entity.getPosition());
            return;
        }
        if (!(entity instanceof MinigamePlayer player)) return;
        switch (ability) {
            case "SPECTATOR_COMPASS" -> new SpectatorSelectMenu().open(player);
            case "SPECTATOR_SPEED_SELECTOR" -> new SpectatorSpeedMenu().open(player);
            case "LOBBY_REQUEST" -> player.sendToLobby();
            default -> { // not an ability
            }
        }
    }
}
