package br.trcraft.animation.object;

import br.trcraft.animation.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ParticleMenu implements Listener {

    private final Main plugin;
    private final String MENU_TITLE = "§6§l✨ Escolha sua Partícula ✨";
    private final int SLOTS = 54;

    // Slots da área de efeitos (linhas 2 e 3 do inventário)
    private final int[] EFFECT_SLOTS = {
            10,11,12,13,14,15,16,
            19,20,21,22,23,24,25
    };

    private final ItemStack borderGlass;
    private final ItemStack nextPageItem;
    private final ItemStack prevPageItem;
    private final ItemStack removeEffectItem;

    // Map para armazenar a página de cada inventário
    private final Map<Inventory, Integer> inventoryPages = new HashMap<>();

    public ParticleMenu(Main plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);

        borderGlass = createGlass(Material.LIGHT_GRAY_STAINED_GLASS_PANE, " ");
        nextPageItem = createGlass(Material.ARROW, "§aPróxima página");
        prevPageItem = createGlass(Material.ARROW, "§aPágina anterior");
        removeEffectItem = createGlass(Material.BARRIER, "§cRemover efeito");
    }

    private ItemStack createGlass(Material mat, String name) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    public void openMenu(Player player) {
        openMenu(player, 0);
    }

    public void openMenu(Player player, int page) {
        Inventory inv = Bukkit.createInventory(null, SLOTS, MENU_TITLE);

        // Salva a página atual
        inventoryPages.put(inv, page);

        // Preenche todo o inventário com vidro
        for (int i = 0; i < SLOTS; i++) inv.setItem(i, borderGlass);

        // Itens fixos
        inv.setItem(49, removeEffectItem);
        if (page > 0) inv.setItem(45, prevPageItem);

        List<ParticleEffectType> effects = Arrays.asList(ParticleEffectType.values());
        int maxPages = (int) Math.ceil((double) effects.size() / EFFECT_SLOTS.length);
        page = Math.min(Math.max(page, 0), maxPages - 1);

        int start = page * EFFECT_SLOTS.length;
        int end = Math.min(start + EFFECT_SLOTS.length, effects.size());

        for (int i = start; i < end; i++) {
            ParticleEffectType effect = effects.get(i);
            ItemStack item = new ItemStack(effect.getIcon());
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§b" + effect.getDisplayName());
                meta.setLore(Arrays.asList(
                        "§7Clique para ativar este efeito",
                        "§8✨ Transforme seu personagem com estilo! ✨"
                ));
                item.setItemMeta(meta);
            }
            inv.setItem(EFFECT_SLOTS[i - start], item);
        }

        if (page < maxPages - 1) inv.setItem(53, nextPageItem);

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!MENU_TITLE.equals(event.getView().getTitle())) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        ItemMeta meta = clicked.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;

        String name = meta.getDisplayName();

        // Navegação entre páginas
        if (name.equals(nextPageItem.getItemMeta().getDisplayName())) {
            int currentPage = getCurrentPage(event.getInventory());
            openMenu(player, currentPage + 1);
            return;
        }
        if (name.equals(prevPageItem.getItemMeta().getDisplayName())) {
            int currentPage = getCurrentPage(event.getInventory());
            openMenu(player, currentPage - 1);
            return;
        }

        // Remover efeito
        if (name.equals(removeEffectItem.getItemMeta().getDisplayName())) {
            if (plugin.getHelixMap().remove(player.getName().toLowerCase()) != null) {
                player.sendMessage("§c⛔ Hélice de partículas removida!");
            } else {
                player.sendMessage("§e⚠ Você não possui nenhuma hélice ativa!");
            }
            player.closeInventory();
            return;
        }

        // Ativar efeito
        name = name.replace("§b", "");
        for (ParticleEffectType effect : ParticleEffectType.values()) {
            if (effect.getDisplayName().equals(name)) {
                Helix current = plugin.getHelixMap().get(player.getName().toLowerCase());

                if (current == null || !current.getEffect().equals(effect.getParticle())) {
                    plugin.getHelixMap().put(player.getName().toLowerCase(), new Helix(player, effect.getParticle()));
                    player.sendMessage("§a✨ Hélice de partículas §6" + effect.getDisplayName() + " §aativada!");
                } else {
                    player.sendMessage("§e⚠ Você já possui esta hélice ativa!");
                }

                player.closeInventory();
                break;
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (MENU_TITLE.equals(event.getView().getTitle())) {
            event.setCancelled(true);
        }
    }

    private int getCurrentPage(Inventory inv) {
        return inventoryPages.getOrDefault(inv, 0);
    }
}
