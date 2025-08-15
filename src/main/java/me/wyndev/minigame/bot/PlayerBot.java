package me.wyndev.minigame.bot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.wyndev.minigame.Main;
import me.wyndev.minigame.bedwars.data.gamecomponent.Team;
import me.wyndev.minigame.bedwars.player.BedwarsPlayer;
import me.wyndev.minigame.bedwars.util.Items;
import me.wyndev.minigame.bedwars.util.Msg;
import me.wyndev.minigame.bot.customization.NametagGenerator;
import me.wyndev.minigame.bot.pathfinding.goal.BedwarsMovementGoal;
import me.wyndev.minigame.bot.pathfinding.goal.WalkAroundGoal;
import me.wyndev.minigame.bot.pathfinding.navigator.BotNodeFollower;
import me.wyndev.minigame.bot.pathfinding.navigator.BotNodeGenerator;
import me.wyndev.minigame.player.Rank;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.*;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;
import net.minestom.server.network.packet.server.play.PlayerInfoRemovePacket;
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class PlayerBot extends EntityCreature {
    private static int botCount = 0;

    @Getter
    private final String username;
    private final Component rankDisplayName;
    private final String skinTexture;
    private final String skinSignature;
    @Getter
    private final PlayerSkin playerSkin;
    private final BedwarsMovementGoal movementGoal;

    @Getter
    private final Rank rank;
    @Getter
    private BedwarsData bedwarsData;
    @Getter
    @Setter
    @Nullable
    private BedwarsPlayer bedwarsPlayer;
    @Getter
    @Setter
    @Nullable
    private Team bedwarsTeam;

    @Setter
    private boolean shouldJump = false;
    @Setter
    private boolean shouldReturnToBase = false;

    public PlayerBot() {
        this(null, null);
        this.bedwarsData = BedwarsData.getDefaultData();
        this.bedwarsPlayer = null;
        this.bedwarsTeam = null;
    }

    public PlayerBot(@Nullable String skinTexture, @Nullable String skinSignature) {
        super(EntityType.PLAYER);
        this.username = NametagGenerator.generateName();
        botCount++;

        this.skinTexture = skinTexture;
        this.skinSignature = skinSignature;
        this.playerSkin = new PlayerSkin(skinTexture, skinSignature);

        this.rank = Rank.DEFAULT;
        rankDisplayName = Main.MINI_MESSAGE.deserialize(rank.prefix() + username);

        this.canPickupItem = true;

        getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.2);
        getAttribute(Attribute.STEP_HEIGHT).setBaseValue(0.6);

        this.movementGoal = new BedwarsMovementGoal(this);

        addAIGroup(
                List.of(
                        movementGoal,
                        new WalkAroundGoal(this) // Walk around when not playing
                ),
                List.of()
        );
        getNavigator().setNodeGenerator(() -> new BotNodeGenerator(this));
        getNavigator().setNodeFollower(() -> new BotNodeFollower(this));
    }

    @Override
    public void updateNewViewer(@NotNull Player player) {
        var properties = new ArrayList<PlayerInfoUpdatePacket.Property>();
        if (skinTexture != null && skinSignature != null) {
            properties.add(new PlayerInfoUpdatePacket.Property("textures", skinTexture, skinSignature));
        }
        Component displayName = bedwarsTeam != null ? Msg.mm(bedwarsTeam.getPrefix() + username) : rankDisplayName;
        var entry = new PlayerInfoUpdatePacket.Entry(getUuid(), username, properties, true,
                0, GameMode.SURVIVAL, displayName, null, rank.sortOrder());
        for (PlayerInfoUpdatePacket.Action action : PlayerInfoUpdatePacket.Action.values()) {
            if (action == PlayerInfoUpdatePacket.Action.INITIALIZE_CHAT || action == PlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE) continue;
            player.sendPacket(new PlayerInfoUpdatePacket(action, entry));
        }

        // Spawn the player entity
        super.updateNewViewer(player);

        //add to ranked team
        if (bedwarsTeam != null) {
            addToTeam(bedwarsTeam.getMcTeam());
        } else {
            addToTeam(rank.getTeam(MinecraftServer.getTeamManager()));
        }

        // Enable skin layers
        player.sendPacket(new EntityMetaDataPacket(getEntityId(), Map.of(17, Metadata.Byte((byte) 127))));
    }

    @Override
    public void updateOldViewer(@NotNull Player player) {
        super.updateOldViewer(player);

        player.sendPacket(new PlayerInfoRemovePacket(getUuid()));
    }

    public void addToTeam(net.minestom.server.scoreboard.Team team) {
        team.addMember(username);
    }

    public boolean shouldJump() {
        return shouldJump && isOnGround();
    }

    public boolean shouldMoveBackwards() {
        return movementGoal.isBridging();
    }

    public boolean shouldReturnToBase() {
        return shouldReturnToBase;
    }

    public void delayMovement(long duration) {
        movementGoal.delay(duration);
    }

    public void delayMovement(Duration duration) {
        delayMovement(duration.toMillis());
    }

    public void resetBedwarsData() {
        this.bedwarsData = BedwarsData.getDefaultData();
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class BedwarsData {

        public static BedwarsData getDefaultData() {
            return new BedwarsData(0, 0, 0, 0, 0, Items.DEFAULT_SWORD);
        }

        private int iron;
        private int gold;
        private int diamonds;
        private int emeralds;
        private int woolBlocks;
        private ItemStack sword;

        public void addIron(int iron) {
            this.iron += iron;
        }

        public void addGold(int gold) {
            this.gold += gold;
        }

        public void addDiamonds(int diamonds) {
            this.diamonds += diamonds;
        }

        public void addEmeralds(int emeralds) {
            this.emeralds += emeralds;
        }
    }
}
