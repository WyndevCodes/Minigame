package me.wyndev.minigame.bedwars.menu.spectator;

import eu.koboo.minestom.stomui.api.PlayerView;
import eu.koboo.minestom.stomui.api.ViewBuilder;
import eu.koboo.minestom.stomui.api.ViewType;
import eu.koboo.minestom.stomui.api.component.ViewProvider;
import eu.koboo.minestom.stomui.api.item.PrebuiltItem;
import eu.koboo.minestom.stomui.api.item.ViewItem;
import me.wyndev.minigame.Main;
import me.wyndev.minigame.bedwars.player.BedwarsPlayer;
import me.wyndev.minigame.bedwars.util.Msg;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.component.DataComponents;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.CustomData;
import net.minestom.server.item.component.HeadProfile;
import net.minestom.server.item.component.TooltipDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class SpectatorSelectMenu extends ViewProvider {

    public SpectatorSelectMenu() {
        super(Main.VIEW_REGISTRY, ViewBuilder.of(ViewType.SIZE_3_X_9).title(Msg.mm("Spectate a player")));
    }

    @Override
    public void onOpen(@NotNull PlayerView view, @NotNull Player p) {
        AtomicInteger i = new AtomicInteger(0);
        Main.getGameManager().getTeams().forEach(team -> team.getPlayers().forEach(player -> {
            PlayerSkin skin = player.getSkin();
            if (skin == null) return;
            ItemStack stack = ItemStack.builder(Material.PLAYER_HEAD)
                    .set(DataComponents.PROFILE, new HeadProfile(skin))
                    .set(DataComponents.TOOLTIP_DISPLAY, new TooltipDisplay(false, Set.of(DataComponents.EQUIPPABLE, DataComponents.UNBREAKABLE)))
                    .set(DataComponents.ITEM_NAME, Msg.green(player.getUsername()))
                    .set(DataComponents.LORE, List.of((Msg.grey("Click to teleport to %s", player.getUsername()))))
                    .set(DataComponents.CUSTOM_DATA, new CustomData(CompoundBinaryTag.builder().putString("uuid", player.getUuid().toString()).build()))
                    .build();
            PrebuiltItem item = PrebuiltItem.of(stack, action -> {
                action.getEvent().setCancelled(true);
                if (!(player.getParentEntity() instanceof Player clicker)) return;
                Entity clicked = Main.getGameManager().getGameWorld().getEntityByUuid(UUID.fromString(Objects.requireNonNull(action.getEvent().getClickedItem().get(DataComponents.CUSTOM_DATA)).nbt().getString("uuid")));
                if (clicked instanceof LivingEntity livingEntity) {
                    BedwarsPlayer other = Main.getGameManager().getBedwarsPlayerFor(livingEntity);
                    if (other != null) {
                        clicker.sendMessage(Msg.success("Teleported you to " + other.getUsername() + "!"));
                        clicker.teleport(livingEntity.getPosition());
                        return;
                    }
                }
                clicker.sendMessage(Msg.whoops("That player is not online."));
                open(clicker);
            });
            ViewItem.bySlot(view, i.getAndIncrement()).applyPrebuilt(item);
        }));
    }
}
