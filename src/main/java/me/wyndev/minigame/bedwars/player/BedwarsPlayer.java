package me.wyndev.minigame.bedwars.player;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.wyndev.minigame.bedwars.data.state.ArmorLevel;
import me.wyndev.minigame.bedwars.data.state.AxeLevel;
import me.wyndev.minigame.bedwars.data.state.PickaxeLevel;
import me.wyndev.minigame.bot.PlayerBot;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.scoreboard.Team;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

@Getter
@Setter
public class BedwarsPlayer {
    private Entity parentEntity;
    private String username;
    private PlayerSkin skin;
    private ArmorLevel armorLevel = ArmorLevel.NONE;
    private AxeLevel axeLevel = AxeLevel.NONE;
    private PickaxeLevel pickaxeLevel = PickaxeLevel.NONE;
    @Getter(AccessLevel.NONE)
    private boolean shears = false;
    private boolean alive = true;
    private boolean respawning = false;
    private Inventory enderChest = new Inventory(InventoryType.CHEST_3_ROW, "Ender Chest");

    public BedwarsPlayer(Entity parentEntity, BedwarsPlayer existing) {
        this.parentEntity = parentEntity;
        this.username = (parentEntity instanceof Player player ? player.getUsername() : (parentEntity instanceof PlayerBot bot ? bot.getUsername() : "Unnamed"));
        this.skin = (parentEntity instanceof Player player ? player.getSkin() : (parentEntity instanceof PlayerBot bot ? bot.getPlayerSkin() : null));
        load(existing);
    }

    public BedwarsPlayer(Entity parentEntity) {
        this(parentEntity, null);
    }

    public void load(@Nullable BedwarsPlayer existing) {
        if (existing == null) return;
        this.armorLevel = existing.getArmorLevel();
        this.axeLevel = existing.getAxeLevel();
        this.pickaxeLevel = existing.getPickaxeLevel();
        this.shears = existing.hasShears();
        this.alive = existing.isAlive();
        this.respawning = existing.isRespawning();
        this.enderChest = existing.getEnderChest();
    }

    public UUID getUuid() {
        return parentEntity.getUuid();
    }

    public boolean hasShears() {
        return shears;
    }

    public void openEnderChest() {
        if (!(this.parentEntity instanceof Player player)) throw new IllegalStateException("Parent entity of bedwars player is not a player! Cannot open ender chest!");
        player.openInventory(enderChest);
    }

    public void addToTeam(Team team) {
        team.addMember(this.parentEntity instanceof Player player ? player.getUsername() : getUuid().toString());
    }
}
