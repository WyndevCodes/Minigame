package me.wyndev.minigame.bot.pathfinding.goal.sub;

import me.wyndev.minigame.bot.PlayerBot;
import net.minestom.server.entity.Entity;

public abstract class BedwarsBotTargetingGoal extends BedwarsBotGoal {

    private boolean stop;
    protected Entity cachedTarget;

    protected BedwarsBotTargetingGoal(PlayerBot bot, int priority) {
        super(bot, priority);
    }

    public abstract Entity findTarget();
    public abstract boolean canStartTargeting();

    public boolean shouldStopIf(boolean condition) {
        stop = condition;
        return shouldStop();
    }

    @Override
    public boolean shouldExecute() {
        this.cachedTarget = findTarget();
        return this.cachedTarget != null && canStartTargeting();
    }

    @Override
    public boolean shouldStop() {
        return stop;
    }

    @Override
    public void startExecution() {
        stop = false;
    }

    @Override
    public void stopExecution() {}
}
