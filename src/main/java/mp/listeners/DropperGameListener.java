package mp.listeners;

import mp.game.DropperGameManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Listener para el minijuego Dropper.
 * Detecta colisiones con bloques trampa y llegada al suelo.
 * Compatible con Spigot 1.8.8.
 */
public class DropperGameListener implements Listener {
    private final DropperGameManager dropperGame;
    private static final int FLOOR_Y = 10; // Y del suelo

    public DropperGameListener(DropperGameManager dropperGame) {
        this.dropperGame = dropperGame;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!dropperGame.isPlaying(player)) {
            return;
        }
        
        Location playerLoc = player.getLocation();
        
        // Detectar llegada al bloque seguro (slime)
        Location safeLoc = dropperGame.getSafeBlockLocation();
        if (playerLoc.getBlockX() == safeLoc.getBlockX() &&
            playerLoc.getBlockY() == safeLoc.getBlockY() &&
            playerLoc.getBlockZ() == safeLoc.getBlockZ()) {
            dropperGame.handleWin(player);
            return;
        }
        
        // Detectar caída al suelo
        if (playerLoc.getBlockY() <= FLOOR_Y) {
            dropperGame.handleLose(player);
        }
    }
}
