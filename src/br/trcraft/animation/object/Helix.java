package br.trcraft.animation.object;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class Helix {

    private final Player player;
    private final Particle particle;
    private double time;
    private final double radius = 1.0;   // distância do jogador
    private final double height = 2.0;   // altura total da hélice
    private final int rotations = 2;     // quantas voltas completas
    private final double viewDistance = 16.0; // distância máxima para ver partículas

    public Helix(Player player, Particle particle) {
        this.player = player;
        this.particle = particle;
        this.time = 0;
    }

    public Player getPlayer() {
        return player;
    }

    public Particle getEffect() {
        return particle;
    }

    public void update() {
        if (player == null || !player.isOnline()) return;

        // Incremento do tempo para animação suave
        time += Math.PI / 16;

        // Calcula posição circular em torno do jogador
        double angle = time;
        double x = radius * Math.cos(angle);
        double z = radius * Math.sin(angle);

        // Calcula a altura linear para fazer subir a hélice
        double y = (height / (2 * Math.PI * rotations)) * time;

        Location loc = player.getLocation().clone().add(x, y, z);

        // Mostra partículas apenas para jogadores dentro de 16 blocos
        for (Player nearby : player.getWorld().getPlayers()) {
            if (nearby.getLocation().distanceSquared(player.getLocation()) <= viewDistance * viewDistance) {
                nearby.spawnParticle(particle, loc, 1, 0, 0, 0, 0);
            }
        }

        // Reinicia o tempo para loop contínuo
        if (time > 2 * Math.PI * rotations) {
            time -= 2 * Math.PI * rotations;
        }
    }
}
