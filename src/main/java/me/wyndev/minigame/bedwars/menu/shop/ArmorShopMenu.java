package me.wyndev.minigame.bedwars.menu.shop;

import me.wyndev.minigame.bedwars.data.state.ArmorLevel;
import me.wyndev.minigame.bedwars.util.Items;
import net.minestom.server.entity.EquipmentSlot;

public class ArmorShopMenu extends ShopMenu {

    public ArmorShopMenu() {
        super("Armor");
    }

    @Override
    protected void setupShop() {
        setSlot('C', Items.MENU_CHAINMAIL_BOOTS, "permanent chain armor", new ItemCost("IRON", 40), (player, bedwarsPlayer) -> {
            player.getInventory().setEquipment(EquipmentSlot.LEGGINGS, player.getHeldSlot(), Items.CHAINMAIL_LEGS);
            player.getInventory().setEquipment(EquipmentSlot.BOOTS, player.getHeldSlot(), Items.CHAINMAIL_BOOTS);
            bedwarsPlayer.setArmorLevel(ArmorLevel.CHAINMAIL);
        });

        setSlot('I', Items.MENU_IRON_BOOTS, "permanent iron armor", new ItemCost("GOLD", 12), (player, bedwarsPlayer) -> {
            player.getInventory().setEquipment(EquipmentSlot.LEGGINGS, player.getHeldSlot(), Items.IRON_LEGS);
            player.getInventory().setEquipment(EquipmentSlot.BOOTS, player.getHeldSlot(), Items.IRON_BOOTS);
            bedwarsPlayer.setArmorLevel(ArmorLevel.IRON);
        });

        setSlot('D', Items.MENU_DIAMOND_BOOTS, "permanent diamond armor", new ItemCost("EMERALD", 6), (player, bedwarsPlayer) -> {
            player.getInventory().setEquipment(EquipmentSlot.LEGGINGS, player.getHeldSlot(), Items.DIAMOND_LEGS);
            player.getInventory().setEquipment(EquipmentSlot.BOOTS, player.getHeldSlot(), Items.DIAMOND_BOOTS);
            bedwarsPlayer.setArmorLevel(ArmorLevel.DIAMOND);
        });

        setSlot('N', Items.MENU_NETHERITE_BOOTS, "permanent netherite armor", new ItemCost("EMERALD", 16), (player, bedwarsPlayer) -> {
            player.getInventory().setEquipment(EquipmentSlot.LEGGINGS, player.getHeldSlot(), Items.NETHERITE_LEGS);
            player.getInventory().setEquipment(EquipmentSlot.BOOTS, player.getHeldSlot(), Items.NETHERITE_BOOTS);
            bedwarsPlayer.setArmorLevel(ArmorLevel.NETHERITE);
        });
    }

    @Override
    protected String[] pattern() {
        return new String[]{
                "#bcatpur#",
                "###s#####",
                "####C####",
                "###IDN###",
                "#########"
        };
    }
}
