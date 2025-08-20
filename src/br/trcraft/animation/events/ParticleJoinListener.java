package br.trcraft.animation.events;

import br.trcraft.animation.Main;
import br.trcraft.animation.object.ParticleMenu;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ParticleJoinListener implements Listener {

    private final Main plugin;
    private final ParticleMenu menu;

    public ParticleJoinListener(Main plugin, ParticleMenu menu) {
        this.plugin = plugin;
        this.menu = menu;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        String welcomeMsg = plugin.getConfig().getString("messages.welcome", "§6§l✨ Bem-vindo(a) ao servidor, §e{player}§6! ✨")
                .replace("{player}", player.getName());
        String joinInstructions = plugin.getConfig().getString("messages.join-instructions", "§7Clique no item especial que você recebeu para escolher suas partículas!");
        String useCommandMsg = plugin.getConfig().getString("messages.use-command-msg", "§7Use §e/particula§7 para receber o item de partículas.");

        player.sendMessage(welcomeMsg);
        player.sendMessage(joinInstructions);

        boolean giveOnJoin = plugin.getConfig().getBoolean("give-particle-item-on-join", true);

        if (giveOnJoin) {
            giveParticleItem(player);
        } else {
            player.sendMessage(useCommandMsg);
        }
    }

    private void giveParticleItem(Player player) {
        ItemStack particleItem = new ItemStack(Material.ENDER_CHEST);
        ItemMeta meta = particleItem.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§6§lPARTÍCULAS ✨");
            particleItem.setItemMeta(meta);
        }

        boolean hasItem = player.getInventory().containsAtLeast(particleItem, 1);

        if (!hasItem) {
            int slot = plugin.getConfig().getInt("particle-item-slot", 1);
            if (slot < 0) slot = 0;
            if (slot > 35) slot = 35;

            if (player.getInventory().getItem(slot) == null || player.getInventory().getItem(slot).getType() == Material.AIR) {
                player.getInventory().setItem(slot, particleItem);
            } else {
                player.getInventory().addItem(particleItem);
            }

            String receivedItemMsg = plugin.getConfig().getString("messages.received-item", "§a✨ Você recebeu o item de partículas! ✨");
            player.sendMessage(receivedItemMsg);
        } else {
            String alreadyHasMsg = plugin.getConfig().getString("messages.item-already-has", "§eVocê já possui o item de partículas!");
            player.sendMessage(alreadyHasMsg);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || item.getType() != Material.ENDER_CHEST) return;
        if (item.getItemMeta() == null) return;
        if (!"§6§lPARTÍCULAS ✨".equals(item.getItemMeta().getDisplayName())) return;

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1f, 1f);
            menu.openMenu(player);

            String menuOpenedMsg = plugin.getConfig().getString("messages.menu-opened", "§a✨ Menu de partículas aberto! Escolha com estilo! ✨");
            player.sendMessage(menuOpenedMsg);

            event.setCancelled(true);
        }
    }
}
