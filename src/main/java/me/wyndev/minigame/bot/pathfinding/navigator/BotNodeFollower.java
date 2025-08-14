package me.wyndev.minigame.bot.pathfinding.navigator;

import me.wyndev.minigame.bot.PlayerBot;
import net.minestom.server.collision.CollisionUtils;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.entity.pathfinding.followers.NodeFollower;
import net.minestom.server.utils.position.PositionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BotNodeFollower implements NodeFollower {

    private final PlayerBot bot;

    public BotNodeFollower(PlayerBot bot) {
        this.bot = bot;
    }

    public void moveTowards(@NotNull Point direction, double speed, @NotNull Point lookAt) {
        final Pos position = bot.getPosition();
        final double dx = direction.x() - position.x();
        final double dy = direction.y() - position.y();
        final double dz = direction.z() - position.z();

        double dxLook = lookAt.x() - position.x();
        double dyLook = lookAt.y() - position.y();
        double dzLook = lookAt.z() - position.z();

        if (bot.isSneaking()) {
            dxLook = -dxLook;
            dyLook = -dyLook;
            dzLook = -dzLook;
        } else {
            dyLook = dyLook * 0.1;
            if (bot.isSprinting() && bot.isOnGround()) {
                jump(4f);
            }
        }

        // the purpose of these few lines is to slow down entities when they reach their destination
        final double distSquared = dx * dx + dy * dy + dz * dz;
        if (speed > distSquared) {
            speed = distSquared;
        }

        final double radians = Math.atan2(dz, dx);
        final double speedX = Math.cos(radians) * speed;
        final double speedZ = Math.sin(radians) * speed;
        final float yaw = PositionUtils.getLookYaw(dxLook, dzLook);

        float pitch = PositionUtils.getLookPitch(dxLook, dyLook, dzLook);
        if (bot.isSneaking()) pitch = Math.clamp(pitch + 45, -90f, 90f);

        final var physicsResult = CollisionUtils.handlePhysics(bot, new Vec(speedX, 0, speedZ));
        this.bot.refreshPosition(physicsResult.newPosition().asPos().withView(yaw, pitch));
    }

    @Override
    public void jump(@Nullable Point point, @Nullable Point target) {
        if (bot.isOnGround()) {
            jump(4f);
        }
    }

    @Override
    public boolean isAtPoint(@NotNull Point point) {
        return bot.getPosition().sameBlock(point);
    }

    public void jump(float height) {
        this.bot.setVelocity(new Vec(0, height * 2.5f, 0));
    }

    @Override
    public double movementSpeed() {
        return bot.getAttribute(Attribute.MOVEMENT_SPEED).getValue() * (bot.isSneaking() ? 0.3 : (bot.isSprinting() ? 1.3 : 1));
    }
}
