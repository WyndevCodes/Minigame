package me.wyndev.minigame;

import io.github.togar2.pvp.MinestomPvP;
import io.github.togar2.pvp.feature.CombatFeatureSet;
import io.github.togar2.pvp.feature.CombatFeatures;
import me.wyndev.minigame.bot.PlayerBot;
import me.wyndev.minigame.command.StopCommand;
import me.wyndev.minigame.command.gamemode.GmcCommand;
import me.wyndev.minigame.command.gamemode.GmsCommand;
import me.wyndev.minigame.player.MinigamePlayer;
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

public class Main {

    public static RegistryKey<DimensionType> DIMENSION_TYPE;
    public static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    public static final String NAMESPACE = "minigame";

    private static File WORLD_FOLDER;

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

        //mojang auth (REMOVE IF NECESSARY FOR PROXY)
        MojangAuth.init();

        //load 1.8 PVP
        MinestomPvP.init(false, true);
        CombatFeatureSet featureSet = CombatFeatures.legacyVanilla();

        minecraftServer.start("0.0.0.0", 25565);
        MinecraftServer.getConnectionManager().setPlayerProvider(MinigamePlayer::new);

        InstanceContainer hub = createHubInstance();
        new PlayerBot().setInstance(hub, new Pos(0.5, 65, 0.5));

        MinecraftServer.getGlobalEventHandler().addListener(AsyncPlayerConfigurationEvent.class, event -> {
            event.setSpawningInstance(hub);
            event.getPlayer().setRespawnPoint(new Pos(0.5, 65, 0.5, 90, 0));
            event.getPlayer().setPermissionLevel(4);
        });
        MinecraftServer.getGlobalEventHandler().addChild(featureSet.createNode());

        //register commands
        registerCommands();
    }

    public static String getWorldFilePath(String worldFileName) {
        return WORLD_FOLDER.getPath() + File.separator + worldFileName;
    }

    private static void registerCommands() {
        MinecraftServer.getCommandManager().register(new StopCommand());
        MinecraftServer.getCommandManager().register(new GmcCommand());
        MinecraftServer.getCommandManager().register(new GmsCommand());
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

}
