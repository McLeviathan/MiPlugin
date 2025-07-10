package mp.commands;

import mp.game.MemoryGame;
import mp.plugin1.MiPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Comando para iniciar el juego de memoria
 */
public class MemoryGameCommand implements CommandExecutor {
    private final MiPlugin plugin;
    private final MemoryGame memoryGame;

    public MemoryGameCommand(MiPlugin plugin) {
        this.plugin = plugin;
        this.memoryGame = plugin.getMemoryGame();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cEste comando solo puede ser usado por jugadores.");
            return true;
        }

        Player player = (Player) sender;
        memoryGame.startGame(player);
        player.sendMessage("§a¡Juego de memoria iniciado! Observa la secuencia y repítela clickeando los bloques.");
        return true;
    }
}
