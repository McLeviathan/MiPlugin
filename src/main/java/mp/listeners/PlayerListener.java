package mp.listeners;

import mp.plugin1.MiPlugin;
import mp.utils.ItemUtils;
import mp.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class PlayerListener implements Listener {

    private MiPlugin plugin;

    public PlayerListener(MiPlugin plugin) {
        this.plugin = plugin;
        // Validar que el plugin no sea null
        if (plugin == null) {
            Bukkit.getLogger().severe("Plugin es null en PlayerListener");
        }
        // Establecer modo pacífico en todos los mundos al iniciar
        setPeacefulInAllWorlds();
    }

    private void setPeacefulInAllWorlds() {
        for (World world : Bukkit.getWorlds()) {
            world.setDifficulty(org.bukkit.Difficulty.PEACEFUL);
            Bukkit.getLogger().info(MessageUtils.getColoredMessage(MiPlugin.prefix + "&aMundo " + world.getName() + " establecido en modo pacífico"));
        }
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        World world = event.getWorld();
        world.setDifficulty(org.bukkit.Difficulty.PEACEFUL);
        Bukkit.getLogger().info(MessageUtils.getColoredMessage(MiPlugin.prefix + "&aNuevo mundo " + world.getName() + " establecido en modo pacífico"));
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        // Convertir el mensaje a minúsculas y buscar "lol"
        if (message.toLowerCase().contains("lol")) {
            event.setCancelled(true);
            player.sendMessage(MessageUtils.getColoredMessage("&cNo puedes usar esa palabra"));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Comprobar si el mensaje de bienvenida está habilitado
        if (plugin.getMainConfigManager() != null && plugin.getMainConfigManager().isWelcomeMessageEnabled()) {
            // Enviar mensajes de bienvenida desde la configuración
            for (String line : plugin.getMainConfigManager().getWelcomeMessageMessage()) {
                player.sendMessage(MessageUtils.getColoredMessage(line.replace("%player%", player.getName())));
            }
        } else {
            // Mensaje de bienvenida predeterminado si la configuración no está disponible
            player.sendMessage(MessageUtils.getColoredMessage(MiPlugin.prefix + "&7Bienvenido al Servidor&a"));
        }

        // Teleportar al jugador
        World world = Bukkit.getWorld("world");
        if (world != null) {
            Location location = new Location(world, -204.5, 4, -1218.5);
            player.teleport(location);
        } else {
            Bukkit.getLogger().warning("[MiPlugin] No se pudo encontrar el mundo 'world' para teleportar al jugador " + player.getName());
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (player.getWorld().getName().equalsIgnoreCase("world") && !player.hasPermission("miPlugin.admin")) {
            event.setCancelled(true);

            // Comprobar que no sea null antes de usar
            if (plugin.getMainConfigManager() != null) {
                String message = plugin.getMainConfigManager().getPreventBlockBreak();
                if (message != null) {
                    player.sendMessage(MessageUtils.getColoredMessage(MiPlugin.prefix + message));
                } else {
                    player.sendMessage(MessageUtils.getColoredMessage(MiPlugin.prefix + "&cNo puedes romper bloques en este mundo"));
                }
            } else {
                player.sendMessage(MessageUtils.getColoredMessage(MiPlugin.prefix + "&cNo puedes romper bloques en este mundo (config no disponible)"));
            }
        }
        Block block = event.getBlock();
        if (block.getType().equals(Material.EMERALD_ORE)) {
            int num = new Random().nextInt(10);
            if (num >= 6) {
                ItemStack item = ItemUtils.generateEsmeralditem(1);
                block.getWorld().dropItemNaturally(block.getLocation(), item);
            }
        }
    }
}
