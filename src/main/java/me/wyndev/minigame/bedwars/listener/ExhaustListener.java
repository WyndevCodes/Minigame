package me.wyndev.minigame.bedwars.listener;

import io.github.togar2.pvp.events.PlayerExhaustEvent;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class ExhaustListener extends BedwarsEvent<PlayerExhaustEvent> {

    public ExhaustListener(Instance gameWorld) {
        super(gameWorld);
    }

    @Override
    public void onEvent(PlayerExhaustEvent event) {
        event.setCancelled(true);
    }

    @Override
    public @Nullable Instance instanceFromEvent(PlayerExhaustEvent event) {
        return event.getInstance();
    }

    @Override
    public @NotNull Class<PlayerExhaustEvent> eventType() {
        return PlayerExhaustEvent.class;
    }
}
