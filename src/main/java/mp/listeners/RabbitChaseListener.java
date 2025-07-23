package mp.listeners;

import mp.game.RabbitChaseGame;
import mp.plugin1.MiPlugin;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class RabbitChaseListener implements Listener {
    
    private final MiPlugin plugin;
    private final RabbitChaseGame rabbitGame;
    
    public RabbitChaseListener(MiPlugin plugin, RabbitChaseGame rabbitGame) {
        this.plugin = plugin;
        this.rabbitGame = rabbitGame;
    }
    
    /**
     * Maneja cuando una entidad es dañada por otra entidad
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // Verificar que el atacante sea un jugador
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        
        // Verificar que la víctima sea un conejo
        if (event.getEntityType() != EntityType.RABBIT) {
            return;
        }
        
        Player player = (Player) event.getDamager();
        Rabbit rabbit = (Rabbit) event.getEntity();
        
        // Verificar si es un conejo del minijuego
        if (rabbit.getCustomName() == null || !rabbit.getCustomName().contains("¡Atrápame!")) {
            return; // No es un conejo del minijuego
        }
        
        // Cancelar el daño para que el conejo no muera por el golpe normal
        event.setCancelled(true);
        
        // Manejar el golpe en el juego
        boolean wasGameRabbit = rabbitGame.handleRabbitHit(player, rabbit);
        
        if (!wasGameRabbit) {
            // No era el conejo de este jugador, permitir el daño normal
            event.setCancelled(false);
        }
    }
    
    /**
     * Maneja cuando un jugador se desconecta para limpiar su juego activo
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Terminar el juego del jugador si tiene uno activo
        rabbitGame.endPlayerGame(player);
    }
}