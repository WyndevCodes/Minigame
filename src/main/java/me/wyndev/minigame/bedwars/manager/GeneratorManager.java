package me.wyndev.minigame.bedwars.manager;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.wyndev.minigame.Main;
import me.wyndev.minigame.bedwars.data.gamecomponent.Generator;
import me.wyndev.minigame.bedwars.data.gamecomponent.Team;
import me.wyndev.minigame.bedwars.data.state.GeneratorType;
import net.minestom.server.coordinate.Pos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class GeneratorManager {
    private final Map<Team, Generator> ironGenerators = new HashMap<>();
    private final Map<Team, Generator> goldGenerators = new HashMap<>();
    private final List<Generator> diamondGenerators = new ArrayList<>();
    private final List<Generator> emeraldGenerators = new ArrayList<>();

    public void registerTeamGenerators() {
        for (Team t : Main.getGameManager().getTeams()) {
            Pos loc = t.getGeneratorLocation();
            Generator ironGenerator = new Generator(
                    GeneratorType.IRON,
                    Main.getGameManager().getWorldConfig().generatorsWaitTimeTicks.get(GeneratorType.IRON), Main.getGameManager().getWorldConfig().generatorsItemLimit.get(GeneratorType.IRON),
                    loc,
                    false,
                    true
            );
            ironGenerators.put(t, ironGenerator);
            Generator goldGenerator = new Generator(
                    GeneratorType.GOLD,
                    Main.getGameManager().getWorldConfig().generatorsWaitTimeTicks.get(GeneratorType.GOLD), Main.getGameManager().getWorldConfig().generatorsItemLimit.get(GeneratorType.GOLD),
                    loc,
                    false,
                    true
            );
            goldGenerators.put(t, goldGenerator);
            ironGenerator.start();
            goldGenerator.start();
        }
    }

    public void registerDiamondGenerators() {
        List<Pos> diamondGeneratorsPositions = Main.getGameManager().getWorldConfig().generators.get(GeneratorType.DIAMOND);
        for (Pos diamondGeneratorsPosition : diamondGeneratorsPositions) {
            Generator generator = new Generator(
                    GeneratorType.DIAMOND,
                    Main.getGameManager().getWorldConfig().generatorsWaitTimeTicks.get(GeneratorType.DIAMOND), Main.getGameManager().getWorldConfig().generatorsItemLimit.get(GeneratorType.DIAMOND),
                    diamondGeneratorsPosition,
                    true,
                    false
            );
            diamondGenerators.add(generator);
            generator.start();
        }
    }

    public void increaseDiamondsSpawnSpeed(int time) {
        for (Generator diamondGenerator : diamondGenerators) {
            diamondGenerator.stop();
            diamondGenerator.setWaitTime(time);
            diamondGenerator.start();
        }
    }

    public void registerEmeraldGenerators() {
        List<Pos> posList = Main.getGameManager().getWorldConfig().generators.get(GeneratorType.EMERALD);
        for (Pos pos : posList) {
            Generator generator = new Generator(
                    GeneratorType.EMERALD,
                    Main.getGameManager().getWorldConfig().generatorsWaitTimeTicks.get(GeneratorType.EMERALD), Main.getGameManager().getWorldConfig().generatorsItemLimit.get(GeneratorType.EMERALD),
                    pos,
                    true,
                    false
            );
            emeraldGenerators.add(generator);
            generator.start();
        }
    }

    public void increaseEmeraldsSpawnSpeed(int time) {
        for (Generator emeraldGenerator : emeraldGenerators) {
            emeraldGenerator.stop();
            emeraldGenerator.setWaitTime(time);
            emeraldGenerator.start();
        }
    }

    public void removeGenerators() {
        for (Generator ironGenerator : ironGenerators.values()) {
            ironGenerator.stop();
        }
        for (Generator goldGenerator : goldGenerators.values()) {
            goldGenerator.stop();
        }
        for (Generator diamondGenerator : diamondGenerators) {
            diamondGenerator.stop();
        }
        for (Generator emeraldGenerator : emeraldGenerators) {
            emeraldGenerator.stop();
        }
        ironGenerators.clear();
        goldGenerators.clear();
        diamondGenerators.clear();
        emeraldGenerators.clear();
    }
}
