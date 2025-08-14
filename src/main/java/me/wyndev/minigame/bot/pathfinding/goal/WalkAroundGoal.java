package me.wyndev.minigame.bot.pathfinding.goal;

import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.ai.GoalSelector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class WalkAroundGoal extends GoalSelector {

    private static final long DELAY = TimeUnit.MILLISECONDS.toNanos(2500);

    private final Random random = new Random();

    private long lastStroll;

    public WalkAroundGoal(@NotNull EntityCreature entityCreature) {
        super(entityCreature);
    }

    @Override
    public boolean shouldStart() {
        return System.nanoTime() - lastStroll >= DELAY;
    }

    @Override
    public void start() {
        List<Vec> closePositions = getNearbyBlocks(5 + random.nextInt(15));
        int remainingAttempt = closePositions.size();
        while (remainingAttempt-- > 0) {
            final int index = random.nextInt(closePositions.size());
            final Vec position = closePositions.get(index);

            final var target = entityCreature.getPosition().add(position);
            final boolean result = entityCreature.getNavigator().setPathTo(target);
            if (result) {
                break;
            }
        }
    }

    @Override
    public void tick(long time) {
    }

    @Override
    public boolean shouldEnd() {
        return true;
    }

    @Override
    public void end() {
        this.lastStroll = System.nanoTime();
    }

    private static @NotNull List<Vec> getNearbyBlocks(int radius) {
        List<Vec> blocks = new ArrayList<>();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    blocks.add(new Vec(x, y, z));
                }
            }
        }
        return blocks;
    }
}
