package com.github.somehowpatrick.testplugin.listener;

import com.github.somehowpatrick.testplugin.Testplugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public record PlayerListener(Testplugin testplugin) implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent playerJoinEvent) {
        this.testplugin.getPlayerCoinsRepository().playerJoin(playerJoinEvent.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent playerQuitEvent) {
        this.testplugin.getPlayerCoinsRepository().playerQuit(playerQuitEvent.getPlayer());
    }

}
