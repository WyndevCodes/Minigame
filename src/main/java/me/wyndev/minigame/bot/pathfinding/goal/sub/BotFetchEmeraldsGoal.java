package me.wyndev.minigame.bot.pathfinding.goal.sub;

import me.wyndev.minigame.Main;
import me.wyndev.minigame.bedwars.data.state.GeneratorType;
import me.wyndev.minigame.bedwars.util.Items;
import me.wyndev.minigame.bot.PlayerBot;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.ItemEntity;

import java.util.List;

public class BotFetchEmeraldsGoal extends BedwarsBotGoal {

    private final int dangerousEmeraldLevel = 4;

    public BotFetchEmeraldsGoal(PlayerBot bot, int priority) {
        super(bot, priority);
    }

    @Override
    public boolean shouldExecute() {
        return bot.getBedwarsData().getEmeralds() == 0 && sumEmeraldsNearGenerators() > 0;
    }

    @Override
    public boolean shouldStop() {
        return bot.getBedwarsData().getEmeralds() >= dangerousEmeraldLevel || sumEmeraldsNearGenerators() == 0;
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
        //find emerald generators with items
        List<Pos> generatorPositions = Main.getGameManager().getWorldConfig().generators.get(GeneratorType.EMERALD);

        Pos target = generatorPositions.getLast();
        for (Pos pos : generatorPositions) {
            if (countEmeraldItemsNear(pos) > 0) {
                if (countEmeraldItemsNear(target) == 0) {
                    target = pos;
                } else {
                    if (bot.getPosition().distanceSquared(pos) < bot.getPosition().distanceSquared(target)) return pos;
                }
            }
        }

        return target;
    }

    private long countEmeraldItemsNear(Pos genPos) {
        return bot.getInstance().getNearbyEntities(genPos, 3).stream().filter(e -> e instanceof ItemEntity itemEntity
                && itemEntity.getItemStack().hasTag(Items.NAMESPACE)
                && itemEntity.getItemStack().getTag(Items.NAMESPACE).equalsIgnoreCase("EMERALD")).count();
    }

    private long sumEmeraldsNearGenerators() {
        long emeralds = 0;
        for (Pos pos : Main.getGameManager().getWorldConfig().generators.get(GeneratorType.EMERALD)) {
            emeralds += countEmeraldItemsNear(pos);
        }
        return emeralds;
    }
}
