package me.wyndev.minigame.bedwars.listener;

import me.wyndev.minigame.Main;
import me.wyndev.minigame.bedwars.player.BedwarsPlayer;
import me.wyndev.minigame.bedwars.util.Items;
import me.wyndev.minigame.bedwars.util.Msg;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BlockBreakListener extends BedwarsEvent<PlayerBlockBreakEvent> {

    public BlockBreakListener(Instance gameWorld) {
        super(gameWorld);
    }

    public void onBlockBreak(PlayerBlockBreakEvent e) {
        Player player = e.getPlayer();
        if (!Main.getGameManager().isPlayerInBedwars(player)) return;

        if (player.getGameMode() == GameMode.CREATIVE) return;
        if (Main.getGameManager().getSpectators().contains(player.getUuid())) {
            player.sendMessage(Msg.whoops("You cannot do this as a spectator!"));
            e.setCancelled(true);
            return;
        }
        if (e.getBlock().name().contains("bed")) {
            BedwarsPlayer bedwarsPlayer = Main.getGameManager().getBedwarsPlayerFor(player);
            if (bedwarsPlayer == null) return;

            if (Main.getGameManager().getPlayerTeam(player).isPresent()) {
                if (Main.getGameManager().getPlayerTeam(player).get().getBedType().key().equals(e.getBlock().key())) {
                    e.setCancelled(true);
                    player.sendMessage(Msg.whoops("You cannot break your own bed!"));
                    return;
                }
            }
            Main.getGameManager().getTeams().forEach(team -> {
                if (e.getBlock().name().equals(team.getBedType().name())) {
                    Main.getGameManager().breakBed(bedwarsPlayer, team);
                    Main.getGameManager().getWorldManager().breakBed(team);
                }
            });
            return;
        }

        if (e.getBlock().hasNbt()) {
            if (!Objects.requireNonNull(e.getBlock().nbt()).getBoolean("placedByPlayer")) {
                player.sendMessage(Msg.whoops("You can only break blocks placed by players!"));
                e.setCancelled(true);
                return;
            }
        } else {
            player.sendMessage(Msg.whoops("You can only break blocks placed by players!"));
            e.setCancelled(true);
            return;
        }
        ItemStack stack = Items.get(e.getBlock().nbt().getString("bwID"));
        ItemEntity item = new ItemEntity(stack);
        item.setInstance(Main.getGameManager().getGameWorld(), e.getBlockPosition());
        item.spawn();
    }

    @Override
    public void onEvent(PlayerBlockBreakEvent event) {
        onBlockBreak(event);
    }

    @Override
    public @Nullable Instance instanceFromEvent(PlayerBlockBreakEvent event) {
        return event.getInstance();
    }

    @Override
    public @NotNull Class<PlayerBlockBreakEvent> eventType() {
        return PlayerBlockBreakEvent.class;
    }
}
