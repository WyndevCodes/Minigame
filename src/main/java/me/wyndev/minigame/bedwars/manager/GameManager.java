package me.wyndev.minigame.bedwars.manager;

import lombok.Getter;
import lombok.Setter;
import me.wyndev.minigame.Main;
import me.wyndev.minigame.bedwars.ItemAbilityDispatcher;
import me.wyndev.minigame.bedwars.config.Config;
import me.wyndev.minigame.bedwars.data.gamecomponent.Scoreboard;
import me.wyndev.minigame.bedwars.data.gamecomponent.Team;
import me.wyndev.minigame.bedwars.data.stat.BedwarsStats;
import me.wyndev.minigame.bedwars.data.state.AxeLevel;
import me.wyndev.minigame.bedwars.data.state.GameState;
import me.wyndev.minigame.bedwars.data.state.PickaxeLevel;
import me.wyndev.minigame.bedwars.menu.shop.BlocksShopMenu;
import me.wyndev.minigame.bedwars.npc.NPC;
import me.wyndev.minigame.bedwars.player.BedwarsPlayer;
import me.wyndev.minigame.bedwars.runnable.GameRunnable;
import me.wyndev.minigame.bedwars.runnable.RespawnRunnable;
import me.wyndev.minigame.bedwars.runnable.WaitingRunnable;
import me.wyndev.minigame.bedwars.util.Items;
import me.wyndev.minigame.bedwars.util.Msg;
import me.wyndev.minigame.bot.PlayerBot;
import me.wyndev.minigame.player.MinigamePlayer;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.*;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.ItemStack;
import net.minestom.server.network.packet.server.play.TeamsPacket;
import net.minestom.server.registry.RegistryKey;
import net.minestom.server.scoreboard.TeamBuilder;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;

@Getter
@Setter
public class GameManager {
    private final Set<LivingEntity> queuedPlayers = new HashSet<>();
    private final Map<LivingEntity, BedwarsPlayer> activePlayers = new HashMap<>();
    private final List<Team> teams = new ArrayList<>();
    private final List<UUID> spectators = new ArrayList<>();
    private final List<NPC> npcList = new ArrayList<>();
    private final StatsManager statsManager;
    private final WorldManager worldManager;
    private final PlayerInventoryManager playerInventoryManager;
    private final GeneratorManager generatorManager;
    public boolean STARTED = false;
    private GameState beforeFrozen;
    private GameState gameState;
    private WaitingRunnable waitingRunnable;
    private GameRunnable gameRunnable;
    private final ItemAbilityDispatcher itemAbilityDispatcher;

    public GameManager(String mapName) {
        statsManager = new StatsManager();
        worldManager = new WorldManager("bedwars", mapName); //todo change
        playerInventoryManager = new PlayerInventoryManager();
        generatorManager = new GeneratorManager();
        itemAbilityDispatcher = new ItemAbilityDispatcher();
        Main.getSideboardManager().setSideboardCreator(new Scoreboard());
        Main.getSideboardManager().cancelUpdates();
        Main.getSideboardManager().autoUpdateBoards(TaskSchedule.tick(1));
    }

    public void queuePlayer(LivingEntity player) {
        if (!STARTED) {
            if (queuedPlayers.size() < getWorldConfig().maxPlayers) {
                queuedPlayers.add(player);
                player.setInstance(getGameWorld(), getWorldConfig().spawnPlatformCenter.add(0, 1, 0));
                if (waitingRunnable == null) {
                    if (queuedPlayers.size() >= getWorldConfig().minPlayers) {
                        setGameState(GameState.STARTING);
                        waitingRunnable = new WaitingRunnable();
                    }
                }
            } else if (player instanceof Player player1) {
                player1.sendMessage(Msg.red("Sorry, the game is full!"));
            }
        } else if (player instanceof Player player1) {
            player1.sendMessage(Msg.red("Sorry, the game has started!"));
        }
    }

    public boolean removePlayerFromQueue(LivingEntity player) {
        boolean removed = queuedPlayers.remove(player);
        if (queuedPlayers.size() < getWorldConfig().minPlayers) {
            if (waitingRunnable != null) {
                waitingRunnable.stop();
                waitingRunnable = null;
                setGameState(GameState.WAITING);
                queuedPlayers.forEach(p -> {
                    if (p instanceof Player pl) {
                        pl.sendMessage(Msg.redSplash("START CANCELLED!", "There are not enough players to start the game!"));
                    }
                });
            }
        }
        return removed;
    }

    public Set<LivingEntity> getBedwarsEntities() {
        return activePlayers.keySet();
    }

    public void forEachBedwarsEntity(Consumer<LivingEntity> toRun) {
        activePlayers.keySet().forEach(toRun);
    }

    public void forEachBedwarsMinigamePlayer(Consumer<MinigamePlayer> toRun) {
        activePlayers.keySet().stream().filter(e -> e instanceof MinigamePlayer).map(e -> (MinigamePlayer) e).forEach(toRun);
    }

    public boolean isPlayerInBedwars(LivingEntity livingEntity) {
        return activePlayers.containsKey(livingEntity) || queuedPlayers.contains(livingEntity);
    }

    @Nullable
    public BedwarsPlayer getBedwarsPlayerFor(LivingEntity livingEntity) {
        if (activePlayers.containsKey(livingEntity)) return activePlayers.get(livingEntity);
        return null;
    }

    public BedwarsPlayer getOrCreateBedwarsPlayerFor(LivingEntity livingEntity) {
        if (activePlayers.containsKey(livingEntity)) return activePlayers.get(livingEntity);
        BedwarsPlayer player = new BedwarsPlayer(livingEntity);
        if (livingEntity instanceof PlayerBot bot) bot.setBedwarsPlayer(player);
        activePlayers.put(livingEntity, player);
        return player;
    }

    public String getUsernameFor(LivingEntity livingEntity) {
        if (activePlayers.containsKey(livingEntity)) return activePlayers.get(livingEntity).getUsername();
        return livingEntity instanceof Player player ? player.getUsername() : (livingEntity instanceof PlayerBot bot ? bot.getUsername() : "Unknown");
    }

    public void removePlayer(LivingEntity player) {
        if (removePlayerFromQueue(player)) {
            forEachBedwarsMinigamePlayer(p -> p.sendMessage(Msg.grey("%s left the queue!", getUsernameFor(player))));
        }
        activePlayers.remove(player);
    }

    public InstanceContainer getGameWorld() {
        return worldManager.getGameWorld();
    }

    public Config getWorldConfig() {
        return worldManager.getWorldConfig();
    }

    public int countPlayers() {
        return this.isSTARTED() ? activePlayers.size() : queuedPlayers.size();
    }

    public void setup() {
        worldManager.loadWorld();
        gameState = GameState.WAITING;
    }

    public void freeze() {
        beforeFrozen = gameState;
        gameState = GameState.FROZEN;
    }

    public void thaw() {
        gameState = beforeFrozen;
        beforeFrozen = null;
    }

    public void start() {
        worldManager.removeSpawnPlatform();
        STARTED = true;
        setGameState(GameState.PLAY);
        activePlayers.forEach((entity, bedwarsPlayer) -> statsManager.addPlayer(entity.getUuid()));
        // split players into teams
        teams.addAll(splitPlayersIntoTeams(queuedPlayers.stream().toList()));

        teams.forEach(team -> team.getPlayerMap().forEach((living, player) -> {
            player.addToTeam(team.getMcTeam());
            if (living instanceof MinigamePlayer actualPlayer) {
                actualPlayer.setGameMode(GameMode.SURVIVAL);
                Main.getSideboardManager().addPlayer(actualPlayer);
            }
            living.teleport(team.getSpawnLocation());
            living.setItemInHand(PlayerHand.MAIN, Items.DEFAULT_SWORD);
            setEquipment(living, player);
        }));
        generatorManager.registerTeamGenerators();
        generatorManager.registerDiamondGenerators();
        generatorManager.registerEmeraldGenerators();

        gameRunnable = new GameRunnable();

        for (Team team : teams) {
            NPC itemShop = NPC.ofHumanoid(team.getItemShopLocation(), worldManager.getGameWorld())
                    .interactTrigger((npc, npcInteractType, player) -> new BlocksShopMenu().open(player))
                    .skin(getWorldConfig().itemShopSkin)
                    .lines(Msg.gold("<b>ITEM SHOP"))
                    .invulnerable()
                    .build();
            npcList.add(itemShop);

            NPC teamShop = NPC.ofHumanoid(team.getTeamShopLocation(), worldManager.getGameWorld())
                    .interactTrigger((npc, npcInteractType, player) -> player.sendMessage(Msg.red("Coming soon")))
                    .skin(getWorldConfig().teamShopSkin)
                    .lines(Msg.red("Coming soon"))
                    .invulnerable()
                    .build();
            npcList.add(teamShop);
        }
    }

    private void setEquipment(LivingEntity entity, BedwarsPlayer player) {
        entity.setEquipment(EquipmentSlot.CHESTPLATE, (Items.get(String.format("%s_CHEST", getPlayerTeam(player).orElseThrow().getColor().toString().toUpperCase()))));
        entity.setEquipment(EquipmentSlot.LEGGINGS, (Items.get(String.format(player.getArmorLevel().getLegsID(), getPlayerTeam(player).orElseThrow().getColor().toString().toUpperCase()))));
        entity.setEquipment(EquipmentSlot.BOOTS, Items.get(String.format(player.getArmorLevel().getBootsID(), getPlayerTeam(player).orElseThrow().getColor().toString().toUpperCase())));
    }

    private List<Team> splitPlayersIntoTeams(List<LivingEntity> players) {
        int numTeams = getWorldConfig().teams.size();
        int teamSize = players.size() / numTeams;
        int remainingPlayers = players.size() % numTeams;
        List<Team> result = new ArrayList<>();

        int playerIndex = 0;
        for (Team team : getWorldConfig().teams.values()) {
            net.minestom.server.scoreboard.Team mcTeam = new TeamBuilder(team.getDisplayName(), MinecraftServer.getTeamManager())
                    .collisionRule(TeamsPacket.CollisionRule.PUSH_OTHER_TEAMS)
                    .teamColor(team.getColor())
                    .prefix(Msg.mm(team.getPrefix()))
                    .build();
            mcTeam.setSeeInvisiblePlayers(true);
            mcTeam.setAllowFriendlyFire(false);
            Map<LivingEntity, BedwarsPlayer> teamPlayers = new HashMap<>();
            int currentTeamSize = teamSize + (remainingPlayers > 0 ? 1 : 0);
            for (int i = 0; i < currentTeamSize; i++) {
                if (playerIndex < players.size()) {
                    teamPlayers.put(players.get(playerIndex), getOrCreateBedwarsPlayerFor(players.get(playerIndex)));
                    if (players.get(playerIndex) instanceof PlayerBot bot) bot.setBedwarsTeam(team);
                    playerIndex++;
                }
            }
            team.setPlayerMap(teamPlayers);
            if (!team.getPlayers().isEmpty()) {
                team.setMcTeam(mcTeam);
                team.setBed(true);
                result.add(team);
            } else {
                worldManager.breakBed(team);
                worldManager.getGameWorld().setBlock(team.getChestLocation(), Block.AIR);
            }
            if (remainingPlayers > 0) {
                remainingPlayers--;
            }
        }

        return result;
    }

    public void end() {
        STARTED = false;
        setGameState(GameState.ENDED);
        gameRunnable.stop();
        gameRunnable = null;
        npcList.forEach((npc -> npc.getActions().clear()));
        generatorManager.removeGenerators();
        MinecraftServer.getSchedulerManager().buildTask(() -> {
            forEachBedwarsMinigamePlayer(MinigamePlayer::sendToLobby);
            cleanup();
        }).delay(Duration.ofSeconds(10)).schedule();
        Team winningTeam = teams.stream().filter(Team::isAlive).findFirst().orElseThrow();
        Collection<BedwarsPlayer> winners = winningTeam.getPlayers();
        forEachBedwarsMinigamePlayer(player -> {
            if (winners.stream().map(BedwarsPlayer::getUuid).toList().contains(player.getUuid())) {
                player.showTitle(Title.title(Msg.gold("<b>VICTORY!"), Msg.mm(""), Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(2), Duration.ofSeconds(1))));
            } else {
                player.showTitle(Title.title(Msg.red("<b>GAME OVER!"), Msg.mm(""), Title.Times.times(Duration.ofSeconds(1), Duration.ofSeconds(2), Duration.ofSeconds(1))));
            }
            player.sendMessage(Msg.mm(""));
            player.sendMessage(Msg.goldSplash("GAME OVER!", "<%s>%s <gray>has won the game!", winningTeam.getColor(), winningTeam.getDisplayName()));
            player.sendMessage(Msg.mm(""));
            BedwarsStats stats = statsManager.getStats(player.getUuid());
            if (stats == null) {
                player.sendMessage(Msg.whoops("You don't have any stats!"));
            } else {
                player.sendMessage(Msg.gold("<b>STATS:"));
                player.sendMessage(Msg.grey("   Kills: <white>%s", stats.getKills()));
                player.sendMessage(Msg.grey("   Final Kills: <white>%s", stats.getFinalKills()));
                player.sendMessage(Msg.grey("   Deaths: <white>%s", stats.getDeaths()));
                player.sendMessage(Msg.grey("   Beds broken: <white>%s", stats.getBedsBroken()));
                player.sendMessage(Msg.grey("   Damage Dealt: <white>%s", stats.getDamageDealt()));
                player.sendMessage(Msg.grey("   Damage Taken: <white>%s", stats.getDamageTaken()));
            }
        });
        queuedPlayers.clear();
        activePlayers.clear();
    }


    public void cleanup() {
        STARTED = false;
        setGameState(GameState.CLEANUP);
        worldManager.redoWorld();
        npcList.forEach((npc -> Main.getNpcManager().removeNPC(npc)));
        teams.clear();
        statsManager.getStats().clear();
        setup();
    }

    /**
     * Gets the players team if they are on one
     */
    public Optional<Team> getPlayerTeam(BedwarsPlayer player) {
        return getPlayerTeam(player.getUuid());
    }

    /**
     * Gets the players team if they are on one
     */
    public Optional<Team> getPlayerTeam(LivingEntity player) {
        return getPlayerTeam(player.getUuid());
    }

    public Optional<Team> getTeamFromColor(NamedTextColor color) {
        for (Team team : teams) {
            if (team.getColor().equals(color)) {
                return Optional.of(team);
            }
        }
        return Optional.empty();
    }

    /**
     * Gets the players team if they are on one
     */
    public Optional<Team> getPlayerTeam(UUID player) {
        for (Team team : teams) {
            for (Map.Entry<LivingEntity, BedwarsPlayer> entry : team.getPlayerMap().entrySet()) {
                if (entry.getKey().getUuid().equals(player) || entry.getValue().getUuid().equals(player))
                    return Optional.of(team);
            }
        }
        return Optional.empty();
    }

    public void breakBed(BedwarsPlayer player, Team team) {
        Component message = Msg.whiteSplash("<newline>BED DESTRUCTION!", "<%s>%s Bed<reset><gray> was destroyed by <%s>%s<reset><gray>!<newline>",
                team.getColor().toString(), team.getName(), getPlayerTeam(player).orElseThrow().getColor(), player.getUsername());
        forEachBedwarsMinigamePlayer(p -> {
            p.playSound(Sound.sound(SoundEvent.ENTITY_GENERIC_EXPLODE, Sound.Source.PLAYER, 1f, 100f));
            p.sendMessage(message);
        });
        for (Map.Entry<LivingEntity, BedwarsPlayer> entry : team.getPlayerMap().entrySet()) {
            Title title = Title.title(Msg.red("<b>BED DESTROYED!"), Msg.white("You will no longer respawn!"),
                    Title.Times.times(Ticks.duration(10L), Ticks.duration(100L), Ticks.duration(20L)));
            if (entry.getKey() instanceof Player p) {
                p.showTitle(title);
            }
        }
        // todo: display animations, messages, etc.
        statsManager.getStats(player.getUuid()).addBedBreak();
        team.setBed(false);
    }

    public void kill(@NotNull LivingEntity dead, @Nullable LivingEntity killer, @NotNull RegistryKey<DamageType> damageType) {
        Team deadTeam = getPlayerTeam(dead.getUuid()).orElseThrow();
        statsManager.getStats(dead.getUuid()).addDeath();

        BedwarsPlayer deadPlayer = getOrCreateBedwarsPlayerFor(dead);

        //degrade tools
        if (AxeLevel.getOrdered(deadPlayer.getAxeLevel(), -1) != null) {
            deadPlayer.setAxeLevel(AxeLevel.getOrdered(deadPlayer.getAxeLevel(), -1));
        }

        if (PickaxeLevel.getOrdered(deadPlayer.getPickaxeLevel(), -1) != null) {
            deadPlayer.setPickaxeLevel(PickaxeLevel.getOrdered(deadPlayer.getPickaxeLevel(), -1));
        }

        boolean finalKill = false;
        Component message = Msg.mm("%s%s<reset> ", deadTeam.getPrefix(), deadPlayer.getUsername());
        if (!deadTeam.hasBed()) {
            finalKill = true;
        }
        if (damageType.equals(DamageType.PLAYER_ATTACK)) {
            if (killer == null) {
                kill(dead, null, DamageType.OUT_OF_WORLD);
            } else {
                if (!finalKill) {
                    statsManager.getStats(killer.getUuid()).addKill();
                }
                message = message.append(Msg.grey("was slain by %s%s", getPlayerTeam(killer).orElseThrow().getPrefix(), getUsernameFor(killer)));

                int iron = 0;
                int gold = 0;
                int diamond = 0;
                int emerald = 0;
                if (dead instanceof Player deadMcPlayer) {
                    iron = playerInventoryManager.itemCount(deadMcPlayer, "IRON");
                    gold = playerInventoryManager.itemCount(deadMcPlayer, "GOLD");
                    diamond = playerInventoryManager.itemCount(deadMcPlayer, "DIAMOND");
                    emerald = playerInventoryManager.itemCount(deadMcPlayer, "EMERALD");
                } else if (dead instanceof PlayerBot bot) {
                    iron = bot.getBedwarsData().getIron();
                    gold = bot.getBedwarsData().getGold();
                    diamond = bot.getBedwarsData().getDiamonds();
                    emerald = bot.getBedwarsData().getEmeralds();
                }
                if (killer instanceof Player killerMcPlayer) {
                    killerMcPlayer.getInventory().addItemStack(Items.get("IRON").withAmount(iron));
                    killerMcPlayer.getInventory().addItemStack(Items.get("GOLD").withAmount(gold));
                    killerMcPlayer.getInventory().addItemStack(Items.get("DIAMOND").withAmount(diamond));
                    killerMcPlayer.getInventory().addItemStack(Items.get("EMERALD").withAmount(emerald));
                } else if (killer instanceof PlayerBot bot) {
                    bot.getBedwarsData().addIron(iron);
                    bot.getBedwarsData().addGold(gold);
                    bot.getBedwarsData().addDiamonds(diamond);
                    bot.getBedwarsData().addEmeralds(emerald);
                }
            }
        } else if (damageType.equals(DamageType.FALL)) {
            message = message.append(Msg.grey("has fallen to their death"));
        } else if (damageType.equals(DamageType.ON_FIRE)) {
            message = message.append(Msg.grey("was roasted like a turkey"));
        } else if (damageType.equals(DamageType.LAVA)) {
            message = message.append(Msg.grey("discovered lava is hot"));
        } else if (damageType.equals(DamageType.OUT_OF_WORLD)) {
            message = message.append(Msg.grey("fell into the abyss"));
        } else if (damageType.equals(DamageType.FREEZE)) {
            message = message.append(Msg.grey("turned into an ice cube"));
        } else if (damageType.equals(DamageType.DROWN)) {
            message = message.append(Msg.grey("forgot how to swim"));
        } else if (damageType.equals(DamageType.EXPLOSION)) {
            message = message.append(Msg.grey("went <red><b>BOOM!"));
        } else if (damageType.equals(DamageType.ARROW) || damageType.equals(DamageType.TRIDENT)) {
            message = message.append(Msg.grey("was remotely terminated"));
        } else {
            System.out.println("unknown damage type: " + damageType.key());
            message = message.append(Msg.grey("died under mysterious circumstances"));
        }

        if (dead instanceof PlayerBot bot) bot.resetBedwarsData();

        dead.teleport(getWorldConfig().spawnPlatformCenter);
        if (finalKill) {
            if (dead instanceof Player player) {
                player.showTitle(Title.title(Msg.red("<b>YOU DIED!"), Msg.yellow("You won't respawn"), Title.Times.times(Duration.ofMillis(100), Duration.ofMillis(2750), Duration.ofMillis(100))));
                player.setGameMode(GameMode.SPECTATOR);
            }
            message = message.append(Msg.red("<b> FINAL KILL!"));
            Component finalMessage = message;
            forEachBedwarsMinigamePlayer((player -> player.sendMessage(finalMessage)));
            deadPlayer.setAlive(false);
            if (deadTeam.getAlivePlayers().isEmpty()) {
                deadTeam.setAlive(false);
                forEachBedwarsMinigamePlayer(player -> {
                    player.sendMessage(Msg.mm(""));
                    player.sendMessage(Msg.whiteSplash("TEAM ELIMINATED!", "<%s>%s <red>has been eliminated!", deadTeam.getColor(), deadTeam.getDisplayName()));
                    player.sendMessage(Msg.mm(""));
                });
            }
            if (killer != null) {
                statsManager.getStats(killer.getUuid()).addFinalKill();
            }
            if (teams.stream().filter(Team::isAlive).count() == 1) {
                end();
            }
            return;
        }
        Component finalMessage = message;
        // respawn logic...
        deadPlayer.setRespawning(true);
        forEachBedwarsMinigamePlayer((player -> player.sendMessage(finalMessage)));
        if (dead instanceof Player player) {
            player.showTitle(Title.title(Msg.red("<b>You DIED!"), Msg.yellow("You will respawn soon"), Title.Times.times(Duration.ofMillis(100), Duration.ofMillis(2750), Duration.ofMillis(100))));
            player.setGameMode(GameMode.SPECTATOR);
            player.getInventory().setItemStack(0, ItemStack.AIR);
            player.getInventory().setItemStack(6, ItemStack.AIR);
            player.getInventory().setItemStack(8, ItemStack.AIR);
        }
        dead.setHealth(20);
        dead.setFireTicks(0); // reset fire
        new RespawnRunnable(6, dead, deadPlayer);
    }

    public void respawnPlayer(LivingEntity dead, BedwarsPlayer deadPlayer) {
        if (dead instanceof Player player) player.setGameMode(GameMode.SURVIVAL);
        dead.setInvulnerable(true);// make them invincible for 5 sec
        MinecraftServer.getSchedulerManager().buildTask(() -> dead.setInvulnerable(false)).delay(Duration.ofSeconds(5)).schedule();
        dead.setVelocity(Vec.ZERO);
        dead.teleport(getPlayerTeam(dead).orElseThrow().getSpawnLocation());

        dead.setItemInHand(PlayerHand.MAIN, Items.DEFAULT_SWORD);

        // set armor
        setEquipment(dead, deadPlayer);

        // set tools
        //todo: check for enchants / team upgrades
        if (dead instanceof Player player) {
            player.getInventory().addItemStack(Items.get(deadPlayer.getAxeLevel().getItemID()));
            player.getInventory().addItemStack(Items.get(deadPlayer.getPickaxeLevel().getItemID()));
            if (deadPlayer.hasShears()) {
                player.getInventory().addItemStack(Items.SHEARS);
            }
        }
        deadPlayer.setRespawning(false);
        deadPlayer.setAlive(true);
    }


    public GameState nextGameState() {
        gameState = gameState.getNext();
        switch (Objects.requireNonNull(gameState)) {
            case DIAMOND_2, DIAMOND_3 -> {
                generatorManager.increaseDiamondsSpawnSpeed(gameState == GameState.DIAMOND_2 ? 20 : 12);
                forEachBedwarsMinigamePlayer(player -> player.sendMessage(Msg.aquaSplash("GENERATORS", "Diamonds generators have upgraded to Tier %s!", gameState == GameState.DIAMOND_2 ? "II" : "III")));
            }
            case EMERALD_2, EMERALD_3 -> {
                generatorManager.increaseEmeraldsSpawnSpeed(gameState == GameState.EMERALD_2 ? 400 : 240);
                forEachBedwarsMinigamePlayer(player -> player.sendMessage(Msg.greenSplash("GENERATORS", "Emerald generators have upgraded to Tier %s!", gameState == GameState.DIAMOND_2 ? "II" : "III")));

            }
            case BED_DESTRUCTION -> {
                teams.stream().filter(Team::isAlive).forEach(team -> {
                    team.setBed(false);
                    worldManager.breakBed(team);
                });
                forEachBedwarsMinigamePlayer(player -> {
                    player.sendMessage(Msg.redSplash("BED DESTROY", "All beds have been destroyed!"));
                    player.sendMessage(Msg.yellow("You can no longer respawn!"));
                });
            }
            case SUDDEN_DEATH -> {
                //todo
                forEachBedwarsMinigamePlayer(player -> player.sendMessage(Msg.red("Wow ender dragons crazy so cool")));
            }
            case ENDED -> end();
        }
        return gameState;
    }
}
