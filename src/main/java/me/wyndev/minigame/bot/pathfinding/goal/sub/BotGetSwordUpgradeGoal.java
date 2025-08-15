package me.wyndev.minigame.bot.pathfinding.goal.sub;

import me.wyndev.minigame.Main;
import me.wyndev.minigame.bedwars.data.state.GameState;
import me.wyndev.minigame.bedwars.util.Items;
import me.wyndev.minigame.bot.PlayerBot;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.item.ItemStack;

public class BotGetSwordUpgradeGoal extends BedwarsBotGoal {

    private final boolean isOffensive;

    public BotGetSwordUpgradeGoal(PlayerBot bot, int priority, boolean isOffensive) {
        super(bot, priority);
        this.isOffensive = isOffensive;
    }

    @Override
    public boolean shouldExecute() {
        return shouldUpgradeFrom(bot.getBedwarsData().getSword()) && bot.getPosition().distanceSquared(bot.getBedwarsTeam().getGeneratorLocation()) < 15 * 15;
    }

    @Override
    public boolean shouldStop() {
        return !shouldUpgradeFrom(bot.getBedwarsData().getSword());
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
        if (canBuyNextSword(bot.getBedwarsData().getSword())) {
            target = bot.getBedwarsTeam().getGeneratorLocation();
        } else {
            //otherwise buy sword
            target = bot.getBedwarsTeam().getItemShopLocation();
        }
        return target;
    }

    @Override
    public void tick() {
        if (shouldStop()) return;
        if (bot.getPosition().distanceSquared(bot.getBedwarsTeam().getItemShopLocation()) < 4 * 4 && canBuyNextSword(bot.getBedwarsData().getSword())) {
            buyNextSword(bot.getBedwarsData().getSword());
            bot.delayMovement(800);
        }
    }

    public boolean shouldUpgradeFrom(ItemStack swordStack) {
        if (!swordStack.hasTag(Items.NAMESPACE)) return false;
        GameState gameState = Main.getGameManager().getGameState();
        switch (swordStack.getTag(Items.NAMESPACE).toUpperCase()) {
            case "DEFAULT_SWORD" -> {
                return gameState.ordinal() >= GameState.PLAY.ordinal();
            }
            case "STONE_SWORD" -> {
                return gameState.ordinal() >= (isOffensive ? GameState.PLAY.ordinal() : GameState.DIAMOND_2.ordinal());
            }
        }
        return false;
    }

    public boolean canBuyNextSword(ItemStack swordStack) {
        if (!swordStack.hasTag(Items.NAMESPACE)) return false;
        switch (swordStack.getTag(Items.NAMESPACE).toUpperCase()) {
            case "DEFAULT_SWORD" -> {
                return bot.getBedwarsData().getIron() >= 10;
            }
            case "STONE_SWORD" -> {
                return bot.getBedwarsData().getGold() >= 7;
            }
        }
        return false;
    }

    public void buyNextSword(ItemStack swordStack) {
        if (!swordStack.hasTag(Items.NAMESPACE)) return;
        switch (swordStack.getTag(Items.NAMESPACE).toUpperCase()) {
            case "DEFAULT_SWORD" -> {
                bot.getBedwarsData().setIron(bot.getBedwarsData().getIron() - 10);
                bot.getBedwarsData().setSword(Items.STONE_SWORD);
            }
            case "STONE_SWORD" -> {
                bot.getBedwarsData().setGold(bot.getBedwarsData().getGold() - 7);
                bot.getBedwarsData().setSword(Items.IRON_SWORD);
            }
        }
    }
}
