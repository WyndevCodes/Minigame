package me.wyndev.minigame.bedwars.listener;

import io.github.togar2.pvp.events.FinalDamageEvent;
import me.wyndev.minigame.Main;
import me.wyndev.minigame.bedwars.player.BedwarsPlayer;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class DamageListener extends BedwarsEvent<FinalDamageEvent> {

    public DamageListener(Instance gameWorld) {
        super(gameWorld);
    }

    public void onDamage(FinalDamageEvent event) {
        BedwarsPlayer player = Main.getGameManager().getBedwarsPlayerFor(event.getEntity());
        if (player == null) return;
        
        if (Main.getGameManager().getSpectators().contains(player.getUuid())) {
            event.setCancelled(true);
            return;
        }
        if (!Main.getGameManager().STARTED) {
            event.setCancelled(true);
            return;
        }

        if (event.getDamage().getAttacker() instanceof LivingEntity livingEntity) {
            BedwarsPlayer damager = Main.getGameManager().getBedwarsPlayerFor(livingEntity);
            if (damager != null) {
                Main.getGameManager().getStatsManager().getStats(damager.getUuid()).addDamageDealt(event.getDamage().getAmount());
            }
        }
        Main.getGameManager().getStatsManager().getStats(player.getUuid()).addDamageTaken(event.getDamage().getAmount());
    }

    @Override
    public void onEvent(FinalDamageEvent event) {
        onDamage(event);
    }

    @Override
    public @Nullable Instance instanceFromEvent(FinalDamageEvent event) {
        return event.getInstance();
    }

    @Override
    public @NotNull Class<FinalDamageEvent> eventType() {
        return FinalDamageEvent.class;
    }
}
