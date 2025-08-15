package me.wyndev.minigame.bedwars.listener;

import lombok.AllArgsConstructor;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.trait.CancellableEvent;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
public abstract class BedwarsEvent<T extends Event> implements EventListener<T> {

    private Instance gameWorld;

    protected boolean isInstanceNot(Instance instance) {
        return gameWorld == null || !gameWorld.equals(instance);
    }

    public abstract void onEvent(T event);

    @Nullable
    public abstract Instance instanceFromEvent(T event);

    @NotNull
    @Override
    public Result run(@NotNull T event) {
        final Instance instance = instanceFromEvent(event);
        if (instance != null && isInstanceNot(instance)) return Result.INVALID;

        // Event cancellation
        if (event instanceof CancellableEvent cancellableEvent && cancellableEvent.isCancelled()) return Result.INVALID;

        onEvent(event);
        return Result.SUCCESS;
    }

}
