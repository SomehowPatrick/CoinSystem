package com.github.somehowpatrick.testplugin.data;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerCoinsRepository {
    private final Map<String, Integer> cache;
    private final MongoCollection<Document> collection;
    private final int default_coins;

    public PlayerCoinsRepository(MongoCollection<Document> mongoCollection, int default_coins) {
        this.cache = new HashMap<>();
        this.collection = mongoCollection;
        this.default_coins = default_coins;
    }

    public void playerQuit(Player player) {
        var filter = new Document("uuid", player.getUniqueId().toString());
        var doc = new Document("uuid", player.getUniqueId().toString())
                .append("coins", getCoinsForPlayer(player));
        this.cache.remove(player.getUniqueId().toString());
        if (this.collection.find(filter).first() != null) {
            this.collection.replaceOne(filter, doc);
            return;
        }
        this.collection.insertOne(doc);
    }

    public void playerJoin(Player player) {
        var iter = this.collection.find(new Document("uuid", player.getUniqueId().toString()));
        var res = iter.first();

        if (res == null || res.isEmpty()) {
            this.cache.put(player.getUniqueId().toString(), default_coins);
            return;
        }
        this.cache.put(player.getUniqueId().toString(), res.getInteger("coins"));
    }

    public void setCoins(Player player, int coins) {
        this.cache.put(player.getUniqueId().toString(), coins);
    }

    public void transfer(Player from, Player to, int coins) {
        setCoins(from, getCoinsForPlayer(from) - coins);
        setCoins(to, getCoinsForPlayer(to) + coins);
    }

    public void removeCoins(Player player, int coins) {
        setCoins(player, getCoinsForPlayer(player) - coins);
    }

    public void addCoins(Player player, int coins) {
        setCoins(player, getCoinsForPlayer(player) + coins);
    }

    public int getCoinsForPlayer(Player player) {
        return getCoinsForUUID(player.getUniqueId().toString());
    }

    public int getCoinsForUUID(String uuid) {
        return this.cache.get(uuid);
    }

    public void resetCoins(Player target) {
        this.cache.put(target.getUniqueId().toString(), this.default_coins);
    }
}
