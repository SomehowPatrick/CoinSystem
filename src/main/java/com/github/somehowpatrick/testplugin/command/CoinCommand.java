package com.github.somehowpatrick.testplugin.command;

import com.github.somehowpatrick.testplugin.Testplugin;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public record CoinCommand(Testplugin testplugin) implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return false;
        }

        if (args.length == 0) {
            var coins = testplugin.getPlayerCoinsRepository().getCoinsForPlayer(player);
            player.sendMessage("§aDu hast §e" + coins + "§a " + (coins == 1 ? "Coin" : "Coins") + "!");
            return false;
        }
        if (args.length == 1) {
            var target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage("§cDer Spieler §e" + args[0] + "§c ist nicht online!");
                return false;
            }
            var coins = testplugin.getPlayerCoinsRepository().getCoinsForPlayer(target);
            player.sendMessage("§aDer Spieler §e" + args[0] + "§a hat §e" + coins + "§a " + (coins == 1 ? "Coin" : "Coins") + "!");
            return false;
        }
        if (args.length == 2) {
            if (!args[0].equalsIgnoreCase("reset")) {
                printHelp(player);
                return false;
            }
            if (!player.hasPermission("coins.admin")) {
                player.sendMessage("§cDazu hast du keine Rechte!");
                return false;
            }
            var target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage("§cDer Spieler §e" + args[1] + "§c ist nicht online!");
                return false;
            }
            this.testplugin.getPlayerCoinsRepository().resetCoins(target);
            player.sendMessage("§aDu hast die Coins von §e" + args[1] + "§a zurückgesetzt!");
            target.sendMessage("§aDeine Coins wurden von §e" + player.getName() + "§a zurückgesetzt!");
            return false;
        }
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("pay")) {
                if (!player.hasPermission("coins.admin")) {
                    player.sendMessage("§cDazu hast du keine Rechte!");
                    return false;
                }
                var target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage("§cDer Spieler §e" + args[1] + "§c ist nicht online!");
                    return false;
                }
                var amount = args[2];
                var a_final = NumberUtils.toInt(amount);
                if (!NumberUtils.isNumber(amount)) {
                    player.sendMessage("§e" + amount + "§c ist keine Zahl!");
                    return false;
                }
                if (amount.startsWith("-")) {
                    player.sendMessage("§cDiese Zahl ist negativ!");
                    return false;
                }
                if (this.testplugin.getPlayerCoinsRepository().getCoinsForPlayer(player) - a_final < 0) {
                    player.sendMessage("§cDu hast nicht genügend Coins um das zu tun!");
                    return false;
                }
                this.testplugin.getPlayerCoinsRepository().removeCoins(player, a_final);
                this.testplugin.getPlayerCoinsRepository().addCoins(target, a_final);
                player.sendMessage("§aDu hast §e" + target.getName() + " " + a_final + "§a " + (a_final == 1 ? "Coin" : "Coins") + " überwiesen!");
                target.sendMessage("§aDu hast von §e" + target.getName() + " " + a_final + "§a " + (a_final == 1 ? "Coin" : "Coins") + " bekommen!");
                return false;
            }
            if (args[0].equalsIgnoreCase("set")) {
                var target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    player.sendMessage("§cDer Spieler §e" + args[1] + "§c ist nicht online!");
                    return false;
                }
                var amount = args[2];
                var a_final = NumberUtils.toInt(amount);
                if (!NumberUtils.isNumber(amount)) {
                    player.sendMessage("§e" + amount + "§c ist keine Zahl!");
                    return false;
                }
                if (amount.startsWith("-")) {
                    player.sendMessage("§cDiese Zahl ist negativ!");
                    return false;
                }
                this.testplugin.getPlayerCoinsRepository().setCoins(target, a_final);
                player.sendMessage("§aDu hast die Coins von §e" + target.getName() + " " + a_final + "§aauf " + (a_final == 1 ? "Coin" : "Coins") + " gesetzt!");
                target.sendMessage("§aDeine Coins wurden von §e" + target.getName() + "§a auf §e" + a_final + " " + (a_final == 1 ? "Coin" : "Coins") + " gesetzt!");
                return false;
            }
            return false;
        }
        return false;
    }

    private void printHelp(Player player) {
        player.sendMessage("§aHelp §7| §eCoins");
        player.sendMessage("§a/coins §7| §eZeige deine Coins");
        player.sendMessage("§a/coins <Spieler> §7| §eZeige die Coins eines anderen Spielers");
        player.sendMessage("§a/coins pay <Spieler> <Anzahl> §7| §eTransferiere Coins zu einem anderen Spieler");
        if (player.hasPermission("coins.admin")) {
            player.sendMessage("§a/coins reset <Spieler> §7| §eSetze die Coins eines Spielers zurück");
            player.sendMessage("§a/coins set <Spieler> <Anzahl> §7| §eSetze die Coins eines Spielers auf eine bestimmte Anzahl");
        }
    }
}
