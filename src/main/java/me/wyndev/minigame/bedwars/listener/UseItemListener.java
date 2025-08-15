package me.wyndev.minigame.bedwars.listener;

import me.wyndev.minigame.Main;
import me.wyndev.minigame.bedwars.player.BedwarsPlayer;
import me.wyndev.minigame.bedwars.util.Items;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UseItemListener extends BedwarsEvent<PlayerUseItemEvent> {

    public UseItemListener(Instance gameWorld) {
        super(gameWorld);
    }

    public void onInteract(PlayerUseItemEvent event) {
        BedwarsPlayer player = Main.getGameManager().getBedwarsPlayerFor(event.getEntity());
        if (player == null) return;

        ItemStack item = event.getPlayer().getItemInHand(event.getHand());
        if (item.hasTag(Items.NAMESPACE)) {
            String key = item.getTag(Items.NAMESPACE);
            Main.getGameManager().getItemAbilityDispatcher().dispatch(key, event.getEntity(), event);
        }
    }

    @Override
    public void onEvent(PlayerUseItemEvent event) {
        onInteract(event);
    }

    @Override
    public @Nullable Instance instanceFromEvent(PlayerUseItemEvent event) {
        return event.getInstance();
    }

    @Override
    public @NotNull Class<PlayerUseItemEvent> eventType() {
        return PlayerUseItemEvent.class;
    }
}
