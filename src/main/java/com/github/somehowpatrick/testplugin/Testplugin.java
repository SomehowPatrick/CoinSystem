package com.github.somehowpatrick.testplugin;

import com.github.somehowpatrick.testplugin.command.CoinCommand;
import com.github.somehowpatrick.testplugin.data.PlayerCoinsRepository;
import com.github.somehowpatrick.testplugin.listener.PlayerListener;
import com.github.somehowpatrick.testplugin.mongo.MongoConnection;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class Testplugin extends JavaPlugin {
    private MongoConnection mongoConnection;
    private MongoCollection<Document> mongoCollection;
    private PlayerCoinsRepository playerCoinsRepository;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        this.mongoConnection = new MongoConnection(getConfig().getString("url"));
        this.mongoConnection.connect(getConfig().getString("database"));
        this.mongoCollection = this.mongoConnection.getCollection(getConfig().getString("collection"));
        this.playerCoinsRepository = new PlayerCoinsRepository(this.mongoCollection, getConfig().getInt("default_coins"));
        getLogger().info("Testplugin | Connection successfully enabled!");
        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        Objects.requireNonNull(getCommand("coins")).setExecutor(new CoinCommand(this));
        getLogger().info("Testplugin | Plugin loaded!");
    }

    @Override
    public void onDisable() {
        this.mongoConnection.disable();
        getLogger().info("Testplugin | Connection successfully disabled!");
    }

    public MongoCollection<Document> getMongoCollection() {
        return this.mongoCollection;
    }

    public PlayerCoinsRepository getPlayerCoinsRepository() {
        return this.playerCoinsRepository;
    }
}

