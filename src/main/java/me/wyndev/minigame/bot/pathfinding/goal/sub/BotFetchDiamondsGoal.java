package me.wyndev.minigame.bot.pathfinding.goal.sub;

import me.wyndev.minigame.Main;
import me.wyndev.minigame.bedwars.data.state.GeneratorType;
import me.wyndev.minigame.bedwars.util.Items;
import me.wyndev.minigame.bot.PlayerBot;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.ItemEntity;

import java.util.List;

public class BotFetchDiamondsGoal extends BedwarsBotGoal {

    private final int dangerousDiamondLevel = 8;

    public BotFetchDiamondsGoal(PlayerBot bot, int priority) {
        super(bot, priority);
    }

    @Override
    public boolean shouldExecute() {
        return bot.getBedwarsData().getEmeralds() == 0 && sumDiamondsNearGenerators() > 0;
    }

    @Override
    public boolean shouldStop() {
        return bot.getBedwarsData().getEmeralds() >= dangerousDiamondLevel || sumDiamondsNearGenerators() == 0;
    }

    @Override
    public void startExecution() {

    }

    @Override
    public void stopExecution() {
        bot.setShouldReturnToBase(true);
    }

    @Override
    public Pos getTargetPos() {
        //find close diamond generators with items
        List<Pos> generatorPositions = Main.getGameManager().getWorldConfig().generators.get(GeneratorType.DIAMOND);

        //find closest of the nearby generators, go there
        Pos target = generatorPositions.getLast();
        for (Pos pos : generatorPositions) {
            if (bot.getBedwarsTeam() != null && pos.distanceSquared(bot.getBedwarsTeam().getSpawnLocation()) > 90 * 90) continue;
            if (countDiamondItemsNear(pos) > 0) {
                if (countDiamondItemsNear(target) == 0) {
                    target = pos;
                } else {
                    if (bot.getPosition().distanceSquared(pos) < bot.getPosition().distanceSquared(target)) return pos;
                }
            }
        }

        return target;
    }

    private long countDiamondItemsNear(Pos genPos) {
        return bot.getInstance().getNearbyEntities(genPos, 3).stream().filter(e -> e instanceof ItemEntity itemEntity
                && itemEntity.getItemStack().hasTag(Items.NAMESPACE)
                && itemEntity.getItemStack().getTag(Items.NAMESPACE).equalsIgnoreCase("DIAMOND")).count();
    }

    private long sumDiamondsNearGenerators() {
        long emeralds = 0;
        for (Pos pos : Main.getGameManager().getWorldConfig().generators.get(GeneratorType.EMERALD)) {
            emeralds += countDiamondItemsNear(pos);
        }
        return emeralds;
    }
}
