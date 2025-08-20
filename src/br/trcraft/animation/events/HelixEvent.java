package br.trcraft.animation.events;

import br.trcraft.animation.Main;
import br.trcraft.animation.object.Helix;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class HelixEvent implements Listener {

    private final Main plugin;
    private BukkitRunnable helixTask;

    public HelixEvent(Main plugin) {
        this.plugin = plugin;
        startHelixTask();
    }

    private void startHelixTask() {
        helixTask = new BukkitRunnable() {
            @Override
            public void run() {
                Map<String, Helix> helixMap = plugin.getHelixMap();
                if (helixMap == null || helixMap.isEmpty()) return;

                // Atualiza cada h�lice e remove jogadores offline ou problemas
                helixMap.values().removeIf(helix -> {
                    if (helix == null) return true;

                    Player player = helix.getPlayer();
                    if (player == null || !player.isOnline()) return true;

                    try {
                        helix.update();
                        return false; // mant�m a h�lice
                    } catch (Exception ex) {
                        plugin.getLogger().warning("Erro ao atualizar h�lice de " + player.getName() + ": " + ex.getMessage());
                        return true; // remove h�lice com erro
                    }
                });
            }
        };
        // Atualiza a cada 2 ticks (~0,1s)
        helixTask.runTaskTimer(plugin, 0L, 2L);
    }

    public void cancelTask() {
        if (helixTask != null) helixTask.cancel();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (player != null) {
            // Remove h�lice do jogador que saiu
            plugin.getHelixMap().remove(player.getName().toLowerCase());
        }
    }
}
