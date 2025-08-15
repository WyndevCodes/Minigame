package me.wyndev.minigame.bot.pathfinding.goal;

import lombok.Getter;
import me.wyndev.minigame.bedwars.data.state.MappableItem;
import me.wyndev.minigame.bedwars.util.Items;
import me.wyndev.minigame.bot.PlayerBot;
import me.wyndev.minigame.bot.pathfinding.goal.sub.*;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.entity.ai.GoalSelector;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.item.ItemStack;
import net.minestom.server.utils.time.TimeUnit;
import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.TreeMap;
import java.util.function.Consumer;

public class BedwarsMovementGoal extends GoalSelector {

    private final TreeMap<Integer, BedwarsBotGoal> goals = new TreeMap<>();

    private final PlayerBot bot;

    private long delayMillis = 0;
    private long lastBridging = 0;
    @Getter
    private final boolean isOffensive;

    public BedwarsMovementGoal(@NotNull PlayerBot bot) {
        super(bot);
        this.bot = bot;

        Random random = new Random();
        isOffensive = random.nextBoolean();

        //lower priority means execution happens first
        BedwarsBotGoal[] goals = new BedwarsBotGoal[]{
                new BotCombatGoal(bot, 1, 1.6, 32, 16, 5, TimeUnit.SERVER_TICK),
                new BotGetWoolGoal(bot, 2, isOffensive),
                new BotGetSwordUpgradeGoal(bot, isOffensive ? 3 : 5, isOffensive),
                new BotFetchEmeraldsGoal(bot, 4),
                new BotFetchDiamondsGoal(bot, isOffensive ? 5 : 3),
                new BotReturnToBaseGoal(bot, 99)
        };

        for (BedwarsBotGoal botGoal : goals) {
            this.goals.put(botGoal.getPriority(), botGoal);
        }
    }

    @Override
    public boolean shouldStart() {
        return bot.getBedwarsTeam() != null;
    }

    @Override
    public void start() {
        Pos target = null;
        for (BedwarsBotGoal goal : goals.values()) {
            if (goal.tryStop()) continue;
            goal.tryStart();
            if (goal.isExecuting()) {
                target = goal.getTargetPos();
                if (target != null) break;
            }
        }

        //move to the target position, or reset the navigator if the position is null
        bot.getNavigator().setPathTo(target);
    }

    @Override
    public void tick(long time) {
        for (BedwarsBotGoal goal : goals.values()) {
            if (goal.isExecuting()) {
                goal.setTime(time);
                goal.tick();
            }
        }

        if (System.currentTimeMillis() - delayMillis < 0) return;
        long speedBridgeBuffer = 100L;

        //revert bot to standing state if not bridging
        if (isBridging()) {
            bot.setItemInHand(PlayerHand.MAIN, ItemStack.AIR);
            if (!isOffensive) bot.setSneaking(false);
        }
        if (System.currentTimeMillis() - lastBridging > speedBridgeBuffer && isOffensive) {
            bot.setSneaking(false);
        }

        //before doing any pathfinding, attempt to bridge over the void if necessary
        Pos blockPos = bot.getPosition()
                .sub(bot.getPosition().facing().vec().withY(0).mul(0.1, 0, 0.1))
                .sub(0, 1, 0)
                .asPos();
        tryPlaceBlock(blockPos, true, (pos) -> lastBridging = System.currentTimeMillis());

        //null check before using target pos in calculations
        Pos target = bot.getNavigator().getPathPosition().asPos();

        //more block placement logic, this time for moving upwards
        boolean isCloseNonY = Math.abs(target.x() - bot.getPosition().x()) < 1 && Math.abs(target.z() - bot.getPosition().z()) < 1;
        if (isCloseNonY && target.y() - bot.getPosition().y() > 1) {
            bot.setShouldJump(true);
            tryPlaceBlock(bot.getPosition().sub(0, 1, 0), false, pos -> {});
        }

        if (target.distanceSquared(entityCreature.getPosition()) > 1 && entityCreature.getNavigator().setPathTo(target)) {
            if (isBridging()) return;
            //check if bot will sprint into void
            Pos toCheck = bot.getPosition().add(bot.getPosition().facing().vec().withY(0).mul(4, 0, 4)).sub(0, 1, 0);
            if (isPosOverVoid(toCheck)) {
                if (bot.isSprinting()) bot.setSprinting(false);
            } else if (!bot.isSprinting() && target.distanceSquared(entityCreature.getPosition()) < 4 * 4) {
                bot.setSprinting(true);
            }
        }
    }

    public boolean isBridging() {
        long bridgeBuffer = 1000L;
        return System.currentTimeMillis() - lastBridging <= bridgeBuffer;
    }

    private void tryPlaceBlock(Pos blockPos, boolean onlyPlaceOverVoid, Consumer<Pos> onPlace) {
        if (bot.getBedwarsData().getWoolBlocks() <= 0) return;

        Block block = bot.getInstance().getBlock(blockPos, Block.Getter.Condition.TYPE);
        if (block.isAir()) {
            //try place block (only bridge when the block below the target block is not the desired wool color)
            int air = 0;
            boolean isNextToVoid = false;
            for (BlockFace blockFace : BlockFace.values()) {
                Pos otherPos = blockPos.relative(blockFace);
                Block other = bot.getInstance().getBlock(otherPos, Block.Getter.Condition.TYPE);
                if (other.isAir()) {
                    air++;
                }
                if (!isNextToVoid && isPosOverVoid(otherPos)) isNextToVoid = true;
            }
            //place block
            if (air < BlockFace.values().length) {

                //check if over void
                if (onlyPlaceOverVoid) {
                    if (!isNextToVoid || !isPosOverVoid(blockPos)) return;
                }

                placeBlock(blockPos);
                onPlace.accept(blockPos);
            }
        }
    }

    private boolean isPosOverVoid(Pos pos) {
        for (int i = pos.blockY(); i > -64; i--) {
            if (!bot.getInstance().getBlock(pos.withY(i), Block.Getter.Condition.TYPE).isAir()) {
                return false;
            }
        }
        return true;
    }

    private void placeBlock(Pos blockPos) {
        bot.getInstance().setBlock(blockPos, bot.getBedwarsTeam().getWoolType());
        bot.setItemInHand(PlayerHand.MAIN, Items.getTeamMapped(MappableItem.WOOL, bot.getBedwarsTeam()));
        bot.setSprinting(false);
        bot.setSneaking(true);
        bot.swingMainHand();
        //update bedwars data
        bot.getBedwarsData().setWoolBlocks(bot.getBedwarsData().getWoolBlocks() - 1);
    }

    @Override
    public boolean shouldEnd() {
        return true;
    }

    @Override
    public void end() {

    }

    public void delay(long duration) {
        delayMillis = System.currentTimeMillis() + duration;
    }
}
