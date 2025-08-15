package me.wyndev.minigame.bedwars.manager;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.wyndev.minigame.bedwars.data.stat.BedwarsStats;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@NoArgsConstructor
@Getter
public class StatsManager {
    private final Map<UUID, BedwarsStats> stats = new HashMap<>();

    public void addPlayer(UUID uuid) {
        stats.put(uuid, new BedwarsStats(0, 0, 0, 0, 0, 0));
    }

    public BedwarsStats getStats(UUID uuid) {
        return stats.get(uuid);
    }
}
