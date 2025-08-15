package me.wyndev.minigame.bedwars.blockhandler;

import me.wyndev.minigame.Main;
import me.wyndev.minigame.bedwars.data.gamecomponent.Team;
import me.wyndev.minigame.bedwars.player.BedwarsPlayer;
import me.wyndev.minigame.bedwars.util.Items;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.server.play.BlockActionPacket;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * Chest block handler for bedwars.
 * @author webhead1104/Cytonic
 */
public class ChestBlockHandler implements BlockHandler {
    @Override
    public boolean onInteract(@NotNull Interaction interaction) {
        if (!Main.getGameManager().isSTARTED()) return false;
        if (Main.getGameManager().getSpectators().contains(interaction.getPlayer().getUuid())) return false;
        BedwarsPlayer player = Main.getGameManager().getBedwarsPlayerFor(interaction.getPlayer());
        if (player == null) return false;
        Team team = Main.getGameManager().getPlayerTeam(player).orElseThrow();
        if (!team.getChestLocation().sameBlock(interaction.getBlockPosition())) return false;
        EventListener<InventoryPreClickEvent> listener = EventListener.of(InventoryPreClickEvent.class, event -> {
            ItemStack item = event.getClickedItem();
            if (item.hasTag(Items.NAMESPACE) && item.getTag(Items.NAMESPACE).equals("DEFAULT_SWORD")) {
                event.setCancelled(true);
            }
        });
        team.getTeamChest().eventNode().addListener(EventListener.of(InventoryCloseEvent.class, event -> {
            Main.getGameManager().getGameWorld().sendGroupedPacket(new BlockActionPacket(interaction.getBlockPosition(), (byte) 1, (byte) 0, interaction.getBlock()));
            Main.getGameManager().getGameWorld().playSound(Sound.sound(SoundEvent.BLOCK_CHEST_CLOSE, Sound.Source.MASTER, 0.5f, new Random().nextFloat() * 0.1F + 0.9F), interaction.getBlockPosition());
        }));
        interaction.getPlayer().eventNode().addListener(listener);
        interaction.getPlayer().openInventory(team.getTeamChest());
        team.getTeamChest().addViewer(interaction.getPlayer());
        Main.getGameManager().getGameWorld().playSound(Sound.sound(SoundEvent.BLOCK_CHEST_OPEN, Sound.Source.MASTER, 0.5f, new Random().nextFloat() * 0.1F + 0.9F), interaction.getBlockPosition());
        Main.getGameManager().getGameWorld().sendGroupedPacket(new BlockActionPacket(interaction.getBlockPosition(), (byte) 1, (byte) 1, interaction.getBlock()));
        return true;
    }

    @Override
    public @NotNull Key getKey() {
        return Key.key("minecraft:chest");
    }
}
