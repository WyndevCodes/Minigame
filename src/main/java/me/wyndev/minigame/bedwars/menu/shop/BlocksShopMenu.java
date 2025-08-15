package me.wyndev.minigame.bedwars.menu.shop;

import me.wyndev.minigame.Main;
import me.wyndev.minigame.bedwars.data.state.MappableItem;
import me.wyndev.minigame.bedwars.util.Items;
import net.minestom.server.item.ItemStack;

public class BlocksShopMenu extends ShopMenu {
    public BlocksShopMenu() {
        super("Blocks");
    }

    @Override
    protected void setupShop() {
        setSlot('W', Items.MENU_WOOL, "16 wool", new ItemCost("IRON", 4), (player, bedwarsPlayer) -> {
            ItemStack itemStack = Items.getTeamMapped(MappableItem.WOOL, Main.getGameManager().getPlayerTeam(player).orElseThrow());
            itemStack = itemStack.withAmount(16);
            player.getInventory().addItemStack(itemStack);
        });

        setSlot('B', Items.MENU_BLAST_GLASS, "4 blast-proof glass", new ItemCost("IRON", 12), (player, bedwarsPlayer) -> {
            ItemStack itemStack = Items.getTeamMapped(MappableItem.GLASS, Main.getGameManager().getPlayerTeam(player).orElseThrow());
            itemStack = itemStack.withAmount(4);
            player.getInventory().addItemStack(itemStack);
        });

        setSlot('E', Items.MENU_END_STONE, "12 end stone", new ItemCost("IRON", 24), (player, bedwarsPlayer) -> {
            ItemStack item = Items.END_STONE;
            item = item.withAmount(12);
            player.getInventory().addItemStack(item);
        });

        setSlot('T', Items.MENU_TERRACOTTA, "12 terracotta", new ItemCost("IRON", 10), (player, bedwarsPlayer) -> {
            ItemStack itemStack = Items.getTeamMapped(MappableItem.TERRACOTTA, Main.getGameManager().getPlayerTeam(player).orElseThrow());
            itemStack = itemStack.withAmount(4);
            player.getInventory().addItemStack(itemStack);
        });

        setSlot('O', Items.MENU_OBSIDIAN, "4 obsidian", new ItemCost("EMERALD", 4), (player, bedwarsPlayer) -> {
            ItemStack item = Items.OBSIDIAN;
            item = item.withAmount(4);
            player.getInventory().addItemStack(item);
        });

        setSlot('P', Items.MENU_PLANKS, "8 planks", new ItemCost("GOLD", 4), (player, bedwarsPlayer) -> {
            ItemStack item = Items.PLANKS;
            item = item.withAmount(8);
            player.getInventory().addItemStack(item);
        });
    }

    @Override
    protected String[] pattern() {
        return new String[]{
                "#bcatpur#",
                "#s#######",
                "###WBE###",
                "###TOP###",
                "#########"
        };
    }
}
