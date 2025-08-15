package me.wyndev.minigame.bedwars.menu.shop;

import me.wyndev.minigame.bedwars.util.Items;

public class PotionShopMenu extends ShopMenu {
    public PotionShopMenu() {
        super("Potions");
    }

    @Override
    protected void setupShop() {
        setSlot('F', Items.MENU_FIRE_RESISTANCE_POTION, "fire resistance potion", new ItemCost("GOLD", 6), (player, bedwarsPlayer) -> {
            player.getInventory().addItemStack(Items.FIRE_RESISTANCE_POTION);
        });
        setSlot('I', Items.MENU_INVISIBILITY_POTION, "invisibility potion", new ItemCost("EMERALD", 2), (player, bedwarsPlayer) -> {
            player.getInventory().addItemStack(Items.INVISIBILITY_POTION);
        });
        setSlot('J', Items.MENU_JUMP_BOOST_POTION, "jump boost potion", new ItemCost("EMERALD", 1), (player, bedwarsPlayer) -> {
            player.getInventory().addItemStack(Items.JUMP_BOOST_POTION);
        });
        setSlot('S', Items.MENU_SPEED_POTION, "speed potion", new ItemCost("EMERALD", 1), (player, bedwarsPlayer) -> {
            player.getInventory().addItemStack(Items.SPEED_POTION);
        });
    }

    @Override
    protected String[] pattern() {
        return new String[]{
                "#bcatpur#",
                "#####s###",
                "#########",
                "##FIJS###",
                "#########"
        };
    }
}
