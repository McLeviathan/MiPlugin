package mp.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import mp.game.MemoryGame;
import mp.plugin1.MiPlugin;

/**
 * Listener para manejar los clics en los bloques del juego de memoria
 */
public class MemoryGameListener implements Listener {
    private final MemoryGame memoryGame;

    public MemoryGameListener(MemoryGame memoryGame) {
        this.memoryGame = memoryGame;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }

        // Verificar si el bloque es de lana (parte del juego)
        if (clickedBlock.getType() == Material.WOOL) {
            
            // Manejar el clic en el bloque del juego
            memoryGame.handleBlockClick(clickedBlock);
            event.setCancelled(true); // Evitar que el jugador coloque bloques
        }
    }
}
