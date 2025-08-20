package br.trcraft.animation;

import br.trcraft.animation.Comandos.AnimationCommand;
import br.trcraft.animation.events.HelixEvent;
import br.trcraft.animation.events.ParticleJoinListener;
import br.trcraft.animation.object.Helix;
import br.trcraft.animation.object.ParticleMenu;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Main extends JavaPlugin {

    private final Map<String, Helix> helixMap = new ConcurrentHashMap<>();
    private ParticleMenu particleMenu;
    private HelixEvent helixEvent;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.particleMenu = new ParticleMenu(this);
        getServer().getPluginManager().registerEvents(particleMenu, this);

        this.helixEvent = new HelixEvent(this);
        getServer().getPluginManager().registerEvents(helixEvent, this);

        getServer().getPluginManager().registerEvents(new ParticleJoinListener(this, particleMenu), this);

        if (getCommand("particula") != null) {
            getCommand("particula").setExecutor(new AnimationCommand(this));
        } else {
            getLogger().warning("Comando 'particula' não encontrado no plugin.yml!");
        }

        sendAsciiLogo(true);
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§9§lANIMATION §7» §fPlugin §a§lLIGADO§f com sucesso! ✨");
        Bukkit.getConsoleSender().sendMessage("§9§lANIMATION §7» §fPrepare-se para uma explosão de partículas §e🌌✨");
        Bukkit.getConsoleSender().sendMessage("");
    }

    @Override
    public void onDisable() {
        if (helixEvent != null) helixEvent.cancelTask();
        helixMap.clear();

        sendAsciiLogo(false);
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§4§lANIMATION §7» §fPlugin §4§ldesligado§f com segurança ✨");
        Bukkit.getConsoleSender().sendMessage("§4§lANIMATION §7» §fAté a próxima! Que as partículas estejam com você §c❌§f✨🌌");
        Bukkit.getConsoleSender().sendMessage("");
    }

    public Map<String, Helix> getHelixMap() {
        return helixMap;
    }

    public ParticleMenu getParticleMenu() {
        return particleMenu;
    }

    public void setParticleMenu(ParticleMenu particleMenu) {
        this.particleMenu = particleMenu;
    }

    public void reloadPluginConfig() {
        reloadConfig();
        saveConfig();
        this.particleMenu = new ParticleMenu(this);
        getLogger().info("Configuração recarregada com sucesso!");
    }

    private final String[] ASCII_LOGO = new String[]{
            "",
            " █████╗ ███╗   ██╗██╗███╗   ███╗ █████╗ ████████╗██╗ ██████╗ ███╗   ██╗",
            "██╔══██╗████╗  ██║██║████╗ ████║██╔══██╗╚══██╔══╝██║██╔═══██╗████╗  ██║",
            "███████║██╔██╗ ██║██║██╔████╔██║███████║   ██║   ██║██║   ██║██╔██╗ ██║",
            "██╔══██║██║╚██╗██║██║██║╚██╔╝██║██╔══██║   ██║   ██║██║   ██║██║╚██╗██║",
            "██║  ██║██║ ╚████║██║██║ ╚═╝ ██║██║  ██║   ██║   ██║╚██████╔╝██║ ╚████║",
            "╚═╝  ╚═╝╚═╝  ╚═══╝╚═╝╚═╝     ╚═╝╚═╝  ╚═╝   ╚═╝   ╚═╝ ╚═════╝ ╚═╝  ╚═══╝",
            ""
    };

    private void sendAsciiLogo(boolean enabled) {
        String color = enabled ? "§9" : "§4";
        for (String line : ASCII_LOGO) {
            Bukkit.getConsoleSender().sendMessage(color + line);
        }
    }
}
