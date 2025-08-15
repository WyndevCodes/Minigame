package me.wyndev.minigame.bot.pathfinding.goal.sub;

import lombok.Getter;
import lombok.Setter;
import me.wyndev.minigame.bot.PlayerBot;
import net.minestom.server.coordinate.Pos;

import java.util.Random;

public abstract class BedwarsBotGoal {

    protected final Random random;

    protected final PlayerBot bot;
    @Getter
    protected final int priority;
    @Getter
    protected boolean isExecuting;

    @Setter
    protected long time;

    protected BedwarsBotGoal(PlayerBot bot, int priority) {
        this.bot = bot;
        this.priority = priority;
        this.random = new Random();
    }

    public void tryStart() {
        if (!isExecuting && shouldExecute()) {
            isExecuting = true;
            startExecution();
        }
    }

    public boolean tryStop() {
        if (isExecuting && shouldStop()) {
            isExecuting = false;
            stopExecution();
            return true;
        }
        return false;
    }

    public abstract boolean shouldExecute();
    public abstract boolean shouldStop();
    public abstract void startExecution();
    public abstract void stopExecution();
    public abstract Pos getTargetPos();
    public void tick() {}

}
