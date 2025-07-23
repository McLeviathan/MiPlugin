package mp.casino;

import mp.plugin1.MiPlugin;
import mp.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CasinoCommand implements CommandExecutor {
    
    private final MiPlugin plugin;
    private final SlotMachineManager slotMachineManager;
    
    public CasinoCommand(MiPlugin plugin) {
        this.plugin = plugin;
        this.slotMachineManager = new SlotMachineManager(plugin);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtils.getColoredMessage("&cEste comando solo puede ser usado por jugadores."));
            return true;
        }
        
        Player player = (Player) sender;
        
        // Abrir la GUI del casino
        slotMachineManager.openSlotMachine(player);
        player.sendMessage(MessageUtils.getColoredMessage("&6&l🎰 &e¡Bienvenido al Casino! &6&l🎰"));
        player.sendMessage(MessageUtils.getColoredMessage("&7¡Haz clic en &aGirar &7para probar tu suerte!"));
        
        return true;
    }
}