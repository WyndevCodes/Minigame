package me.wyndev.minigame.bot.pathfinding.goal.sub;

import me.wyndev.minigame.Main;
import me.wyndev.minigame.bedwars.data.gamecomponent.Team;
import me.wyndev.minigame.bot.PlayerBot;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EquipmentSlot;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.entity.damage.EntityDamage;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.utils.time.Cooldown;
import net.minestom.server.utils.time.TimeUnit;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.temporal.TemporalUnit;
import java.util.Comparator;
import java.util.Optional;

public class BotCombatGoal extends BedwarsBotTargetingGoal {

    private final Cooldown cooldown = new Cooldown(Duration.of(5, TimeUnit.SERVER_TICK));

    private long lastHit;
    private final double range;
    private final Duration delay;
    private final float followRange;
    private final float searchRange;

    public BotCombatGoal(PlayerBot bot, int priority, double hitRange, float followRange, float searchRange, Duration hitDelay) {
        super(bot, priority);
        this.range = hitRange;
        this.delay = hitDelay;
        this.followRange = followRange;
        this.searchRange = searchRange;
    }

    public BotCombatGoal(PlayerBot bot, int priority, double hitRange, float followRange, float searchRange,
                         int hitDelay, @NotNull TemporalUnit timeUnit) {
        this(bot, priority, hitRange, followRange, searchRange, Duration.of(hitDelay, timeUnit));
    }

    @Override
    public boolean canStartTargeting() {
        return bot.getHealth() > 5;
    }

    @Override
    public Pos getTargetPos() {
        Entity target;
        if (this.cachedTarget != null) {
            target = this.cachedTarget;
            this.cachedTarget = null;
        } else {
            target = findTarget();
        }

        if (!shouldStopIf(target == null || bot.getHealth() <= 5)) {
            if (target == null) return null;

            //hold the correct item
            bot.setEquipment(EquipmentSlot.MAIN_HAND, bot.getBedwarsData().getSword());

            // Attack the target entity
            if (bot.getDistanceSquared(target) <= range * range) {
                bot.lookAt(target);
                if (!Cooldown.hasCooldown(time, lastHit, delay)) {
                    bot.attack(target, true);
                    this.lastHit = time;
                }

            }
            return target.getPosition();
        }
        return null;
    }

    @Override
    public Entity findTarget() {
        if (bot.getBedwarsTeam() == null) return null;

        //first, check for last damager
        final Damage damage = bot.getLastDamageSource();
        if (damage instanceof EntityDamage entityDamage) {
            final Entity entity = entityDamage.getSource();
            if (!entity.isRemoved() && bot.getDistanceSquared(entity) < followRange * followRange) return entity;
        }

        //next, find a nearby target on a different team
        Instance instance = bot.getInstance();

        if (instance == null) return null;

        return instance.getNearbyEntities(bot.getPosition(), searchRange).stream()
                // Don't target our self and make sure entity is valid
                .filter(ent -> !bot.equals(ent) && !ent.isRemoved())
                .filter(e -> {
                    Optional<Team> team = Main.getGameManager().getPlayerTeam(e.getUuid());
                    if (team.isEmpty() || team.get().equals(bot.getBedwarsTeam())) return false;
                    return !isPosOverVoid(e.getPosition()); //don't target entities who are falling into the void/flying over the void
                })
                .min(Comparator.comparingDouble(e -> e.getDistanceSquared(bot)))
                .orElse(null);
    }

    private boolean isPosOverVoid(Pos pos) {
        for (int i = pos.blockY(); i > -64; i--) {
            if (!bot.getInstance().getBlock(pos.withY(i), Block.Getter.Condition.TYPE).isAir()) {
                return false;
            }
        }
        return true;
    }
}
