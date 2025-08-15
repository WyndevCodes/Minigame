package me.wyndev.minigame.bedwars.menu.shop;

import me.wyndev.minigame.Main;
import me.wyndev.minigame.bedwars.util.Items;
import net.minestom.server.item.ItemStack;

public class CombatShopMenu extends ShopMenu {
    public CombatShopMenu() {
        super("Combat");
    }

    @Override
    protected void setupShop() {
        setSlot(19, Items.MENU_STONE_SWORD, "stone sword", new ItemCost("IRON", 10), (player, bedwarsPlayer) -> {
            Main.getGameManager().getPlayerInventoryManager().setSword(Items.STONE_SWORD, player);
        });
        setSlot(20, Items.MENU_IRON_SWORD, "iron sword", new ItemCost("GOLD", 7), (player, bedwarsPlayer) -> {
            Main.getGameManager().getPlayerInventoryManager().setSword(Items.IRON_SWORD, player);
        });
        setSlot(21, Items.MENU_DIAMOND_SWORD, "diamond sword", new ItemCost("EMERALD", 3), (player, bedwarsPlayer) -> {
            Main.getGameManager().getPlayerInventoryManager().setSword(Items.DIAMOND_SWORD, player);
        });
        setSlot(22, Items.MENU_BOW_1, "bow", new ItemCost("GOLD", 10), (player, bedwarsPlayer) -> {
            player.getInventory().addItemStack(Items.BOW_1);
        });
        setSlot(23, Items.MENU_BOW_2, "bow", new ItemCost("GOLD", 18), (player, bedwarsPlayer) -> {
            Main.getGameManager().getPlayerInventoryManager().takeItem("BOW_1", 1, player);
            player.getInventory().addItemStack(Items.BOW_2);
        });
        setSlot(24, Items.MENU_BOW_3, "bow", new ItemCost("IRON", 10), (player, bedwarsPlayer) -> {
            Main.getGameManager().getPlayerInventoryManager().takeItem("BOW_2", 1, player);
            player.getInventory().addItemStack(Items.BOW_3);
        });
        setSlot(25, Items.MENU_ARROW, "some arrows", new ItemCost("GOLD", 2), (player, bedwarsPlayer) -> {
            ItemStack item = Items.ARROW;
            item = item.withAmount(8);
            player.getInventory().addItemStack(item);
        });
    }

    @Override
    protected String[] pattern() {
        return new String[]{
                "#bcatpur#",
                "##s######",
                "#########",
                "#########",
                "#########"
        };
    }
}
