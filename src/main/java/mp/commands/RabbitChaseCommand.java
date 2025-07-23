package mp.commands;

import mp.game.RabbitChaseGame;
import mp.plugin1.MiPlugin;
import mp.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RabbitChaseCommand implements CommandExecutor {
    
    private final MiPlugin plugin;
    private final RabbitChaseGame rabbitGame;
    
    public RabbitChaseCommand(MiPlugin plugin, RabbitChaseGame rabbitGame) {
        this.plugin = plugin;
        this.rabbitGame = rabbitGame;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Verificar que sea un jugador
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtils.getColoredMessage("&cEste comando solo puede ser usado por jugadores."));
            return true;
        }
        
        Player player = (Player) sender;
        
        // Verificar si hay argumentos adicionales para futuras expansiones
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("ayuda")) {
                showHelp(player);
                return true;
            }
        }
        
        // Intentar iniciar el juego
        boolean gameStarted = rabbitGame.startGame(player);
        
        if (!gameStarted) {
            // El mensaje de error ya se envía desde RabbitChaseGame
            return true;
        }
        
        return true;
    }
    
    private void showHelp(Player player) {
        player.sendMessage(MessageUtils.getColoredMessage("&6&l🐰 AYUDA - MINIJUEGO DEL CONEJO 🐰"));
        player.sendMessage(MessageUtils.getColoredMessage("&e/conejo &7- Inicia el minijuego"));
        player.sendMessage(MessageUtils.getColoredMessage("&7"));
        player.sendMessage(MessageUtils.getColoredMessage("&fObjetivo: &7Golpea al conejo antes de que pasen 10 segundos"));
        player.sendMessage(MessageUtils.getColoredMessage("&fRecompensa: &a25 XP &7por victoria"));
        player.sendMessage(MessageUtils.getColoredMessage("&fÁrea de juego: &710x10 bloques alrededor de tu posición"));
        player.sendMessage(MessageUtils.getColoredMessage("&7"));
        player.sendMessage(MessageUtils.getColoredMessage("&c¡El conejo se teletransporta cada segundo!"));
    }
}