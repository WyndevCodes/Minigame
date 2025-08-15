package me.wyndev.minigame.bedwars.listener;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.player.PlayerPreEatEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

public class PotionDrinkListener extends BedwarsEvent<PlayerPreEatEvent> {

    public PotionDrinkListener(Instance gameWorld) {
        super(gameWorld);
    }

    public void onEat(PlayerPreEatEvent e) {
        if (e.getItemStack().material() == Material.POTION || e.getItemStack().material() == Material.GLASS_BOTTLE) {
            MinecraftServer.getSchedulerManager().buildTask(() -> e.getPlayer().setItemInHand(e.getHand(), ItemStack.AIR)).delay(Duration.ofMillis(100));
        }
    }

    @Override
    public void onEvent(PlayerPreEatEvent event) {
        onEat(event);
    }

    @Override
    public @Nullable Instance instanceFromEvent(PlayerPreEatEvent event) {
        return event.getInstance();
    }

    @Override
    public @NotNull Class<PlayerPreEatEvent> eventType() {
        return PlayerPreEatEvent.class;
    }
}
