package me.wyndev.minigame.bot.pathfinding.goal;

import me.wyndev.minigame.bot.PlayerBot;
import net.kyori.adventure.text.Component;
import net.minestom.server.adventure.audience.Audiences;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.entity.ai.GoalSelector;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Random;

public class BedwarsMovementGoal extends GoalSelector {

    private final Random random = new Random();
    private final int ironThreshold;
    private final int woolBlockThreshold;

    private final PlayerBot bot;

    private long delayMillis = 0;
    private long lastBridging = 0;

    public BedwarsMovementGoal(@NotNull PlayerBot bot) {
        super(bot);
        this.bot = bot;

        this.ironThreshold = 18 + random.nextInt(10);
        this.woolBlockThreshold = 48 + random.nextInt(33);
    }

    @Override
    public boolean shouldStart() {
        return true;
    }

    @Override
    public void start() {

    }

    @Override
    public void tick(long time) {
        if (System.currentTimeMillis() - delayMillis < 0) return;
        long bridgeBuffer = 1000L;

        //revert bot to standing state if not bridging
        if (System.currentTimeMillis() - lastBridging > bridgeBuffer) {
            bot.setSneaking(false);
            bot.setItemInHand(PlayerHand.MAIN, ItemStack.AIR);
        }

        //before doing any pathfinding, attempt to bridge over the void if necessary
        if (bot.getBedwarsData().getWoolBlocks() > 0) {
            Pos blockPos = bot.getPosition().sub(bot.getPosition().facing().vec().withY(0).mul(0.1, 0, 0.1)).sub(0, 1, 0).asPos();
            Block block = bot.getInstance().getBlock(blockPos, Block.Getter.Condition.TYPE);
            if (block.isAir()) {
                //try place block (only bridge when the block below the target block is not the desired wool color)
                int air = 0;
                for (BlockFace blockFace : BlockFace.values()) {
                    Block other = bot.getInstance().getBlock(blockPos.relative(blockFace), Block.Getter.Condition.TYPE);
                    if (other.isAir()) {
                        air++;
                        continue;
                    }
                    if (blockFace == BlockFace.BOTTOM && other.id() != Block.WHITE_WOOL.id()) {
                        air = BlockFace.values().length;
                        break;
                    }
                }
                //place block
                if (air < BlockFace.values().length) {
                    bot.getInstance().setBlock(blockPos, Block.WHITE_WOOL); //todo fetch team wool color
                    bot.setItemInHand(PlayerHand.MAIN, ItemStack.of(Material.WHITE_WOOL).builder().build());
                    bot.setSprinting(false);
                    bot.setSneaking(true);
                    //update bedwars data
                    bot.getBedwarsData().setWoolBlocks(bot.getBedwarsData().getWoolBlocks() - 1);
                    lastBridging = System.currentTimeMillis();
                }
            }
        }

        Pos target;

        if (bot.getBedwarsData().getWoolBlocks() < woolBlockThreshold) {
            //sit in generator
            if (bot.getBedwarsData().getIron() < ironThreshold) {
                target = new Pos(-5, 65, 0); //todo fetch point from team
                if (target.distanceSquared(entityCreature.getPosition()) > 1 && entityCreature.getNavigator().setPathTo(target)) return;
                //temp iron collection
                if (bot.getAliveTicks() % 20 != 0) return;
                bot.getBedwarsData().setIron(bot.getBedwarsData().getIron() + 4);
                //visual
                bot.setSprinting(false);
            } else {
                //otherwise buy wool
                target = new Pos(0, 65, 2); //todo fetch shopkeeper point from team
                if (target.distanceSquared(entityCreature.getPosition()) > 1 && entityCreature.getNavigator().setPathTo(target))
                    return;
                int ironPrice = 4; //todo fetch sell price
                int ironSetsSpent = bot.getBedwarsData().getIron() / ironPrice; //todo fetch sell price
                bot.getBedwarsData().setIron(bot.getBedwarsData().getIron() - (ironSetsSpent * ironPrice));
                int woolAmount = 16; //todo fetch amount
                bot.getBedwarsData().setWoolBlocks(bot.getBedwarsData().getWoolBlocks() + (woolAmount * ironSetsSpent));
                delay(200L * ironSetsSpent);
                //visual
                bot.setSprinting(false);
            }
        } else {
            //middle
            target = new Pos(20, 68, 2);
        }
        if (target.distanceSquared(entityCreature.getPosition()) > 1 && entityCreature.getNavigator().setPathTo(target)) {
            if (System.currentTimeMillis() - lastBridging < bridgeBuffer) return;
            bot.setSprinting(true);
        }
    }

    @Override
    public boolean shouldEnd() {
        return false;
    }

    @Override
    public void end() {

    }

    private void delay(long duration) {
        delayMillis = System.currentTimeMillis() + duration;
    }

    private void delay(Duration duration) {
       delay(duration.toMillis());
    }
}
