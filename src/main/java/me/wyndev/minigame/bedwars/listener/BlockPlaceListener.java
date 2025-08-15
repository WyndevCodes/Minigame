package me.wyndev.minigame.bedwars.listener;

import me.wyndev.minigame.Main;
import me.wyndev.minigame.bedwars.util.Msg;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.component.DataComponents;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@SuppressWarnings("unused")
public class BlockPlaceListener extends BedwarsEvent<PlayerBlockPlaceEvent> {

    public BlockPlaceListener(Instance gameWorld) {
        super(gameWorld);
    }

    public void onBlockPlace(PlayerBlockPlaceEvent e) {
        Player player = e.getPlayer();
        if (e.getPlayer().getItemInHand(e.getHand()).has(DataComponents.CUSTOM_DATA)) {
            String id = Objects.requireNonNull(player.getItemInHand(e.getHand()).get(DataComponents.CUSTOM_DATA)).nbt().getString("bwID");
            if (id.equals("TNT")) {
                ItemStack item = player.getItemInHand(e.getHand());
                player.setItemInHand(e.getHand(), item.withAmount(item.amount() - 1));
                Entity entity = new Entity(EntityType.TNT);
                entity.setInstance(Main.getGameManager().getGameWorld(), e.getBlockPosition());
                e.setBlock(Block.AIR);
            }
            Block block;
            if (e.getBlock().hasNbt()) {
                block = e.getBlock().withNbt(Objects.requireNonNull(e.getBlock().nbt()).putBoolean("placedByPlayer", true).putString("bwID", id));
            } else {
                block = e.getBlock().withNbt(CompoundBinaryTag.builder().putBoolean("placedByPlayer", true).putString("bwID", id).build());
            }
            e.setBlock(block);
        }
        if (player.getGameMode() == GameMode.CREATIVE) return;
        Pos spawn = Main.getGameManager().getWorldConfig().spawnPlatformCenter;
        if (distanceSquared(e.getBlockPosition().x(), spawn.x(), e.getBlockPosition().z(), spawn.z()) > 105 * 105 || e.getBlockPosition().y() >= 50) {
            e.setCancelled(true);
            player.sendMessage(Msg.whoops("You cannot place blocks that far from the map!"));
        }
    }

    private static double distanceSquared(double x1, double x2, double z1, double z2) {
        return Math.pow(x1 - x2, 2) + Math.pow(z1 - z2, 2);
    }

    @Override
    public void onEvent(PlayerBlockPlaceEvent event) {
        onBlockPlace(event);
    }

    @Override
    public @Nullable Instance instanceFromEvent(PlayerBlockPlaceEvent event) {
        return event.getInstance();
    }

    @Override
    public @NotNull Class<PlayerBlockPlaceEvent> eventType() {
        return PlayerBlockPlaceEvent.class;
    }
}
