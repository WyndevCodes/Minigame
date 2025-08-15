package me.wyndev.minigame.bedwars.menu.shop;

public class CombatShopMenu extends ShopMenu {
    public CombatShopMenu() {
        super("Combat");
    }

    @Override
    protected void setupShop() {
        //todo
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
