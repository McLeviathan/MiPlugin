package mp.config;

import mp.plugin1.MiPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class MainConfigManager {

    private static CustomConfig config;
    private MiPlugin plugin;
    private String preventBlockBreak;
    private boolean welcomeMessageEnabled;
    private List<String> welcomeMessageMessage;

    public MainConfigManager(MiPlugin plugin){
        this.plugin = plugin;
        config = new CustomConfig("config.yml", null, plugin, false);
        config.registerConfig();
        loadConfig();
    }

    public void loadConfig(){
        FileConfiguration config = this.config.getConfig();

        // Corregidos los paths para acceder a los valores correctamente
        preventBlockBreak = config.getString("messages.prevent_block_break", "&cNo puedes romper bloques en este mundo");
        welcomeMessageEnabled = config.getBoolean("messages.welcome_message.enabled", true);
        welcomeMessageMessage = config.getStringList("messages.welcome_message.message");

        // Log para depuración
        plugin.getLogger().info("Cargando configuración:");
        plugin.getLogger().info("prevent_block_break: " + preventBlockBreak);
        plugin.getLogger().info("welcome_message.enabled: " + welcomeMessageEnabled);
        plugin.getLogger().info("welcome_message.message size: " + welcomeMessageMessage.size());
    }

    public void reloadConfig(){
        if (config == null) {
            plugin.getLogger().severe("Error: config es null al intentar recargar");
            return;
        }
        boolean success = config.reloadConfig();
        if (success) {
            loadConfig();
            plugin.getLogger().info("Configuración recargada exitosamente");
        } else {
            plugin.getLogger().severe("Error al recargar la configuración");
        }
    }

    public String getPreventBlockBreak() {
        return preventBlockBreak;
    }

    public boolean isWelcomeMessageEnabled() {
        return welcomeMessageEnabled;
    }

    public List<String> getWelcomeMessageMessage() {
        return welcomeMessageMessage;
    }
}