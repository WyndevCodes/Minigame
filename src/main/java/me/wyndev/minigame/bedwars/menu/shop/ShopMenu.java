package me.wyndev.minigame.bedwars.menu.shop;

import eu.koboo.minestom.stomui.api.PlayerView;
import eu.koboo.minestom.stomui.api.ViewBuilder;
import eu.koboo.minestom.stomui.api.ViewType;
import eu.koboo.minestom.stomui.api.component.ViewProvider;
import eu.koboo.minestom.stomui.api.item.PrebuiltItem;
import eu.koboo.minestom.stomui.api.item.ViewItem;
import eu.koboo.minestom.stomui.api.slots.ViewPattern;
import me.wyndev.minigame.Main;
import me.wyndev.minigame.bedwars.menu.MenuUtils;
import me.wyndev.minigame.bedwars.player.BedwarsPlayer;
import me.wyndev.minigame.bedwars.util.Msg;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;
import net.minestom.server.sound.SoundEvent;
import org.apache.commons.text.WordUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public abstract class ShopMenu extends ViewProvider {

    private final ViewPattern pattern;
    private final Map<Integer, ShopItem> shopItems;

    public ShopMenu(String categoryName) {
        super(Main.VIEW_REGISTRY, ViewBuilder.of(ViewType.SIZE_5_X_9).title(Msg.mm("Item Shop ➜ " + categoryName)));
        shopItems = new HashMap<>();
        pattern = Main.VIEW_REGISTRY.pattern(pattern());
        setupShop();
    }

    protected boolean checkState() {
        return true;
    }

    protected abstract void setupShop();

    protected abstract String[] pattern();

    protected void setSlot(char patternID, ItemStack item, String purchaseName, ItemCost itemCost, BiConsumer<Player, BedwarsPlayer> whenPurchased) {
        setSlot(patternID, (player, bedwarsPlayer) -> item, (player, bedwarsPlayer) -> purchaseName, (player, bedwarsPlayer) -> itemCost, whenPurchased);
    }

    protected void setSlot(int patternID, ItemStack item, String purchaseName, ItemCost itemCost, BiConsumer<Player, BedwarsPlayer> whenPurchased) {
        setSlot(patternID, (player, bedwarsPlayer) -> item, (player, bedwarsPlayer) -> purchaseName, (player, bedwarsPlayer) -> itemCost, whenPurchased);
    }

    protected void setSlot(char patternID,
                           BiFunction<Player, @NotNull BedwarsPlayer, ItemStack> itemGetterFunction,
                           BiFunction<Player, @NotNull BedwarsPlayer, String> purchaseNameFunction,
                           BiFunction<Player, @NotNull BedwarsPlayer, ItemCost> itemCostFunction,
                           BiConsumer<Player, BedwarsPlayer> whenPurchased) {
        setSlot(pattern.getSlot(patternID), itemGetterFunction, purchaseNameFunction, itemCostFunction, whenPurchased);
    }

    protected void setSlot(int slot,
                           BiFunction<Player, @NotNull BedwarsPlayer, ItemStack> itemGetterFunction,
                           BiFunction<Player, @NotNull BedwarsPlayer, String> purchaseNameFunction,
                           BiFunction<Player, @NotNull BedwarsPlayer, ItemCost> itemCostFunction,
                           BiConsumer<Player, BedwarsPlayer> whenPurchased) {
        ShopItem shopItem = new ShopItem(itemGetterFunction, purchaseNameFunction, itemCostFunction, whenPurchased);
        shopItems.put(slot, shopItem);
    }

    @Override
    public void onOpen(@NotNull PlayerView view, @NotNull Player player) {
        if (!checkState()) return;
        BedwarsPlayer bedwarsPlayer = Main.getGameManager().getBedwarsPlayerFor(player);
        if (bedwarsPlayer == null) return;

        MenuUtils.setItemShopItems(view, pattern);
        shopItems.forEach((slot, shopItem) -> ViewItem.bySlot(view, slot).applyPrebuilt(shopItem.getPrebuiltItem(player, bedwarsPlayer)));
    }

    protected record ShopItem(BiFunction<Player, @NotNull BedwarsPlayer, ItemStack> itemGetter,
                              BiFunction<Player, @NotNull BedwarsPlayer, String> purchaseNameFunction,
                              BiFunction<Player, @NotNull BedwarsPlayer, @Nullable ItemCost> itemCostFunction,
                              BiConsumer<Player, BedwarsPlayer> whenPurchased) {

        public PrebuiltItem getPrebuiltItem(Player viewer, BedwarsPlayer bedwarsViewer) {
            return PrebuiltItem.of(itemGetter.apply(viewer, bedwarsViewer), action -> {
                action.getEvent().setCancelled(true);

                Player player = action.getPlayer();
                BedwarsPlayer bedwarsPlayer = Main.getGameManager().getBedwarsPlayerFor(player);
                if (bedwarsPlayer == null) return;

                if (Main.getGameManager().getPlayerInventoryManager().hasSpace(player)) {
                    ItemCost cost = itemCostFunction.apply(player, bedwarsPlayer);
                    if (cost != null && !cost.tryPurchase(player)) {
                        player.sendMessage(Msg.red("You need at least " + cost.getCountText() + " to buy this!"));
                        player.playSound(Sound.sound(SoundEvent.ENTITY_VILLAGER_NO, Sound.Source.MASTER, 1, 1));
                        return;
                    }

                    whenPurchased.accept(player, bedwarsPlayer);
                    if (cost != null) {
                        player.sendMessage(Msg.green("You bought " + purchaseNameFunction.apply(player, bedwarsPlayer) + "!"));
                        player.playSound(Sound.sound(SoundEvent.BLOCK_NOTE_BLOCK_PLING, Sound.Source.MASTER, 1, 1));
                    }
                } else {
                    player.sendMessage(Msg.red("Your inventory is full!"));
                    player.playSound(Sound.sound(SoundEvent.ENTITY_VILLAGER_NO, Sound.Source.MASTER, 1, 1));
                }
            });
        }

    }

    protected record ItemCost(String itemID, int amount) {

        public boolean tryPurchase(Player player) {
            return Main.getGameManager().getPlayerInventoryManager().takeItem(itemID, amount, player);
        }

        public String getCountText() {
            return amount + " " + WordUtils.capitalizeFully(itemID.toLowerCase().replaceAll("_", " ")) + (amount > 1 ? "s" : "");
        }

    }

}
