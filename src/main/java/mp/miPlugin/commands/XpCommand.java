package mp.miPlugin.commands;

import mp.plugin1.MiPlugin;
import mp.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class XpCommand implements CommandExecutor {
    private MiPlugin plugin;

    public XpCommand(MiPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        // Verificar permisos
        if (!sender.hasPermission("miplugin.commands.experiencia")) {
            sender.sendMessage(MessageUtils.getColoredMessage(MiPlugin.prefix + "&cNo tienes permisos para usar este comando"));
            return true;
        }

        // Verificar argumentos
        if (args.length == 0) {
            sender.sendMessage(MessageUtils.getColoredMessage(MiPlugin.prefix + "&cUso: &7/experiencia <cantidad> [jugador]"));
            return true;
        }

        // Validar cantidad de experiencia
        int cantidad;
        try {
            cantidad = Integer.parseInt(args[0]);
            if (cantidad <= 0) {
                sender.sendMessage(MessageUtils.getColoredMessage(MiPlugin.prefix + "&cDebes usar una cantidad válida mayor a 0"));
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(MessageUtils.getColoredMessage(MiPlugin.prefix + "&cDebes usar un número válido para la cantidad de experiencia"));
            return true;
        }

        // Determinar el jugador objetivo
        Player targetPlayer = null;

        // Caso 1: Un solo argumento (cantidad)
        if (args.length == 1) {
            // Solo jugadores en el juego pueden usar el comando con un argumento
            if (!(sender instanceof Player)) {
                sender.sendMessage(MessageUtils.getColoredMessage(MiPlugin.prefix + "&cDesde la consola debes especificar un jugador"));
                return true;
            }
            targetPlayer = (Player) sender;
        }
        // Caso 2: Dos argumentos (cantidad y jugador)
        else if (args.length == 2) {
            // Buscar jugador
            targetPlayer = Bukkit.getPlayer(args[1]);

            // Verificar si el jugador existe
            if (targetPlayer == null) {
                sender.sendMessage(MessageUtils.getColoredMessage(MiPlugin.prefix + "&cEl jugador &7" + args[1] + " &cno está conectado"));
                return true;
            }
        }
        // Caso 3: Demasiados argumentos
        else {
            sender.sendMessage(MessageUtils.getColoredMessage(MiPlugin.prefix + "&cUso: &7/experiencia <cantidad> [jugador]"));
            return true;
        }

        // Debug: Imprimir información actual de experiencia
        Bukkit.getLogger().info("Experiencia actual de " + targetPlayer.getName() + ":");
        Bukkit.getLogger().info("Total XP: " + targetPlayer.getTotalExperience());
        Bukkit.getLogger().info("Nivel actual: " + targetPlayer.getLevel());
        Bukkit.getLogger().info("Progreso de XP: " + targetPlayer.getExp());

        // Método alternativo para dar experiencia
        targetPlayer.giveExp(cantidad);

        // Debug: Imprimir información después de dar XP
        Bukkit.getLogger().info("Después de dar " + cantidad + " XP:");
        Bukkit.getLogger().info("Total XP: " + targetPlayer.getTotalExperience());
        Bukkit.getLogger().info("Nivel actual: " + targetPlayer.getLevel());
        Bukkit.getLogger().info("Progreso de XP: " + targetPlayer.getExp());

        // Mensajes de confirmación
        if (sender == targetPlayer) {
            // Si se da exp a sí mismo
            sender.sendMessage(MessageUtils.getColoredMessage(MiPlugin.prefix + "&aHas recibido &e" + cantidad + " &apuntos de experiencia"));
        } else {
            // Si un jugador (o consola) da exp a otro
            targetPlayer.sendMessage(MessageUtils.getColoredMessage(MiPlugin.prefix + "&aHas recibido &e" + cantidad + " &apuntos de experiencia"));
            sender.sendMessage(MessageUtils.getColoredMessage(MiPlugin.prefix + "&aHas dado &e" + cantidad + " &apuntos de experiencia a &7" + targetPlayer.getName()));
        }

        return true;
    }
}