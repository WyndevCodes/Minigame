package me.wyndev.minigame.bedwars.command;

import me.wyndev.minigame.bedwars.util.Items;
import me.wyndev.minigame.bedwars.util.Msg;
import me.wyndev.minigame.command.MinigameCommand;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.entity.Player;
import net.minestom.server.item.ItemStack;

/**
 * Bedwars item command.
 * @author webhead1104/Cytonic
 */
public class ItemCommand extends MinigameCommand {

    public ItemCommand() {
        super("item", "i");
        var itemArgument = ArgumentType.Word("item");
        itemArgument.setSuggestionCallback(((commandSender, commandContext, suggestion) ->
                Items.getItemIDs().forEach(a -> suggestion.addEntry(new SuggestionEntry(a)))));
        var amountArgument = ArgumentType.Integer("amount");

        addSyntax((sender, context) -> {
            if (!hasPermission(sender)) return;
            if (sender instanceof Player player) {
                String item = context.get(itemArgument);
                if (Items.get(item) != null) {
                    player.getInventory().addItemStack(Items.get(item));
                    player.sendMessage(Msg.green("Gave you 1 %s", item));
                } else {
                    player.sendMessage(Msg.red("Invalid item ID: '%s'", item));
                }
            }
        }, itemArgument);
        addSyntax((sender, context) -> {
            if (!hasPermission(sender)) return;
            if (sender instanceof Player player) {
                int amount = context.get(amountArgument);
                String item = context.get(itemArgument);
                if (Items.get(item) != null) {
                    ItemStack foo = Items.get(item);
                    foo = foo.withAmount(amount);
                    player.getInventory().addItemStack(foo);
                    player.sendMessage(Msg.green("Gave you %d %s", amount, item));
                } else {
                    player.sendMessage(Msg.red("Invalid item ID: '%s'", item));
                }
            }
        }, itemArgument, amountArgument);
    }

    @Override
    public void executeDefault(CommandSender sender, CommandContext context) {
        sender.sendMessage(Msg.red("Please specify an item ID!"));
    }

    @Override
    public int requiredPermissionLevel() {
        return 4;
    }
}