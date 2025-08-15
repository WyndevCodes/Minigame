package me.wyndev.minigame.bedwars.listener;

import me.wyndev.minigame.bedwars.util.Items;
import me.wyndev.minigame.bedwars.util.Msg;
import net.kyori.adventure.nbt.CompoundBinaryTag;
import net.minestom.server.entity.GameMode;
import net.minestom.server.event.inventory.InventoryPreClickEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@SuppressWarnings("unused")
public class InventoryClickListener extends BedwarsEvent<InventoryPreClickEvent> {

    public InventoryClickListener(Instance gameWorld) {
        super(gameWorld);
    }

    public void onInventoryClick(InventoryPreClickEvent event) {
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE) return;
        ItemStack item = event.getClickedItem();
        if (item.hasTag(Items.MOVE_BLACKLIST)) {
            if (item.getTag(Items.MOVE_BLACKLIST)) {
                event.setCancelled(true);
                return;
            }
            int[] slots = ((CompoundBinaryTag) item.getTag(Items.ALLOWED_SLOTS)).getIntArray("allowed_slots");
            if (Arrays.stream(slots).allMatch(value -> value != event.getSlot())) {
                event.getPlayer().getInventory().setCursorItem(ItemStack.AIR);
                event.getPlayer().sendMessage(Msg.redSplash("HEY!", "We noticed a blacklisted item in your inventory, so we took it. Sorry! (slot %d)", event.getSlot()));
            }
        }
    }

    @Override
    public void onEvent(InventoryPreClickEvent event) {
        onInventoryClick(event);
    }

    @Override
    public @Nullable Instance instanceFromEvent(InventoryPreClickEvent event) {
        return event.getInstance();
    }

    @Override
    public @NotNull Class<InventoryPreClickEvent> eventType() {
        return InventoryPreClickEvent.class;
    }
}
