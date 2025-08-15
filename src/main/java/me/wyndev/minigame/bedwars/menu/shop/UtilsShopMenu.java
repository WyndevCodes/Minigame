package me.wyndev.minigame.bedwars.menu.shop;

import me.wyndev.minigame.bedwars.util.Items;
import net.minestom.server.item.ItemStack;

public class UtilsShopMenu extends ShopMenu {
    public UtilsShopMenu() {
        super("Utilities");
    }

    @Override
    protected void setupShop() {
        setSlot('F', Items.MENU_FIREBALL, "fireball", new ItemCost("IRON", 40), (player, bedwarsPlayer) -> {
            ItemStack item = Items.FIREBALL;
            item = item.withAmount(1);
            player.getInventory().addItemStack(item);
        });
        setSlot('T', Items.MENU_TNT, "tnt", new ItemCost("GOLD", 4), (player, bedwarsPlayer) -> {
            ItemStack item = Items.TNT;
            item = item.withAmount(1);
            player.getInventory().addItemStack(item);
        });
    }

    @Override
    protected String[] pattern() {
        return new String[]{
                "#bcatpur#",
                "######s##",
                "###F#T###",
                "#########",
                "#########"
        };
    }
}
