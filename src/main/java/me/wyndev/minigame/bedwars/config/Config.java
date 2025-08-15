package me.wyndev.minigame.bedwars.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.wyndev.minigame.bedwars.data.gamecomponent.Team;
import me.wyndev.minigame.bedwars.data.state.GeneratorType;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.instance.block.Block;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This class is used to store all the cached configuration values.
 */
@AllArgsConstructor
@NoArgsConstructor
public final class Config {
    /**
     * The instance of Gson for serializing and deserializing objects. (Mostly for preferences).
     */
    public static final GsonConfigurationLoader.Builder GSON_CONFIGURATION_LOADER = GsonConfigurationLoader.builder()
            .indent(0)
            .defaultOptions(opts -> opts
                    .shouldCopyDefaults(true)
                    .serializers(builder -> {
                        builder.registerAnnotatedObjects(ObjectMapper.factory());
                        builder.register(Key.class, new KeySerializer());
                        builder.register(Pos.class, new PosSerializer());
                    })
            );

    public String worldName = "NULL";
    public String mapName = "NULL";
    public String mode = "NULL";
    public int minPlayers = 0;
    public int maxPlayers = 0;
    public int bridgeEggBlockDespawn = 0;
    public int bridgeEggBlockLimit = 0;
    public Pos spawnPlatformCenter = new Pos(0, 0, 0, 180, 0);
    public Map<String, Team> teams = new HashMap<>();
    public Map<GeneratorType, Integer> generatorsWaitTimeTicks = new HashMap<>();
    public Map<GeneratorType, Integer> generatorsItemLimit = new HashMap<>();
    public Map<GeneratorType, List<Pos>> generators = new HashMap<>();
    public PlayerSkin itemShopSkin;
    public PlayerSkin teamShopSkin;

    /**
     * Loads the config from a string
     *
     * @param data the data
     */
    public static Config importConfig(String data) {
        long time = System.currentTimeMillis();
        System.out.println("Importing bedwars config!");
        ConfigurationNode node;
        try {
            node = GSON_CONFIGURATION_LOADER.buildAndLoadString(data);
        } catch (ConfigurateException e) {
            System.out.println("Could not import config!");
            return new Config();
        }
        String worldName = node.node("world_name").getString();
        String mapName = node.node("map_name").getString();
        String mode = node.node("mode").getString();
        int minPlayers = node.node("min_players").getInt();
        int maxPlayers = node.node("max_players").getInt();
        int bridgeEggBlockDespawn = node.node("bridge_egg_block_despawn").getInt();
        int bridgeEggBlockLimit = node.node("bridge_egg_block_limit").getInt();
        Pos spawnPlatformCenter = new Pos(0, 0, 0, 180, 0);
        try {
            spawnPlatformCenter = node.node("spawn_platform_center").get(Pos.class);
        } catch (SerializationException e) {
            System.out.println("Could not import spawn platform center!");
        }

        Map<String, Team> teams = new HashMap<>();
        Map<GeneratorType, Integer> generatorsWaitTimeTicks = new HashMap<>();
        Map<GeneratorType, Integer> generatorsItemLimit = new HashMap<>();
        Map<GeneratorType, List<Pos>> generators = new HashMap<>();

        node.node("teams").childrenMap().forEach((key, teamNode) -> {
            try {
                Team team = new Team(
                        String.valueOf(key),
                        teamNode.node("tab_prefix").getString(),
                        NamedTextColor.NAMES.value(Objects.requireNonNull(teamNode.node("team_color").getString())),
                        Objects.requireNonNull(teamNode.node("spawn_location").get(Pos.class)),
                        teamNode.node("generation_location").get(Pos.class),
                        teamNode.node("item_shop_location").get(Pos.class),
                        teamNode.node("team_shop_location").get(Pos.class),
                        teamNode.node("team_chest_location").get(Pos.class),
                        teamNode.node("bed_location").get(Pos.class),
                        Block.fromKey(Objects.requireNonNull(teamNode.node("bed_item").get(Key.class))),
                        Block.fromKey(Objects.requireNonNull(teamNode.node("wool_item").get(Key.class))),
                        Block.fromKey(Objects.requireNonNull(teamNode.node("glass_item").get(Key.class))),
                        Block.fromKey(Objects.requireNonNull(teamNode.node("terracotta_item").get(Key.class)))
                );
                teams.put(String.valueOf(key), team);
            } catch (SerializationException e) {
                System.out.println("Could not import team!");
            }
        });
        node.node("generators_wait_time_ticks").childrenMap().forEach((key, value) -> generatorsWaitTimeTicks.put(GeneratorType.valueOf(String.valueOf(key).toUpperCase()), value.getInt()));
        node.node("generators_item_limit").childrenMap().forEach((key, value) -> generatorsItemLimit.put(GeneratorType.valueOf(String.valueOf(key).toUpperCase()), value.getInt()));
        node.node("generators").childrenMap().forEach((key, value) -> {
            try {
                generators.put(GeneratorType.valueOf(String.valueOf(key).toUpperCase()), value.getList(Pos.class));
            } catch (SerializationException e) {
                System.out.println("Could not import generators!");
            }
        });
        PlayerSkin itemShopSkin = null;
        PlayerSkin teamShopSkin = null;
        try {
            itemShopSkin = new PlayerSkin(node.node("npc_skins", "item_shop", "textures").getString(), node.node("npc_skins", "item_shop", "signature").getString());
            teamShopSkin = new PlayerSkin(node.node("npc_skins", "team_shop", "textures").getString(), node.node("npc_skins", "team_shop", "signature").getString());
        } catch (Exception e) {
            System.out.println("Could not import npc skins!");
        }
        System.out.printf("Finished importing bedwars config in %sms!", (System.currentTimeMillis() - time));
        return new Config(
                worldName,
                mapName,
                mode,
                minPlayers,
                maxPlayers,
                bridgeEggBlockLimit,
                bridgeEggBlockDespawn,
                spawnPlatformCenter,
                teams,
                generatorsWaitTimeTicks,
                generatorsItemLimit,
                generators,
                itemShopSkin,
                teamShopSkin
        );
    }
}
