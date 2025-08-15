package me.wyndev.minigame.bedwars.listener;

import me.wyndev.minigame.Main;
import me.wyndev.minigame.bedwars.util.Items;
import me.wyndev.minigame.bot.PlayerBot;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PickupItemListener extends BedwarsEvent<PickupItemEvent> {

    public PickupItemListener(Instance gameWorld) {
        super(gameWorld);
    }

    public void onPickup(PickupItemEvent event) {
        final Entity entity = event.getLivingEntity();
        if (entity instanceof Player player) {
            if (Main.getGameManager().getSpectators().contains(player.getUuid())) {
                event.setCancelled(true);
            }
            final ItemStack item = event.getItemEntity().getItemStack();
            if (item.hasTag(Items.NAMESPACE) && item.getTag(Items.NAMESPACE).contains("SWORD") && !item.getTag(Items.NAMESPACE).contains("MENU")) {
                Main.getGameManager().getPlayerInventoryManager().setSword(item, player);
                event.getItemEntity().setItemStack(ItemStack.AIR);
                event.setCancelled(true);
                return;
            }
            final ItemStack itemStack = event.getItemEntity().getItemStack();
            event.setCancelled(!player.getInventory().addItemStack(itemStack));
        } else if (entity instanceof PlayerBot bot) {
            if (Main.getGameManager().getSpectators().contains(bot.getUuid())) {
                event.setCancelled(true);
            }
            final ItemStack item = event.getItemEntity().getItemStack();
            if (item.hasTag(Items.NAMESPACE)) {
                switch (item.getTag(Items.NAMESPACE)) {
                    case "IRON" -> bot.getBedwarsData().addIron(item.amount());
                    case "GOLD" -> bot.getBedwarsData().addGold(item.amount());
                    case "DIAMOND" -> bot.getBedwarsData().addDiamonds(item.amount());
                    case "EMERALD" -> bot.getBedwarsData().addEmeralds(item.amount());
                }
            }
        }
    }

    @Override
    public void onEvent(PickupItemEvent event) {
        onPickup(event);
    }

    @Override
    public @Nullable Instance instanceFromEvent(PickupItemEvent event) {
        return event.getInstance();
    }

    @Override
    public @NotNull Class<PickupItemEvent> eventType() {
        return PickupItemEvent.class;
    }
}
