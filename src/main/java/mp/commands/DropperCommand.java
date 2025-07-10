package mp.commands;

import mp.game.DropperGameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Comando /droppergame para iniciar el minijuego Dropper.
 * Teletransporta al jugador y genera bloques trampa.
 */
public class DropperCommand implements CommandExecutor {
    private final DropperGameManager dropperGameManager;

    /**
     * Constructor: recibe la instancia de DropperGameManager
     */
    public DropperCommand(DropperGameManager dropperGameManager) {
        this.dropperGameManager = dropperGameManager;
    }

    /**
     * Maneja la ejecución del comando /droppergame
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSolo los jugadores pueden usar este comando.");
            return true;
        }
        Player player = (Player) sender;
        dropperGameManager.startForPlayer(player);
        return true;
    }
}
