package me.wyndev.minigame;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import eu.koboo.minestom.stomui.api.ViewRegistry;
import eu.koboo.minestom.stomui.core.MinestomUI;
import io.github.togar2.pvp.MinestomPvP;
import io.github.togar2.pvp.feature.CombatFeatureSet;
import io.github.togar2.pvp.feature.CombatFeatures;
import lombok.Getter;
import me.wyndev.minigame.bedwars.blockhandler.ChestBlockHandler;
import me.wyndev.minigame.bedwars.blockhandler.EnderChestBlockHandler;
import me.wyndev.minigame.bedwars.command.DebugCommand;
import me.wyndev.minigame.bedwars.command.ItemCommand;
import me.wyndev.minigame.bedwars.config.Config;
import me.wyndev.minigame.bedwars.manager.GameManager;
import me.wyndev.minigame.bedwars.manager.NPCManager;
import me.wyndev.minigame.bot.PlayerBot;
import me.wyndev.minigame.command.PlayCommand;
import me.wyndev.minigame.command.StopCommand;
import me.wyndev.minigame.command.gamemode.GmcCommand;
import me.wyndev.minigame.command.gamemode.GmsCommand;
import me.wyndev.minigame.command.LobbyCommand;
import me.wyndev.minigame.player.MinigamePlayer;
import me.wyndev.minigame.sideboard.SideboardManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.world.DimensionType;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class Main {

    public static RegistryKey<DimensionType> DIMENSION_TYPE;
    public static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    public static final String NAMESPACE = "minigame";

    private static File WORLD_FOLDER;

    public static final ViewRegistry VIEW_REGISTRY = MinestomUI.create();

    @Getter
    private static GameManager gameManager;
    @Getter
    private static NPCManager npcManager;
    @Getter
    private static SideboardManager sideboardManager;
    @Getter
    private static InstanceContainer hub;
    @Getter
    private static final Pos hubSpawn = new Pos(0.5, 65, 0.5, 90, 0);

    public static void main(String[] args) {
        WORLD_FOLDER = new File("worlds");
        if (!WORLD_FOLDER.exists() || !WORLD_FOLDER.isDirectory()) WORLD_FOLDER.mkdir();

        //Initialize the Minestom server
        MinecraftServer minecraftServer = MinecraftServer.init();

        //register full bright dimension
        DIMENSION_TYPE = MinecraftServer.getDimensionTypeRegistry().register(
                Key.key(NAMESPACE + ":full_bright"),
                DimensionType.builder().ambientLight(2.0f).build()
        );

        //register initial managers
        npcManager = new NPCManager();
        sideboardManager = new SideboardManager();

        //mojang auth (REMOVE IF NECESSARY FOR PROXY)
        MojangAuth.init();

        //load 1.8 PVP
        MinestomPvP.init(false, true);
        CombatFeatureSet featureSet = CombatFeatures.legacyVanilla();

        minecraftServer.start("0.0.0.0", 25565);
        MinecraftServer.getConnectionManager().setPlayerProvider(MinigamePlayer::new);

        hub = createHubInstance();

        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, event -> {
            event.setSpawningInstance(hub);
            event.getPlayer().setRespawnPoint(hubSpawn);
            event.getPlayer().setPermissionLevel(4);
        });
        MinecraftServer.getGlobalEventHandler().addChild(featureSet.createNode());

        //register commands
        registerCommands();

        //load bedwars
        loadBedwars();

        //create some random bots to walk around the hub (test non-game movement)
        for (int i = 0; i < 3; i++) {
            new PlayerBot().setInstance(hub, hubSpawn);
        }

        //load a bot and have it join bedwars
        PlayerBot bedwarsBot = new PlayerBot();
        bedwarsBot.setInstance(hub, hubSpawn);
        Main.getGameManager().queuePlayer(bedwarsBot);
    }

    public static void shutdown() {
        gameManager.cleanup();
    }

    public static String getWorldFilePath(String worldFileName) {
        return WORLD_FOLDER.getPath() + File.separator + worldFileName;
    }

    public static String getGameMapData(String gameName, String mapName) {
        String path = WORLD_FOLDER.getPath() + File.separator + "maps" + File.separator + gameName + File.separator + mapName + File.separator + "data.json";
        try (FileReader reader = new FileReader(path)) {
            JsonElement element = JsonParser.parseReader(reader);
            return element.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void registerCommands() {
        MinecraftServer.getCommandManager().register(new StopCommand());
        MinecraftServer.getCommandManager().register(new GmcCommand());
        MinecraftServer.getCommandManager().register(new GmsCommand());
        MinecraftServer.getCommandManager().register(new LobbyCommand());
        MinecraftServer.getCommandManager().register(new PlayCommand());
    }

    private static InstanceContainer createHubInstance() {
        InstanceContainer hub = MinecraftServer.getInstanceManager().createInstanceContainer(DIMENSION_TYPE);
        hub.setChunkLoader(new AnvilLoader(getWorldFilePath("hub")));

        hub.enableAutoChunkLoad(false);
        for (int x = -10; x <= 10; x++) {
            for (int z = -10; z <= 10; z++) {
                hub.loadChunk(x, z);
            }
        }

        return hub;
    }

    private static void loadBedwars() {
        MinecraftServer.getBlockManager().registerHandler(Key.key("minecraft:ender_chest"), EnderChestBlockHandler::new);
        MinecraftServer.getBlockManager().registerHandler(Key.key("minecraft:chest"), ChestBlockHandler::new);
        gameManager = new GameManager("test");
        gameManager.setup();
        MinecraftServer.getCommandManager().register(new DebugCommand());
        MinecraftServer.getCommandManager().register(new ItemCommand());
    }

}
