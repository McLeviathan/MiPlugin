package mp.commands;

import mp.game.DropperGameManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Comando /droppergame para iniciar el minijuego Dropper.
 * Registra y ejecuta la lógica de DropperGame.
 */
public class DropperGameCommand implements CommandExecutor {
    private final DropperGameManager dropperGame;

    /**
     * Constructor: recibe la instancia de DropperGame
     */
    public DropperGameCommand(DropperGameManager dropperGame) {
        this.dropperGame = dropperGame;
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
        dropperGame.startForPlayer(player);
        return true;
    }
}
