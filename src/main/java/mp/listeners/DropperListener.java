package mp.listeners;

import mp.game.DropperGameManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Listener para el minijuego Dropper.
 * Detecta colisiones con bloques trampa y llegada al slime.
 */
public class DropperListener implements Listener {
    private final DropperGameManager dropperGameManager;
    private static final int SLIME_Y = 10; // Y del suelo seguro

    public DropperListener(DropperGameManager dropperGameManager) {
        this.dropperGameManager = dropperGameManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!dropperGameManager.isPlaying(player)) {
            return;
        }
        
        Block blockAtFeet = player.getLocation().getBlock();
        Block blockBelow = player.getLocation().subtract(0, 1, 0).getBlock();
        
        // PRIORIDAD 1: Detectar victoria (llegada al agua) - tiene prioridad sobre todo
        // Detecta agua en cualquier Y por debajo del área de caída (Y=12 hacia abajo)
        if (player.getLocation().getBlockY() <= 12) {
            Material typeBelow = blockBelow.getType();
            Material typeAtFeet = blockAtFeet.getType();
            if (typeBelow == Material.WATER || typeBelow == Material.STATIONARY_WATER || 
                typeAtFeet == Material.WATER || typeAtFeet == Material.STATIONARY_WATER) {
                player.sendMessage("§a¡Ganaste el Dropper! Aterrizaste en el agua.");
                int points = dropperGameManager.addPoint(player);
                player.sendMessage("§ePuntos totales: " + points);
                if (dropperGameManager.getPlayerStartLocation() != null) {
                    player.teleport(dropperGameManager.getPlayerStartLocation());
                }
                dropperGameManager.endGame();
                return;
            }
        }
        
        // PRIORIDAD 2: Detectar colisión con bloques trampa (solo si no está en la base segura)
        if (player.getLocation().getBlockY() > SLIME_Y + 1) { // Solo si está por encima de Y=11
            boolean trapCollision = dropperGameManager.getTrapBlocks().contains(blockAtFeet) || 
                                  dropperGameManager.getTrapBlocks().contains(blockBelow);
            if (trapCollision) {
                player.sendMessage("§c¡Tocaste un bloque trampa! ¡Perdiste!");
                if (dropperGameManager.getPlayerStartLocation() != null) {
                    player.teleport(dropperGameManager.getPlayerStartLocation());
                }
                dropperGameManager.endGame();
                return;
            }
        }
    }
}
