package mp.casino;

import mp.plugin1.MiPlugin;
import mp.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class CasinoListener implements Listener {
    
    private final MiPlugin plugin;
    private final SlotMachineManager slotMachineManager;
    
    public CasinoListener(MiPlugin plugin) {
        this.plugin = plugin;
        this.slotMachineManager = new SlotMachineManager(plugin);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        // Verificar que es un jugador
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getWhoClicked();
        
        // Verificar que es el inventario del casino
        if (event.getView().getTitle() == null || !event.getView().getTitle().contains("Casino")) {
            return;
        }
        
        // Cancelar el evento para evitar que se muevan items
        event.setCancelled(true);
        
        // Verificar que se hizo clic en un item válido
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }
        
        // Verificar si se hizo clic en el botón de girar
        if (clickedItem.getType() == Material.EMERALD_BLOCK && 
            clickedItem.hasItemMeta() && 
            clickedItem.getItemMeta().hasDisplayName() &&
            clickedItem.getItemMeta().getDisplayName().contains("GIRAR")) {
            
            // Iniciar la animación de giro
            slotMachineManager.startSpinAnimation(player);
            
        } else if (clickedItem.getType() == Material.REDSTONE_BLOCK &&
                   clickedItem.hasItemMeta() &&
                   clickedItem.getItemMeta().hasDisplayName() &&
                   clickedItem.getItemMeta().getDisplayName().contains("GIRANDO")) {
            
            // El jugador intentó hacer clic mientras la animación está en curso
            player.sendMessage(MessageUtils.getColoredMessage("&c⚠ ¡Espera a que termine el giro actual!"));
        }
    }
    
    @EventHandler(priority = EventPriority.NORMAL)
    public void onInventoryClose(InventoryCloseEvent event) {
        // Verificar que es un jugador
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        
        Player player = (Player) event.getPlayer();
        
        // Verificar que es el inventario del casino
        if (event.getView().getTitle() == null || !event.getView().getTitle().contains("Casino")) {
            return;
        }
        
        // Mensaje de despedida
        player.sendMessage(MessageUtils.getColoredMessage("&6¡Gracias por visitar el casino! ¡Vuelve pronto!"));
    }
}