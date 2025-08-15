package me.wyndev.minigame.bedwars.manager;

import lombok.Getter;
import me.wyndev.minigame.bedwars.config.Config;
import me.wyndev.minigame.bedwars.data.gamecomponent.Team;
import me.wyndev.minigame.bedwars.listener.*;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.BlockVec;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.anvil.AnvilLoader;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockFace;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static me.wyndev.minigame.Main.*;

public class WorldManager {

    private final EventNode<Event> node;
    private final List<BedwarsEvent<?>> eventList;
    private final String gameWorldName;
    private final String gameName;

    @Getter
    private Config worldConfig;

    @Getter
    private InstanceContainer gameWorld;
    
    public WorldManager(String gameName, String gameWorldName) {
        this.gameName = gameName;
        this.gameWorldName = gameWorldName;
        this.worldConfig = Config.importConfig(getGameMapData(gameName, gameWorldName));
        node = EventNode.all("bedwars-" + gameWorldName);
        MinecraftServer.getGlobalEventHandler().addChild(node);
        eventList = new ArrayList<>();
    }

    // util

    public void breakBed(Team team) {
        Block block = gameWorld.getBlock(team.getBedLocation());
        BlockVec blockPos = new BlockVec(team.getBedLocation());
        if (block.key().equals(team.getBedType().key())) {
            this.gameWorld.setBlock(blockPos, Block.AIR);
            BlockFace facing = BlockFace.valueOf(block.getProperty("facing").toUpperCase());
            if (block.getProperty("part").equals("head")) {
                facing = facing.getOppositeFace();
            }
            this.gameWorld.setBlock(blockPos.relative(facing), Block.AIR);
        }
    }

    public void loadWorld() {
        gameWorld = MinecraftServer.getInstanceManager().createInstanceContainer(DIMENSION_TYPE);
        gameWorld.setChunkLoader(new AnvilLoader(getWorldFilePath("maps" + File.separator + gameName + File.separator + gameWorldName)));
        this.gameWorld.setTimeRate(0);
        this.gameWorld.setTime(6000);

        //load chunks
        Pos center = worldConfig.spawnPlatformCenter;
        int centerX = center.chunkX();
        int centerZ = center.chunkZ();
        for (int x = centerX - 20; x <= centerX + 20; x++) {
            for (int z = centerZ - 20; z <= centerZ + 20; z++) {
                gameWorld.loadChunk(x, z);
            }
        }

        createSpawnPlatform();

        eventList.add(new ArmorEquipListener(gameWorld));
        eventList.add(new BlockBreakListener(gameWorld));
        eventList.add(new BlockPlaceListener(gameWorld));
        eventList.add(new DamageListener(gameWorld));
        eventList.add(new DeathListener(gameWorld));
        eventList.add(new DropItemListener(gameWorld));
        eventList.add(new ExhaustListener(gameWorld));
        eventList.add(new ExhaustListener(gameWorld));
        eventList.add(new GamemodeChangeListener(gameWorld));
        eventList.add(new InventoryClickListener(gameWorld));
        eventList.add(new MoveListener(gameWorld));
        eventList.add(new PickupItemListener(gameWorld));
        eventList.add(new PotionDrinkListener(gameWorld));
        eventList.add(new UseItemListener(gameWorld));

        eventList.forEach(node::addListener);
    }

    public void redoWorld() {
        eventList.forEach(node::removeListener);
        loadWorld();
        createSpawnPlatform();
    }

    public void createSpawnPlatform() {
        Pos loc = worldConfig.spawnPlatformCenter;
        int x = (int) loc.x();
        int y = (int) loc.y();
        int z = (int) loc.z();
        this.gameWorld.setBlock(x, y, z, Block.BEACON);
        this.gameWorld.setBlock(x + 1, y, z, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 2, y, z, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 3, y, z, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 4, y, z, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 5, y, z, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 1, y, z, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 2, y, z, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 3, y, z, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 4, y, z, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 5, y, z, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x, y, z + 1, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 1, y, z + 1, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 2, y, z + 1, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 3, y, z + 1, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 4, y, z + 1, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 5, y, z + 1, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 1, y, z + 1, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 2, y, z + 1, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 3, y, z + 1, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 4, y, z + 1, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 5, y, z + 1, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x, y, z - 1, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 1, y, z - 1, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 2, y, z - 1, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 3, y, z - 1, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 4, y, z - 1, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 5, y, z - 1, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 1, y, z - 1, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 2, y, z - 1, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 3, y, z - 1, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 4, y, z - 1, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 5, y, z - 1, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x, y, z - 2, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 1, y, z - 2, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 2, y, z - 2, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 3, y, z - 2, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 4, y, z - 2, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 1, y, z - 2, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 2, y, z - 2, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 3, y, z - 2, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 4, y, z - 2, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x, y, z - 3, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 1, y, z - 3, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 2, y, z - 3, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 3, y, z - 3, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 4, y, z - 3, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 1, y, z - 3, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 2, y, z - 3, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 3, y, z - 3, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 4, y, z - 3, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x, y, z + 2, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 1, y, z + 2, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 2, y, z + 2, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 3, y, z + 2, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 4, y, z + 2, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 1, y, z + 2, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 2, y, z + 2, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 3, y, z + 2, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 4, y, z + 2, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x, y, z + 3, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 1, y, z + 3, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 2, y, z + 3, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 3, y, z + 3, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 4, y, z + 3, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 1, y, z + 3, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 2, y, z + 3, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 3, y, z + 3, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 4, y, z + 3, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x, y, z - 4, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 1, y, z - 4, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 2, y, z - 4, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 3, y, z - 4, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 1, y, z - 4, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 2, y, z - 4, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 3, y, z - 4, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x, y, z + 4, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 1, y, z + 4, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 2, y, z + 4, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 3, y, z + 4, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 1, y, z + 4, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 2, y, z + 4, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 3, y, z + 4, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x, y, z - 5, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 1, y, z - 5, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 1, y, z - 5, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x, y, z + 5, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 1, y, z + 5, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x - 1, y, z + 5, Block.GRAY_STAINED_GLASS);
        this.gameWorld.setBlock(x + 1, y + 1, z - 5, Block.WHITE_STAINED_GLASS_PANE); // WALLS V LAYER 1
        this.gameWorld.setBlock(x + 1, y + 1, z - 4, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x + 2, y + 1, z - 4, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x + 3, y + 1, z - 4, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x + 3, y + 1, z - 3, Block.COBBLED_DEEPSLATE_WALL);
        this.gameWorld.setBlock(x + 4, y + 1, z - 3, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x + 4, y + 1, z - 2, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x + 4, y + 1, z - 1, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x + 5, y + 1, z - 1, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 1, y + 1, z - 5, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 1, y + 1, z - 4, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 2, y + 1, z - 4, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 3, y + 1, z - 4, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 3, y + 1, z - 3, Block.COBBLED_DEEPSLATE_WALL);
        this.gameWorld.setBlock(x - 4, y + 1, z - 3, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 4, y + 1, z - 2, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 4, y + 1, z - 1, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 5, y + 1, z - 1, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x + 1, y + 1, z + 5, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x + 1, y + 1, z + 4, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x + 2, y + 1, z + 4, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x + 3, y + 1, z + 4, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x + 3, y + 1, z + 3, Block.COBBLED_DEEPSLATE_WALL);
        this.gameWorld.setBlock(x + 4, y + 1, z + 3, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x + 4, y + 1, z + 2, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x + 4, y + 1, z + 1, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x + 5, y + 1, z + 1, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 1, y + 1, z + 5, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 1, y + 1, z + 4, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 2, y + 1, z + 4, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 3, y + 1, z + 4, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 3, y + 1, z + 3, Block.COBBLED_DEEPSLATE_WALL);
        this.gameWorld.setBlock(x - 4, y + 1, z + 3, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 4, y + 1, z + 2, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 4, y + 1, z + 1, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 5, y + 1, z + 1, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 5, y + 1, z, Block.WHITE_STAINED_GLASS_PANE, true);
        this.gameWorld.setBlock(x + 5, y + 1, z, Block.WHITE_STAINED_GLASS_PANE, true);
        this.gameWorld.setBlock(x, y + 1, z + 5, Block.WHITE_STAINED_GLASS_PANE, true);
        this.gameWorld.setBlock(x, y + 1, z - 5, Block.WHITE_STAINED_GLASS_PANE, true);
        this.gameWorld.setBlock(x + 1, y + 2, z - 5, Block.WHITE_STAINED_GLASS_PANE); // WALLS LAYER 2 V
        this.gameWorld.setBlock(x + 1, y + 2, z - 4, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x + 2, y + 2, z - 4, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x + 3, y + 2, z - 4, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x + 3, y + 2, z - 3, Block.COBBLED_DEEPSLATE_WALL);
        this.gameWorld.setBlock(x + 4, y + 2, z - 3, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x + 4, y + 2, z - 2, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x + 4, y + 2, z - 1, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x + 5, y + 2, z - 1, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 1, y + 2, z - 5, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 1, y + 2, z - 4, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 2, y + 2, z - 4, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 3, y + 2, z - 4, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 3, y + 2, z - 3, Block.COBBLED_DEEPSLATE_WALL);
        this.gameWorld.setBlock(x - 4, y + 2, z - 3, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 4, y + 2, z - 2, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 4, y + 2, z - 1, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 5, y + 2, z - 1, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x + 1, y + 2, z + 5, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x + 1, y + 2, z + 4, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x + 2, y + 2, z + 4, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x + 3, y + 2, z + 4, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x + 3, y + 2, z + 3, Block.COBBLED_DEEPSLATE_WALL);
        this.gameWorld.setBlock(x + 4, y + 2, z + 3, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x + 4, y + 2, z + 2, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x + 4, y + 2, z + 1, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x + 5, y + 2, z + 1, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 1, y + 2, z + 5, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 1, y + 2, z + 4, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 2, y + 2, z + 4, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 3, y + 2, z + 4, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 3, y + 2, z + 3, Block.COBBLED_DEEPSLATE_WALL);
        this.gameWorld.setBlock(x - 4, y + 2, z + 3, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 4, y + 2, z + 2, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 4, y + 2, z + 1, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 5, y + 2, z + 1, Block.WHITE_STAINED_GLASS_PANE);
        this.gameWorld.setBlock(x - 5, y + 2, z, Block.WHITE_STAINED_GLASS_PANE, true);
        this.gameWorld.setBlock(x + 5, y + 2, z, Block.WHITE_STAINED_GLASS_PANE, true);
        this.gameWorld.setBlock(x, y + 2, z + 5, Block.WHITE_STAINED_GLASS_PANE, true);
        this.gameWorld.setBlock(x, y + 2, z - 5, Block.WHITE_STAINED_GLASS_PANE, true);
        this.gameWorld.setBlock(x - 3, y + 3, z + 2, Block.DARK_PRISMARINE_SLAB);// ROOF V
        this.gameWorld.setBlock(x - 3, y + 3, z + 1, Block.DARK_PRISMARINE_SLAB);
        this.gameWorld.setBlock(x - 2, y + 3, z + 3, Block.DARK_PRISMARINE_SLAB);
        this.gameWorld.setBlock(x - 1, y + 3, z + 3, Block.DARK_PRISMARINE_SLAB);
        this.gameWorld.setBlock(x - 3, y + 3, z - 2, Block.DARK_PRISMARINE_SLAB);
        this.gameWorld.setBlock(x - 3, y + 3, z - 1, Block.DARK_PRISMARINE_SLAB);
        this.gameWorld.setBlock(x - 2, y + 3, z - 3, Block.DARK_PRISMARINE_SLAB);
        this.gameWorld.setBlock(x - 1, y + 3, z - 3, Block.DARK_PRISMARINE_SLAB);
        this.gameWorld.setBlock(x + 3, y + 3, z + 2, Block.DARK_PRISMARINE_SLAB);
        this.gameWorld.setBlock(x + 3, y + 3, z + 1, Block.DARK_PRISMARINE_SLAB);
        this.gameWorld.setBlock(x + 2, y + 3, z + 3, Block.DARK_PRISMARINE_SLAB);
        this.gameWorld.setBlock(x + 1, y + 3, z + 3, Block.DARK_PRISMARINE_SLAB);
        this.gameWorld.setBlock(x + 3, y + 3, z - 2, Block.DARK_PRISMARINE_SLAB);
        this.gameWorld.setBlock(x + 3, y + 3, z - 1, Block.DARK_PRISMARINE_SLAB);
        this.gameWorld.setBlock(x + 2, y + 3, z - 3, Block.DARK_PRISMARINE_SLAB);
        this.gameWorld.setBlock(x + 1, y + 3, z - 3, Block.DARK_PRISMARINE_SLAB);
        this.gameWorld.setBlock(x + 4, y + 3, z, Block.DARK_PRISMARINE_SLAB);
        this.gameWorld.setBlock(x - 4, y + 3, z, Block.DARK_PRISMARINE_SLAB);
        this.gameWorld.setBlock(x, y + 3, z + 4, Block.DARK_PRISMARINE_SLAB);
        this.gameWorld.setBlock(x, y + 3, z - 4, Block.DARK_PRISMARINE_SLAB);
    }

    //fixme use a for loop or recursion to simplify
    public void removeSpawnPlatform() {
        Pos loc = worldConfig.spawnPlatformCenter;
        int x = (int) loc.x();
        int y = (int) loc.y();
        int z = (int) loc.z();
        this.gameWorld.setBlock(x, y, z, Block.AIR);
        this.gameWorld.setBlock(x + 1, y, z, Block.AIR);
        this.gameWorld.setBlock(x + 2, y, z, Block.AIR);
        this.gameWorld.setBlock(x + 3, y, z, Block.AIR);
        this.gameWorld.setBlock(x + 4, y, z, Block.AIR);
        this.gameWorld.setBlock(x + 5, y, z, Block.AIR);
        this.gameWorld.setBlock(x - 1, y, z, Block.AIR);
        this.gameWorld.setBlock(x - 2, y, z, Block.AIR);
        this.gameWorld.setBlock(x - 3, y, z, Block.AIR);
        this.gameWorld.setBlock(x - 4, y, z, Block.AIR);
        this.gameWorld.setBlock(x - 5, y, z, Block.AIR);
        this.gameWorld.setBlock(x, y, z + 1, Block.AIR);
        this.gameWorld.setBlock(x + 1, y, z + 1, Block.AIR);
        this.gameWorld.setBlock(x + 2, y, z + 1, Block.AIR);
        this.gameWorld.setBlock(x + 3, y, z + 1, Block.AIR);
        this.gameWorld.setBlock(x + 4, y, z + 1, Block.AIR);
        this.gameWorld.setBlock(x + 5, y, z + 1, Block.AIR);
        this.gameWorld.setBlock(x - 1, y, z + 1, Block.AIR);
        this.gameWorld.setBlock(x - 2, y, z + 1, Block.AIR);
        this.gameWorld.setBlock(x - 3, y, z + 1, Block.AIR);
        this.gameWorld.setBlock(x - 4, y, z + 1, Block.AIR);
        this.gameWorld.setBlock(x - 5, y, z + 1, Block.AIR);
        this.gameWorld.setBlock(x, y, z - 1, Block.AIR);
        this.gameWorld.setBlock(x + 1, y, z - 1, Block.AIR);
        this.gameWorld.setBlock(x + 2, y, z - 1, Block.AIR);
        this.gameWorld.setBlock(x + 3, y, z - 1, Block.AIR);
        this.gameWorld.setBlock(x + 4, y, z - 1, Block.AIR);
        this.gameWorld.setBlock(x + 5, y, z - 1, Block.AIR);
        this.gameWorld.setBlock(x - 1, y, z - 1, Block.AIR);
        this.gameWorld.setBlock(x - 2, y, z - 1, Block.AIR);
        this.gameWorld.setBlock(x - 3, y, z - 1, Block.AIR);
        this.gameWorld.setBlock(x - 4, y, z - 1, Block.AIR);
        this.gameWorld.setBlock(x - 5, y, z - 1, Block.AIR);
        this.gameWorld.setBlock(x, y, z - 2, Block.AIR);
        this.gameWorld.setBlock(x + 1, y, z - 2, Block.AIR);
        this.gameWorld.setBlock(x + 2, y, z - 2, Block.AIR);
        this.gameWorld.setBlock(x + 3, y, z - 2, Block.AIR);
        this.gameWorld.setBlock(x + 4, y, z - 2, Block.AIR);
        this.gameWorld.setBlock(x - 1, y, z - 2, Block.AIR);
        this.gameWorld.setBlock(x - 2, y, z - 2, Block.AIR);
        this.gameWorld.setBlock(x - 3, y, z - 2, Block.AIR);
        this.gameWorld.setBlock(x - 4, y, z - 2, Block.AIR);
        this.gameWorld.setBlock(x, y, z - 3, Block.AIR);
        this.gameWorld.setBlock(x + 1, y, z - 3, Block.AIR);
        this.gameWorld.setBlock(x + 2, y, z - 3, Block.AIR);
        this.gameWorld.setBlock(x + 3, y, z - 3, Block.AIR);
        this.gameWorld.setBlock(x + 4, y, z - 3, Block.AIR);
        this.gameWorld.setBlock(x - 1, y, z - 3, Block.AIR);
        this.gameWorld.setBlock(x - 2, y, z - 3, Block.AIR);
        this.gameWorld.setBlock(x - 3, y, z - 3, Block.AIR);
        this.gameWorld.setBlock(x - 4, y, z - 3, Block.AIR);
        this.gameWorld.setBlock(x, y, z + 2, Block.AIR);
        this.gameWorld.setBlock(x + 1, y, z + 2, Block.AIR);
        this.gameWorld.setBlock(x + 2, y, z + 2, Block.AIR);
        this.gameWorld.setBlock(x + 3, y, z + 2, Block.AIR);
        this.gameWorld.setBlock(x + 4, y, z + 2, Block.AIR);
        this.gameWorld.setBlock(x - 1, y, z + 2, Block.AIR);
        this.gameWorld.setBlock(x - 2, y, z + 2, Block.AIR);
        this.gameWorld.setBlock(x - 3, y, z + 2, Block.AIR);
        this.gameWorld.setBlock(x - 4, y, z + 2, Block.AIR);
        this.gameWorld.setBlock(x, y, z + 3, Block.AIR);
        this.gameWorld.setBlock(x + 1, y, z + 3, Block.AIR);
        this.gameWorld.setBlock(x + 2, y, z + 3, Block.AIR);
        this.gameWorld.setBlock(x + 3, y, z + 3, Block.AIR);
        this.gameWorld.setBlock(x + 4, y, z + 3, Block.AIR);
        this.gameWorld.setBlock(x - 1, y, z + 3, Block.AIR);
        this.gameWorld.setBlock(x - 2, y, z + 3, Block.AIR);
        this.gameWorld.setBlock(x - 3, y, z + 3, Block.AIR);
        this.gameWorld.setBlock(x - 4, y, z + 3, Block.AIR);
        this.gameWorld.setBlock(x, y, z - 4, Block.AIR);
        this.gameWorld.setBlock(x + 1, y, z - 4, Block.AIR);
        this.gameWorld.setBlock(x + 2, y, z - 4, Block.AIR);
        this.gameWorld.setBlock(x + 3, y, z - 4, Block.AIR);
        this.gameWorld.setBlock(x - 1, y, z - 4, Block.AIR);
        this.gameWorld.setBlock(x - 2, y, z - 4, Block.AIR);
        this.gameWorld.setBlock(x - 3, y, z - 4, Block.AIR);
        this.gameWorld.setBlock(x, y, z + 4, Block.AIR);
        this.gameWorld.setBlock(x + 1, y, z + 4, Block.AIR);
        this.gameWorld.setBlock(x + 2, y, z + 4, Block.AIR);
        this.gameWorld.setBlock(x + 3, y, z + 4, Block.AIR);
        this.gameWorld.setBlock(x - 1, y, z + 4, Block.AIR);
        this.gameWorld.setBlock(x - 2, y, z + 4, Block.AIR);
        this.gameWorld.setBlock(x - 3, y, z + 4, Block.AIR);
        this.gameWorld.setBlock(x, y, z - 5, Block.AIR);
        this.gameWorld.setBlock(x + 1, y, z - 5, Block.AIR);
        this.gameWorld.setBlock(x - 1, y, z - 5, Block.AIR);
        this.gameWorld.setBlock(x, y, z + 5, Block.AIR);
        this.gameWorld.setBlock(x + 1, y, z + 5, Block.AIR);
        this.gameWorld.setBlock(x - 1, y, z + 5, Block.AIR);
        this.gameWorld.setBlock(x + 1, y + 1, z - 5, Block.AIR); // WALLS V LAYER 1
        this.gameWorld.setBlock(x + 1, y + 1, z - 4, Block.AIR);
        this.gameWorld.setBlock(x + 2, y + 1, z - 4, Block.AIR);
        this.gameWorld.setBlock(x + 3, y + 1, z - 4, Block.AIR);
        this.gameWorld.setBlock(x + 3, y + 1, z - 3, Block.AIR);
        this.gameWorld.setBlock(x + 4, y + 1, z - 3, Block.AIR);
        this.gameWorld.setBlock(x + 4, y + 1, z - 2, Block.AIR);
        this.gameWorld.setBlock(x + 4, y + 1, z - 1, Block.AIR);
        this.gameWorld.setBlock(x + 5, y + 1, z - 1, Block.AIR);
        this.gameWorld.setBlock(x - 1, y + 1, z - 5, Block.AIR);
        this.gameWorld.setBlock(x - 1, y + 1, z - 4, Block.AIR);
        this.gameWorld.setBlock(x - 2, y + 1, z - 4, Block.AIR);
        this.gameWorld.setBlock(x - 3, y + 1, z - 4, Block.AIR);
        this.gameWorld.setBlock(x - 3, y + 1, z - 3, Block.AIR);
        this.gameWorld.setBlock(x - 4, y + 1, z - 3, Block.AIR);
        this.gameWorld.setBlock(x - 4, y + 1, z - 2, Block.AIR);
        this.gameWorld.setBlock(x - 4, y + 1, z - 1, Block.AIR);
        this.gameWorld.setBlock(x - 5, y + 1, z - 1, Block.AIR);
        this.gameWorld.setBlock(x + 1, y + 1, z + 5, Block.AIR);
        this.gameWorld.setBlock(x + 1, y + 1, z + 4, Block.AIR);
        this.gameWorld.setBlock(x + 2, y + 1, z + 4, Block.AIR);
        this.gameWorld.setBlock(x + 3, y + 1, z + 4, Block.AIR);
        this.gameWorld.setBlock(x + 3, y + 1, z + 3, Block.AIR);
        this.gameWorld.setBlock(x + 4, y + 1, z + 3, Block.AIR);
        this.gameWorld.setBlock(x + 4, y + 1, z + 2, Block.AIR);
        this.gameWorld.setBlock(x + 4, y + 1, z + 1, Block.AIR);
        this.gameWorld.setBlock(x + 5, y + 1, z + 1, Block.AIR);
        this.gameWorld.setBlock(x - 1, y + 1, z + 5, Block.AIR);
        this.gameWorld.setBlock(x - 1, y + 1, z + 4, Block.AIR);
        this.gameWorld.setBlock(x - 2, y + 1, z + 4, Block.AIR);
        this.gameWorld.setBlock(x - 3, y + 1, z + 4, Block.AIR);
        this.gameWorld.setBlock(x - 3, y + 1, z + 3, Block.AIR);
        this.gameWorld.setBlock(x - 4, y + 1, z + 3, Block.AIR);
        this.gameWorld.setBlock(x - 4, y + 1, z + 2, Block.AIR);
        this.gameWorld.setBlock(x - 4, y + 1, z + 1, Block.AIR);
        this.gameWorld.setBlock(x - 5, y + 1, z + 1, Block.AIR);
        this.gameWorld.setBlock(x - 5, y + 1, z, Block.AIR, true);
        this.gameWorld.setBlock(x + 5, y + 1, z, Block.AIR, true);
        this.gameWorld.setBlock(x, y + 1, z + 5, Block.AIR, true);
        this.gameWorld.setBlock(x, y + 1, z - 5, Block.AIR, true);
        this.gameWorld.setBlock(x + 1, y + 2, z - 5, Block.AIR); // WALLS LAYER 2 V
        this.gameWorld.setBlock(x + 1, y + 2, z - 4, Block.AIR);
        this.gameWorld.setBlock(x + 2, y + 2, z - 4, Block.AIR);
        this.gameWorld.setBlock(x + 3, y + 2, z - 4, Block.AIR);
        this.gameWorld.setBlock(x + 3, y + 2, z - 3, Block.AIR);
        this.gameWorld.setBlock(x + 4, y + 2, z - 3, Block.AIR);
        this.gameWorld.setBlock(x + 4, y + 2, z - 2, Block.AIR);
        this.gameWorld.setBlock(x + 4, y + 2, z - 1, Block.AIR);
        this.gameWorld.setBlock(x + 5, y + 2, z - 1, Block.AIR);
        this.gameWorld.setBlock(x - 1, y + 2, z - 5, Block.AIR);
        this.gameWorld.setBlock(x - 1, y + 2, z - 4, Block.AIR);
        this.gameWorld.setBlock(x - 2, y + 2, z - 4, Block.AIR);
        this.gameWorld.setBlock(x - 3, y + 2, z - 4, Block.AIR);
        this.gameWorld.setBlock(x - 3, y + 2, z - 3, Block.AIR);
        this.gameWorld.setBlock(x - 4, y + 2, z - 3, Block.AIR);
        this.gameWorld.setBlock(x - 4, y + 2, z - 2, Block.AIR);
        this.gameWorld.setBlock(x - 4, y + 2, z - 1, Block.AIR);
        this.gameWorld.setBlock(x - 5, y + 2, z - 1, Block.AIR);
        this.gameWorld.setBlock(x + 1, y + 2, z + 5, Block.AIR);
        this.gameWorld.setBlock(x + 1, y + 2, z + 4, Block.AIR);
        this.gameWorld.setBlock(x + 2, y + 2, z + 4, Block.AIR);
        this.gameWorld.setBlock(x + 3, y + 2, z + 4, Block.AIR);
        this.gameWorld.setBlock(x + 3, y + 2, z + 3, Block.AIR);
        this.gameWorld.setBlock(x + 4, y + 2, z + 3, Block.AIR);
        this.gameWorld.setBlock(x + 4, y + 2, z + 2, Block.AIR);
        this.gameWorld.setBlock(x + 4, y + 2, z + 1, Block.AIR);
        this.gameWorld.setBlock(x + 5, y + 2, z + 1, Block.AIR);
        this.gameWorld.setBlock(x - 1, y + 2, z + 5, Block.AIR);
        this.gameWorld.setBlock(x - 1, y + 2, z + 4, Block.AIR);
        this.gameWorld.setBlock(x - 2, y + 2, z + 4, Block.AIR);
        this.gameWorld.setBlock(x - 3, y + 2, z + 4, Block.AIR);
        this.gameWorld.setBlock(x - 3, y + 2, z + 3, Block.AIR);
        this.gameWorld.setBlock(x - 4, y + 2, z + 3, Block.AIR);
        this.gameWorld.setBlock(x - 4, y + 2, z + 2, Block.AIR);
        this.gameWorld.setBlock(x - 4, y + 2, z + 1, Block.AIR);
        this.gameWorld.setBlock(x - 5, y + 2, z + 1, Block.AIR);
        this.gameWorld.setBlock(x - 5, y + 2, z, Block.AIR, true);
        this.gameWorld.setBlock(x + 5, y + 2, z, Block.AIR, true);
        this.gameWorld.setBlock(x, y + 2, z + 5, Block.AIR, true);
        this.gameWorld.setBlock(x, y + 2, z - 5, Block.AIR, true);
        this.gameWorld.setBlock(x - 3, y + 3, z + 2, Block.AIR);// ROOF V
        this.gameWorld.setBlock(x - 3, y + 3, z + 1, Block.AIR);
        this.gameWorld.setBlock(x - 2, y + 3, z + 3, Block.AIR);
        this.gameWorld.setBlock(x - 1, y + 3, z + 3, Block.AIR);
        this.gameWorld.setBlock(x - 3, y + 3, z - 2, Block.AIR);
        this.gameWorld.setBlock(x - 3, y + 3, z - 1, Block.AIR);
        this.gameWorld.setBlock(x - 2, y + 3, z - 3, Block.AIR);
        this.gameWorld.setBlock(x - 1, y + 3, z - 3, Block.AIR);
        this.gameWorld.setBlock(x + 3, y + 3, z + 2, Block.AIR);
        this.gameWorld.setBlock(x + 3, y + 3, z + 1, Block.AIR);
        this.gameWorld.setBlock(x + 2, y + 3, z + 3, Block.AIR);
        this.gameWorld.setBlock(x + 1, y + 3, z + 3, Block.AIR);
        this.gameWorld.setBlock(x + 3, y + 3, z - 2, Block.AIR);
        this.gameWorld.setBlock(x + 3, y + 3, z - 1, Block.AIR);
        this.gameWorld.setBlock(x + 2, y + 3, z - 3, Block.AIR);
        this.gameWorld.setBlock(x + 1, y + 3, z - 3, Block.AIR);
        this.gameWorld.setBlock(x + 4, y + 3, z, Block.AIR);
        this.gameWorld.setBlock(x - 4, y + 3, z, Block.AIR);
        this.gameWorld.setBlock(x, y + 3, z + 4, Block.AIR);
        this.gameWorld.setBlock(x, y + 3, z - 4, Block.AIR);
    }
}
