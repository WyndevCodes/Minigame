package me.wyndev.minigame.bedwars.menu.shop;

public class RotatingShopMenu extends ShopMenu {
    public RotatingShopMenu() {
        super("Rotating Items");
    }

    @Override
    protected void setupShop() {
        //todo
    }

    @Override
    protected String[] pattern() {
        return new String[]{
                "#bcatpur#",
                "#######s#",
                "#########",
                "#########",
                "#########"
        };
    }
}
