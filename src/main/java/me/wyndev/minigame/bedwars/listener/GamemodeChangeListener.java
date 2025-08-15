package me.wyndev.minigame.bedwars.listener;

import io.github.togar2.pvp.utils.PotionFlags;
import me.wyndev.minigame.Main;
import me.wyndev.minigame.bedwars.util.Items;
import net.minestom.server.entity.EquipmentSlot;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerGameModeChangeEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class GamemodeChangeListener extends BedwarsEvent<PlayerGameModeChangeEvent> {

    public GamemodeChangeListener(Instance gameWorld) {
        super(gameWorld);
    }

    public void onGamemodeChange(PlayerGameModeChangeEvent event) {
        Player player = event.getPlayer();
        if (event.getNewGameMode() == GameMode.SPECTATOR) {
            player.getInventory().clear();
            player.getInventory().setEquipment(EquipmentSlot.BOOTS, player.getHeldSlot(), Items.SPECTATOR_ARMOR);
            player.getInventory().setEquipment(EquipmentSlot.LEGGINGS, player.getHeldSlot(), Items.SPECTATOR_ARMOR);
            player.getInventory().setEquipment(EquipmentSlot.CHESTPLATE, player.getHeldSlot(), Items.SPECTATOR_ARMOR);
            player.setHealth(20);
            event.setCancelled(true);
            player.setGameMode(GameMode.ADVENTURE);
            //fixme
            player.addEffect(new Potion(PotionEffect.INVISIBILITY, PotionFlags.create(false, false, true), Potion.INFINITE_DURATION));
            player.setAllowFlying(true);
            player.setFlying(true);
            Main.getGameManager().getSpectators().add(player.getUuid());
            player.getInventory().setItemStack(0, Items.SPECTATOR_TARGET_SELECTOR);
            player.getInventory().setItemStack(4, Items.SPECTATOR_SPEED_SELECTOR);
            player.getInventory().setItemStack(8, Items.SPECTATOR_LOBBY_REQUEST);
        } else {
            // un-ivisafy if they are respawning, etc.
            if (event.getPlayer().getGameMode() == GameMode.ADVENTURE)
                player.getInventory().clear(); // only clear the inventory if they are coming from a spectator
            Main.getGameManager().getSpectators().remove(player.getUuid());
            player.removeEffect(PotionEffect.INVISIBILITY);
            if (event.getNewGameMode() != GameMode.CREATIVE) {
                player.setAllowFlying(false);
                player.setFlying(false);
            }
        }
    }

    @Override
    public void onEvent(PlayerGameModeChangeEvent event) {
        onGamemodeChange(event);
    }

    @Override
    public @Nullable Instance instanceFromEvent(PlayerGameModeChangeEvent event) {
        return event.getInstance();
    }

    @Override
    public @NotNull Class<PlayerGameModeChangeEvent> eventType() {
        return PlayerGameModeChangeEvent.class;
    }
}
