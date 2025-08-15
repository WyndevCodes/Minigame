package me.wyndev.minigame.bot.pathfinding.goal.sub;

import me.wyndev.minigame.bot.PlayerBot;
import net.minestom.server.coordinate.Pos;

public class BotGetWoolGoal extends BedwarsBotGoal {

    private final int ironThreshold;
    private final int woolBlockThreshold;

    public BotGetWoolGoal(PlayerBot bot, int priority, boolean isBotOffensive) {
        super(bot, priority);
        this.ironThreshold = (isBotOffensive ? 12 : 18) + random.nextInt(10);
        this.woolBlockThreshold = 48 + random.nextInt(isBotOffensive ? 17 : 33);
    }

    @Override
    public boolean shouldExecute() {
        return bot.getBedwarsData().getWoolBlocks() < woolBlockThreshold && bot.getPosition().distanceSquared(bot.getBedwarsTeam().getGeneratorLocation()) < 15 * 15;
    }

    @Override
    public boolean shouldStop() {
        return bot.getBedwarsData().getWoolBlocks() >= woolBlockThreshold;
    }

    @Override
    public void startExecution() {

    }

    @Override
    public void stopExecution() {

    }

    @Override
    public Pos getTargetPos() {
        //sit in generator
        Pos target;
        if (bot.getBedwarsData().getIron() < ironThreshold) {
            target = bot.getBedwarsTeam().getGeneratorLocation();
        } else {
            //otherwise buy wool
            target = bot.getBedwarsTeam().getItemShopLocation();
        }
        return target;
    }

    @Override
    public void tick() {
        if (shouldStop()) return;
        if (bot.getPosition().distanceSquared(bot.getBedwarsTeam().getItemShopLocation()) < 4 * 4) {
            int ironPrice = 4; //todo fetch sell price
            int ironSetsSpent = bot.getBedwarsData().getIron() / ironPrice; //todo fetch sell price
            bot.getBedwarsData().setIron(bot.getBedwarsData().getIron() - (ironSetsSpent * ironPrice));
            int woolAmount = 16; //todo fetch amount
            bot.getBedwarsData().setWoolBlocks(bot.getBedwarsData().getWoolBlocks() + (woolAmount * ironSetsSpent));
            bot.delayMovement(200L * ironSetsSpent);
        }
    }
}
