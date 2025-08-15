package me.wyndev.minigame.bedwars.listener;

import me.wyndev.minigame.Main;
import me.wyndev.minigame.bedwars.util.Items;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.EquipmentSlot;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.inventory.InventoryItemChangeEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class ArmorEquipListener extends BedwarsEvent<InventoryItemChangeEvent> {

    public ArmorEquipListener(Instance gameWorld) {
        super(gameWorld);
    }

    @Override
    public @NotNull Class<InventoryItemChangeEvent> eventType() {
        return InventoryItemChangeEvent.class;
    }

    @Override
    public void onEvent(InventoryItemChangeEvent event) {
        if (!(event.getInventory() instanceof PlayerInventory playerInventory)) return;
        for (Player player : playerInventory.getViewers()) {
            if (isInstanceNot(player.getInstance())) continue;
            if (event.getPreviousItem().material() == Material.BARRIER && player.getGameMode() == GameMode.ADVENTURE && !Main.getGameManager().getSpectators().contains(player.getUuid())) {
                MinecraftServer.getSchedulerManager().buildTask(() -> {
                    playerInventory.setEquipment(EquipmentSlot.BOOTS, player.getHeldSlot(), Items.SPECTATOR_ARMOR);
                    playerInventory.setEquipment(EquipmentSlot.LEGGINGS, player.getHeldSlot(), Items.SPECTATOR_ARMOR);
                    playerInventory.setEquipment(EquipmentSlot.CHESTPLATE, player.getHeldSlot(), Items.SPECTATOR_ARMOR);
                }).delay(Duration.ofMillis(100)).schedule();
            }
        }
    }

    @Override
    public Instance instanceFromEvent(InventoryItemChangeEvent event) {
        return null; //check instance manually
    }
}
