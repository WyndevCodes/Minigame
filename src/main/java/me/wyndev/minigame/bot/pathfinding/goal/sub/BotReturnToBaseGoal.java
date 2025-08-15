package me.wyndev.minigame.bot.pathfinding.goal.sub;

import me.wyndev.minigame.bot.PlayerBot;
import net.minestom.server.coordinate.Pos;

public class BotReturnToBaseGoal extends BedwarsBotGoal {

    private Pos basePos;

    public BotReturnToBaseGoal(PlayerBot bot, int priority) {
        super(bot, priority);
    }

    @Override
    public boolean shouldExecute() {
        return bot.shouldReturnToBase();
    }

    @Override
    public boolean shouldStop() {
        return basePos.distanceSquared(bot.getPosition()) < 5 * 5;
    }

    @Override
    public void startExecution() {
        basePos = bot.getBedwarsTeam().getSpawnLocation();
    }

    @Override
    public void stopExecution() {
        bot.setShouldReturnToBase(false);
    }

    @Override
    public Pos getTargetPos() {
        if (basePos == null) {
            basePos = bot.getBedwarsTeam().getSpawnLocation();
        }
        return basePos;
    }
}
