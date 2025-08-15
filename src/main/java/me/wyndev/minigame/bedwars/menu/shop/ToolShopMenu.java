package me.wyndev.minigame.bedwars.menu.shop;

import me.wyndev.minigame.Main;
import me.wyndev.minigame.bedwars.data.state.AxeLevel;
import me.wyndev.minigame.bedwars.data.state.PickaxeLevel;
import me.wyndev.minigame.bedwars.util.Items;
import me.wyndev.minigame.bedwars.util.Msg;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.sound.SoundEvent;

public class ToolShopMenu extends ShopMenu {
    public ToolShopMenu() {
        super("Tools");
    }

    @Override
    protected boolean checkState() {
        if (!Main.getGameManager().STARTED) {
            System.out.println("The game must be started to generate a tool shop!");
            return false;
        }
        return true;
    }

    @Override
    protected void setupShop() {
        //axe
        setSlot('A', (player, bedwarsPlayer) -> switch (bedwarsPlayer.getAxeLevel()) {
            case NONE -> Items.MENU_WOODEN_AXE;
            case WOODEN -> Items.MENU_STONE_AXE;
            case STONE -> Items.MENU_IRON_AXE;
            case IRON -> Items.MENU_DIAMOND_AXE;
            case DIAMOND -> Items.MENU_DIAMOND_AXE.withLore(Msg.green("<bold>Already purchased!"));
        }, (player, bedwarsPlayer) -> switch (bedwarsPlayer.getAxeLevel()) {
            //use CURRENT axe (this is called AFTER purchase logic)
            case NONE -> "";
            case WOODEN -> "wooden axe";
            case STONE -> "stone axe";
            case IRON -> "iron axe";
            case DIAMOND -> "diamond axe";
        }, (player, bedwarsPlayer) -> switch (bedwarsPlayer.getAxeLevel()) {
            //use cost for NEXT axe
            case NONE -> new ItemCost("IRON", 10);
            case WOODEN -> new ItemCost("IRON", 20);
            case STONE -> new ItemCost("GOLD", 6);
            case IRON -> new ItemCost("EMERALD", 3);
            case DIAMOND -> null;
        }, (player, bedwarsPlayer) -> {
            switch (bedwarsPlayer.getAxeLevel()) {
                case NONE -> {
                    bedwarsPlayer.setAxeLevel(AxeLevel.WOODEN);
                    Main.getGameManager().getPlayerInventoryManager().setAxe(AxeLevel.WOODEN, player);
                }
                case WOODEN -> {
                    bedwarsPlayer.setAxeLevel(AxeLevel.STONE);
                    Main.getGameManager().getPlayerInventoryManager().setAxe(AxeLevel.STONE, player);
                }
                case STONE -> {
                    bedwarsPlayer.setAxeLevel(AxeLevel.IRON);
                    Main.getGameManager().getPlayerInventoryManager().setAxe(AxeLevel.IRON, player);
                }
                case IRON -> {
                    bedwarsPlayer.setAxeLevel(AxeLevel.DIAMOND);
                    Main.getGameManager().getPlayerInventoryManager().setAxe(AxeLevel.DIAMOND, player);
                }
                case DIAMOND -> {
                    player.playSound(Sound.sound(SoundEvent.ENTITY_VILLAGER_NO, Sound.Source.MASTER, 1, 1));
                    player.sendMessage(Msg.red("You already purchased a diamond axe!"));
                }
            }
        });

        //pickaxe
        setSlot('P', (player, bedwarsPlayer) -> switch (bedwarsPlayer.getPickaxeLevel()) {
            case NONE -> Items.MENU_WOODEN_PICKAXE;
            case WOODEN -> Items.MENU_STONE_PICKAXE;
            case STONE -> Items.MENU_IRON_PICKAXE;
            case IRON -> Items.MENU_DIAMOND_PICKAXE;
            case DIAMOND -> Items.MENU_DIAMOND_AXE.withLore(Msg.green("<bold>Already purchased!"));
        }, (player, bedwarsPlayer) -> switch (bedwarsPlayer.getPickaxeLevel()) {
            //use CURRENT axe (this is called AFTER purchase logic)
            case NONE -> "";
            case WOODEN -> "wooden pickaxe";
            case STONE -> "stone pickaxe";
            case IRON -> "iron pickaxe";
            case DIAMOND -> "diamond pickaxe";
        }, (player, bedwarsPlayer) -> switch (bedwarsPlayer.getPickaxeLevel()) {
            //use cost for NEXT axe
            case NONE -> new ItemCost("IRON", 10);
            case WOODEN -> new ItemCost("IRON", 20);
            case STONE -> new ItemCost("GOLD", 6);
            case IRON -> new ItemCost("EMERALD", 3);
            case DIAMOND -> null;
        }, (player, bedwarsPlayer) -> {
            switch (bedwarsPlayer.getPickaxeLevel()) {
                case NONE -> {
                    bedwarsPlayer.setPickaxeLevel(PickaxeLevel.WOODEN);
                    Main.getGameManager().getPlayerInventoryManager().setPickaxe(PickaxeLevel.WOODEN, player);
                }
                case WOODEN -> {
                    bedwarsPlayer.setPickaxeLevel(PickaxeLevel.STONE);
                    Main.getGameManager().getPlayerInventoryManager().setPickaxe(PickaxeLevel.STONE, player);
                }
                case STONE -> {
                    bedwarsPlayer.setPickaxeLevel(PickaxeLevel.IRON);
                    Main.getGameManager().getPlayerInventoryManager().setPickaxe(PickaxeLevel.IRON, player);
                }
                case IRON -> {
                    bedwarsPlayer.setPickaxeLevel(PickaxeLevel.DIAMOND);
                    Main.getGameManager().getPlayerInventoryManager().setPickaxe(PickaxeLevel.DIAMOND, player);
                }
                case DIAMOND -> {
                    player.playSound(Sound.sound(SoundEvent.ENTITY_VILLAGER_NO, Sound.Source.MASTER, 1, 1));
                    player.sendMessage(Msg.red("You already purchased a diamond pickaxe!"));
                }
            }
        });

        //shears
        setSlot('S', (player, bedwarsPlayer) -> {
            if (bedwarsPlayer.hasShears()) {
                return Items.MENU_SHEARS.withLore(Msg.green("<bold>Already purchased!"));
            } else {
                return Items.MENU_SHEARS;
            }
        }, (player, bedwarsPlayer) -> "shears", (player, bedwarsPlayer) -> {
            if (bedwarsPlayer.hasShears()) {
                return null;
            } else {
                return new ItemCost("IRON", 20);
            }
        }, (player, bedwarsPlayer) -> {
            if (!bedwarsPlayer.hasShears()) {
                bedwarsPlayer.setShears(true);
                player.getInventory().addItemStack(Items.SHEARS);
            } else {
                player.playSound(Sound.sound(SoundEvent.ENTITY_VILLAGER_NO, Sound.Source.MASTER, 1, 1));
                player.sendMessage(Msg.red("You already purchased a pair of shears!"));
            }
        });
    }

    @Override
    protected String[] pattern() {
        return new String[]{
                "#bcatpur#",
                "####s####",
                "#########",
                "###APS###",
                "#########"
        };
    }
}
