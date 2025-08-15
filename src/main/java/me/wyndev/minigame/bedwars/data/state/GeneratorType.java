package me.wyndev.minigame.bedwars.data.state;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.wyndev.minigame.bedwars.util.Items;
import me.wyndev.minigame.bedwars.util.Msg;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

@AllArgsConstructor
@Getter
public enum GeneratorType {
    IRON(Items.get("IRON"), null, null),
    GOLD(Items.get("GOLD"), null, null),
    DIAMOND(Items.get("DIAMOND"), Msg.aqua("<bold>Diamond Generator"), ItemStack.of(Material.DIAMOND_BLOCK)),
    EMERALD(Items.get("EMERALD"), Msg.green("<bold>Emerald Generator"), ItemStack.of(Material.EMERALD_BLOCK));
    private final ItemStack item;
    private final Component name;
    private final ItemStack visualItem;
}
