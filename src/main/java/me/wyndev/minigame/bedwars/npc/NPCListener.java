package me.wyndev.minigame.bedwars.npc;

import me.wyndev.minigame.Main;
import me.wyndev.minigame.player.MinigamePlayer;
import net.minestom.server.entity.PlayerHand;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.event.player.PlayerEntityInteractEvent;

import java.util.Optional;

public class NPCListener {

    public void onInteract(PlayerEntityInteractEvent event) {
        Optional<NPC> optional = Main.getNpcManager().findNPC(event.getTarget().getUuid());
        if (optional.isPresent() && optional.get() == event.getTarget() && event.getHand() == PlayerHand.MAIN) {
            NPC npc = optional.get();
            npc.getActions().forEach((action) -> action.execute(npc, NPCInteractType.INTERACT, event.getPlayer()));
        }
    }

    public void onAttack(EntityAttackEvent event) {
        if (!(event.getEntity() instanceof MinigamePlayer player)) return;
        Optional<NPC> optional = Main.getNpcManager().findNPC(event.getTarget().getUuid());
        if (optional.isPresent() && optional.get() == event.getTarget()) {
            NPC npc = optional.get();
            npc.getActions().forEach((action) -> action.execute(npc, NPCInteractType.ATTACK, player));
        }
    }

}
