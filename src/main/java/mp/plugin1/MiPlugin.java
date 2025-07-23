package mp.plugin1;

import mp.commands.MemoryGameCommand;
import mp.commands.RabbitChaseCommand;
import mp.config.MainConfigManager;
import mp.game.DropperGameManager;
import mp.game.RabbitChaseGame;
import mp.listeners.MenuListener;
import mp.listeners.PlayerListener;
import mp.listeners.MemoryGameListener;
import mp.listeners.RabbitChaseListener;
import mp.listeners.ZombieListener;
import mp.game.MemoryGame;
import mp.managers.MenuInventoryManager;
import mp.managers.WaveManager;
import mp.miPlugin.commands.MainCommands;
import mp.miPlugin.commands.MenuCommand;
import mp.casino.CasinoCommand;
import mp.casino.CasinoListener;

import org.bukkit.plugin.java.JavaPlugin;

public class MiPlugin extends JavaPlugin {
    public static String prefix = "&8[&aMiPlugin&8] ";
    private MainConfigManager mainConfigManager;
    private MenuInventoryManager menuInventoryManager;
    private WaveManager waveManager;
    private MemoryGame memoryGame;
    private RabbitChaseGame rabbitChaseGame;
    private final DropperGameManager dropperGameManager = new DropperGameManager();

    @Override
    public void onEnable() {
        mainConfigManager = new MainConfigManager(this);
        menuInventoryManager = new MenuInventoryManager(this);
        waveManager = new WaveManager(this);
        memoryGame = new MemoryGame(this);
        rabbitChaseGame = new RabbitChaseGame(this);
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
        } catch (Exception e) {
            getLogger().warning("No se pudo registrar el comando volar: " + e.getMessage());
        }

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new MenuListener(this), this);
        getServer().getPluginManager().registerEvents(new ZombieListener(this), this);
        getServer().getPluginManager().registerEvents(new mp.listeners.MobSpawnListener(this), this);
        getServer().getPluginManager().registerEvents(new mp.listeners.DropperWinByWaterListener(dropperGameManager), this);

        // Registrar sistema de casino
        getCommand("casino").setExecutor(new CasinoCommand(this));
        getServer().getPluginManager().registerEvents(new CasinoListener(this), this);

        // Registrar minijuego del conejo
        getCommand("conejo").setExecutor(new RabbitChaseCommand(this, rabbitChaseGame));
        getServer().getPluginManager().registerEvents(new RabbitChaseListener(this, rabbitChaseGame), this);

        // Mensaje de inicio en consola
        getLogger().info("==============================");
        getLogger().info("[MiPlugin] Plugin iniciado correctamente!");
        getLogger().info("[MiPlugin] Versión: " + getDescription().getVersion());
        getLogger().info("[MiPlugin] Autor: " + getDescription().getAuthors());
        getLogger().info("[MiPlugin] Sistema de oleadas listo para iniciar manualmente");
        getLogger().info("[MiPlugin] Casino con tragamonedas disponible con /casino");
        getLogger().info("[MiPlugin] Minijuego del conejo disponible con /conejo");
        getLogger().info("==============================");

        // NO iniciar el sistema de oleadas automáticamente - se inicia desde /menu
    }

    @Override
    public void onDisable() {
        // Limpiar juegos activos para evitar memory leaks
        if (rabbitChaseGame != null) {
            rabbitChaseGame.cleanup();
        }
        
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
