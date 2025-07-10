package mp.miPlugin.commands;

import mp.plugin1.MiPlugin;
import mp.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommands implements CommandExecutor {
    private MiPlugin plugin;

    public FlyCommands(MiPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("miPlugin.commands.volar")) {
            sender.sendMessage(MessageUtils.getColoredMessage(MiPlugin.prefix + "&cNo tienes permisos para usar este comando"));
            return true;
        }

        Player targetPlayer;

        // Determinar el jugador objetivo
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(MessageUtils.getColoredMessage(MiPlugin.prefix + "&cUso: &7/volar <jugador>"));
                return true;
            }
            targetPlayer = (Player) sender;
        } else {
            targetPlayer = Bukkit.getPlayer(args[0]);
            if (targetPlayer == null) {
                sender.sendMessage(MessageUtils.getColoredMessage(MiPlugin.prefix + "&cJugador no encontrado: &7" + args[0]));
                return true;
            }
        }

        // Cambiar estado de vuelo
        boolean newState = !targetPlayer.getAllowFlight();
        targetPlayer.setAllowFlight(newState);
        targetPlayer.setFlying(newState);

        // Mensajes sin duplicación
        String estado = newState ? "&aACTIVADO" : "&cDESACTIVADO";
        String mensajeTarget = MiPlugin.prefix + "&7Modo vuelo: " + estado;
        String mensajeSender = MiPlugin.prefix + "&7Modo vuelo " + estado + " &7para &e" + targetPlayer.getName();

        // Enviar mensaje al jugador afectado
        targetPlayer.sendMessage(MessageUtils.getColoredMessage(mensajeTarget));

        // Enviar mensaje al ejecutor solo si es diferente del objetivo
        if (!sender.equals(targetPlayer)) {
            sender.sendMessage(MessageUtils.getColoredMessage(mensajeSender));
        }

        return true;
    }
}