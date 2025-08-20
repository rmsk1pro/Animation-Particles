package br.trcraft.animation.Comandos;

import br.trcraft.animation.Main;
import br.trcraft.animation.object.ParticleMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AnimationCommand implements CommandExecutor {

    private final Main plugin;

    public AnimationCommand(Main plugin) {
        this.plugin = plugin;
        if (plugin.getParticleMenu() == null) {
            plugin.setParticleMenu(new ParticleMenu(plugin));
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Só jogadores podem usar
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getConfig().getString("messages.only-players", "§c⚠ Este comando só pode ser usado por jogadores!"));
            return true;
        }

        // Permissão geral para usar o comando
        if (!player.hasPermission("particulas.usar")) {
            player.sendMessage(plugin.getConfig().getString("messages.no-permission", "§c❌ Você não tem permissão para usar este comando!"));
            return true;
        }

        // Verifica se o menu está carregado
        ParticleMenu menu = plugin.getParticleMenu();
        if (menu == null) {
            player.sendMessage(plugin.getConfig().getString("messages.menu-not-loaded", "§c❌ O menu de partículas ainda não foi carregado. Tente novamente mais tarde!"));
            return true;
        }

        // Comando reload
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!player.hasPermission("particulas.reload")) {
                player.sendMessage(plugin.getConfig().getString("messages.no-reload-permission", "§c❌ Você não tem permissão para recarregar o plugin!"));
                return true;
            }

            plugin.reloadPluginConfig();
            player.sendMessage(plugin.getConfig().getString("messages.config-reloaded", "§a✅ Configuração recarregada com sucesso!"));
            return true;
        }

        // Abre o menu de partículas
        menu.openMenu(player);
        player.sendMessage(plugin.getConfig().getString("messages.menu-opened", "§a✨ Menu de partículas aberto! Escolha com estilo! ✨"));

        return true;
    }
}
