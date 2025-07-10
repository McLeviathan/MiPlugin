package mp.plugin1;

import mp.commands.MemoryGameCommand;
import mp.config.MainConfigManager;
import mp.game.DropperGameManager;
import mp.listeners.MenuListener;
import mp.listeners.PlayerListener;
import mp.listeners.MemoryGameListener;
import mp.listeners.ZombieListener;
import mp.game.MemoryGame;
import mp.managers.MenuInventoryManager;
import mp.managers.WaveManager;
import mp.miPlugin.commands.MainCommands;
import mp.miPlugin.commands.MenuCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class MiPlugin extends JavaPlugin {
    public static String prefix = "&8[&aMiPlugin&8] ";
    private MainConfigManager mainConfigManager;
    private MenuInventoryManager menuInventoryManager;
    private WaveManager waveManager;
    private MemoryGame memoryGame;
    private final DropperGameManager dropperGameManager = new DropperGameManager();

    @Override
    public void onEnable() {
        mainConfigManager = new MainConfigManager(this);
        menuInventoryManager = new MenuInventoryManager(this);
        waveManager = new WaveManager(this);
        memoryGame = new MemoryGame(this);
        getCommand("droppergame").setExecutor(new mp.commands.DropperCommand(dropperGameManager));
        getServer().getPluginManager().registerEvents(new mp.listeners.DropperListener(dropperGameManager), this);

        getCommand("miPlugin").setExecutor(new MainCommands(this));
        getCommand("memorygame").setExecutor(new MemoryGameCommand(this));
        getServer().getPluginManager().registerEvents(new MemoryGameListener(memoryGame), this);
        getCommand("menu").setExecutor(new MenuCommand(this));
        getCommand("experiencia").setExecutor(new mp.miPlugin.commands.XpCommand(this));
        // Registrar comando volar si existe su clase
        try {
            getCommand("volar").setExecutor(new mp.miPlugin.commands.FlyCommands(this));
        } catch (Exception ignored) {}

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new MenuListener(this), this);
        getServer().getPluginManager().registerEvents(new ZombieListener(this), this);
        getServer().getPluginManager().registerEvents(new mp.listeners.MobSpawnListener(this), this);
        getServer().getPluginManager().registerEvents(new mp.listeners.DropperWinByWaterListener(dropperGameManager), this);

        // Mensaje de inicio en consola
        getLogger().info("==============================");
        getLogger().info("[MiPlugin] Plugin iniciado correctamente!");
        getLogger().info("[MiPlugin] Versión: " + getDescription().getVersion());
        getLogger().info("[MiPlugin] Autor: " + getDescription().getAuthors());
        getLogger().info("[MiPlugin] Sistema de oleadas listo para iniciar manualmente");
        getLogger().info("==============================");

        // NO iniciar el sistema de oleadas automáticamente - se inicia desde /menu
    }

    @Override
    public void onDisable() {
        // Mensaje de fin en consola
        getLogger().info("==============================");
        getLogger().info("[MiPlugin] Plugin detenido correctamente!");
        getLogger().info("==============================");
    }

    public MainConfigManager getMainConfigManager() {
        return mainConfigManager;
    }

    public MenuInventoryManager getMenuInventoryManager() {
        return menuInventoryManager;
    }

    public WaveManager getWaveManager() {
        return waveManager;
    }

    public MemoryGame getMemoryGame() {
        return memoryGame;
    }
}
