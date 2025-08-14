package me.wyndev.minigame.bot;

import me.wyndev.minigame.Main;
import me.wyndev.minigame.bot.customization.NametagGenerator;
import me.wyndev.minigame.bot.pathfinding.goal.BedwarsMovementGoal;
import me.wyndev.minigame.bot.pathfinding.navigator.BotNodeFollower;
import me.wyndev.minigame.bot.pathfinding.navigator.BotNodeGenerator;
import me.wyndev.minigame.player.Rank;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.*;
import net.minestom.server.entity.attribute.Attribute;
import net.minestom.server.network.packet.server.play.EntityMetaDataPacket;
import net.minestom.server.network.packet.server.play.PlayerInfoRemovePacket;
import net.minestom.server.network.packet.server.play.PlayerInfoUpdatePacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("UnstableApiUsage")
public class PlayerBot extends EntityCreature {
    private static int botCount = 0;

    private final String username;
    private final Component displayName;
    private final String skinTexture;
    private final String skinSignature;

    private final Rank rank;
    private BedwarsData bedwarsData;

    public PlayerBot() {
        this(null, null);
        this.bedwarsData = new BedwarsData(0, 0, 0, 0, 0);
    }

    public PlayerBot(@Nullable String skinTexture, @Nullable String skinSignature) {
        super(EntityType.PLAYER);
        this.username = NametagGenerator.generateName();
        botCount++;

        this.skinTexture = skinTexture;
        this.skinSignature = skinSignature;

        this.rank = Rank.DEFAULT;
        displayName = Main.MINI_MESSAGE.deserialize(rank.prefix() + username);

        getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.2);
        getAttribute(Attribute.STEP_HEIGHT).setBaseValue(0.6);

        addAIGroup(
                List.of(
                        new BedwarsMovementGoal(this)
                        //new WalkAroundGoal(this) // Walk around
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
        var entry = new PlayerInfoUpdatePacket.Entry(getUuid(), username, properties, true,
                0, GameMode.SURVIVAL, displayName, null, rank.sortOrder());
        for (PlayerInfoUpdatePacket.Action action : PlayerInfoUpdatePacket.Action.values()) {
            if (action == PlayerInfoUpdatePacket.Action.INITIALIZE_CHAT) continue;
            player.sendPacket(new PlayerInfoUpdatePacket(action, entry));
        }

        // Spawn the player entity
        super.updateNewViewer(player);

        //add to ranked team
        setTeam(rank.getTeam(MinecraftServer.getTeamManager()));

        // Enable skin layers
        player.sendPacket(new EntityMetaDataPacket(getEntityId(), Map.of(17, Metadata.Byte((byte) 127))));
    }

    @Override
    public void updateOldViewer(@NotNull Player player) {
        super.updateOldViewer(player);

        player.sendPacket(new PlayerInfoRemovePacket(getUuid()));
    }

    public BedwarsData getBedwarsData() {
        return bedwarsData;
    }

    public static class BedwarsData {

        private int iron;
        private int gold;
        private int diamonds;
        private int emeralds;
        private int woolBlocks;

        public BedwarsData(int iron, int gold, int diamonds, int emeralds, int woolBlocks) {
            this.iron = iron;
            this.gold = gold;
            this.diamonds = diamonds;
            this.emeralds = emeralds;
            this.woolBlocks = woolBlocks;
        }

        public int getIron() {
            return iron;
        }

        public void setIron(int iron) {
            this.iron = iron;
        }

        public int getGold() {
            return gold;
        }

        public void setGold(int gold) {
            this.gold = gold;
        }

        public int getDiamonds() {
            return diamonds;
        }

        public void setDiamonds(int diamonds) {
            this.diamonds = diamonds;
        }

        public int getEmeralds() {
            return emeralds;
        }

        public void setEmeralds(int emeralds) {
            this.emeralds = emeralds;
        }

        public int getWoolBlocks() {
            return woolBlocks;
        }

        public void setWoolBlocks(int woolBlocks) {
            this.woolBlocks = woolBlocks;
        }
    }
}
