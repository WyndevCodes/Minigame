package me.wyndev.minigame.bedwars.listener;

import io.github.togar2.pvp.events.EntityPreDeathEvent;
import me.wyndev.minigame.Main;
import me.wyndev.minigame.bedwars.player.BedwarsPlayer;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class DeathListener extends BedwarsEvent<EntityPreDeathEvent> {

    public DeathListener(Instance gameWorld) {
        super(gameWorld);
    }

    public void onDeath(EntityPreDeathEvent event) {
        event.setCancelDeath(true);
        if (!(event.getEntity() instanceof LivingEntity victim)) return;
        BedwarsPlayer player = Main.getGameManager().getBedwarsPlayerFor(victim);
        if (player == null) return;
        if (event.getDamage().getAttacker() instanceof LivingEntity livingEntity) {
            BedwarsPlayer damager = Main.getGameManager().getBedwarsPlayerFor(livingEntity);
            if (damager != null) {
                Main.getGameManager().kill(victim, livingEntity, event.getDamage().getType());
                return;
            }
        }
        Main.getGameManager().kill(victim, null, event.getDamage().getType());
    }

    @Override
    public void onEvent(EntityPreDeathEvent event) {
        onDeath(event);
    }

    @Override
    public @Nullable Instance instanceFromEvent(EntityPreDeathEvent event) {
        return event.getInstance();
    }

    @Override
    public @NotNull Class<EntityPreDeathEvent> eventType() {
        return EntityPreDeathEvent.class;
    }
}
