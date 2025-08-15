package me.wyndev.minigame.bedwars.menu.shop;

public class PotionShopMenu extends ShopMenu {
    public PotionShopMenu() {
        super("Potions");
    }

    @Override
    protected void setupShop() {
        //todo
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
