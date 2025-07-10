package mp.miPlugin.commands;

import mp.plugin1.MiPlugin;
import mp.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MenuCommand implements CommandExecutor {

    private final MiPlugin plugin;

    public MenuCommand(MiPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Verificar si el comando fue ejecutado por un jugador
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtils.getColoredMessage(MiPlugin.prefix + "&cEste comando solo puede usarlo un jugador."));
            return true;
        }

        Player player = (Player) sender;

        // Abrir menú principal con opciones de oleadas
        plugin.getMenuInventoryManager().openMainInventory(player);

        // Mensaje de bienvenida
        player.sendMessage(MessageUtils.getColoredMessage("&a&l✦ &f¡Bienvenido al menú principal! &a&l✦"));

        return true;
    }
}
