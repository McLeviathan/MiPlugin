package mp.listeners;

import mp.game.DropperGameManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * Listener para victoria por caer en agua en el Dropper.
 * Cancela el daño por caída, da feedback y teletransporta al inicio.
 */
public class DropperWinByWaterListener implements Listener {
    private final DropperGameManager dropperGameManager;
    private static final int WIN_Y = 10;

    public DropperWinByWaterListener(DropperGameManager dropperGameManager) {
        this.dropperGameManager = dropperGameManager;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (!dropperGameManager.isPlaying(player)) return;
        if (event.getCause() != DamageCause.FALL) return;
        // Solo si está en y=10 y el bloque bajo sus pies es agua
        if (player.getLocation().getBlockY() == WIN_Y) {
            Material type = player.getLocation().subtract(0, 1, 0).getBlock().getType();
            if (type == Material.WATER || type == Material.STATIONARY_WATER) {
                event.setCancelled(true);
                int points = dropperGameManager.addPoint(player);
                player.sendMessage("§b¡Caíste en el agua, ganaste! §7(Puntos: " + points + ")");
                if (dropperGameManager.getPlayerStartLocation() != null) {
                    player.teleport(dropperGameManager.getPlayerStartLocation());
                }
                dropperGameManager.endGame();
            }
        }
    }
}
