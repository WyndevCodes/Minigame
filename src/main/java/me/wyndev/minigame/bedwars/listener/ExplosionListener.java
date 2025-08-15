package me.wyndev.minigame.bedwars.listener;

import io.github.togar2.pvp.events.ExplosionEvent;
import net.minestom.server.entity.EntityType;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings("unused")
//this does not currently work because minestom pvp explosions are broken
public class ExplosionListener extends BedwarsEvent<ExplosionEvent> {

    public ExplosionListener(Instance gameWorld) {
        super(gameWorld);
    }

    public void onExplode(ExplosionEvent event) {
        if (event.getDamageObject().getAttacker() == null) return;
        if (event.getDamageObject().getAttacker().getEntityType().equals(EntityType.TNT) || event.getDamageObject().getAttacker().getEntityType().equals(EntityType.FIREBALL)) {
            event.getDamageObject().setAmount(0f);
            event.setCancelled(false);
            event.getAffectedBlocks().forEach(point -> {
                Block block = event.getInstance().getBlock(point);
                if (block.hasNbt() && Objects.requireNonNull(block.nbt()).getBoolean("placedByPlayer")) return;
                event.getAffectedBlocks().remove(point);
            });
        }
    }

    @Override
    public void onEvent(ExplosionEvent event) {
        onExplode(event);
    }

    @Override
    public @Nullable Instance instanceFromEvent(ExplosionEvent event) {
        return event.getInstance();
    }

    @Override
    public @NotNull Class<ExplosionEvent> eventType() {
        return ExplosionEvent.class;
    }
}
