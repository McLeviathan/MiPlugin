package mp.casino;

import mp.plugin1.MiPlugin;
import mp.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class SlotMachineManager {
    
    private final MiPlugin plugin;
    private final Random random;
    private final Material[] slotMaterials;
    
    // Slots de la tragamonedas (posiciones 11, 13, 15)
    private static final int SLOT_1 = 11;
    private static final int SLOT_2 = 13;
    private static final int SLOT_3 = 15;
    private static final int SPIN_BUTTON = 22;
    
    public SlotMachineManager(MiPlugin plugin) {
        this.plugin = plugin;
        this.random = new Random();
        this.slotMaterials = new Material[]{
            Material.DIAMOND,
            Material.GOLD_INGOT,
            Material.EMERALD,
            Material.IRON_INGOT,
            Material.REDSTONE
        };
    }
    
    public void openSlotMachine(Player player) {
        Inventory casino = Bukkit.createInventory(null, 27, 
            MessageUtils.getColoredMessage("&6&l🎰 &eCasino - Tragamonedas &6&l🎰"));
        
        // Llenar el marco decorativo
        fillBorder(casino);
        
        // Colocar los slots iniciales
        setSlotItem(casino, SLOT_1, getRandomSlotMaterial());
        setSlotItem(casino, SLOT_2, getRandomSlotMaterial());
        setSlotItem(casino, SLOT_3, getRandomSlotMaterial());
        
        // Colocar el botón de girar
        setSpinButton(casino);
        
        player.openInventory(casino);
    }
    
    private void fillBorder(Inventory inventory) {
        ItemStack border = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5); // Lima
        ItemMeta borderMeta = border.getItemMeta();
        borderMeta.setDisplayName(MessageUtils.getColoredMessage("&a "));
        border.setItemMeta(borderMeta);
        
        // Llenar bordes (primera y última fila, y columnas laterales)
        for (int i = 0; i < 27; i++) {
            if (i < 9 || i >= 18 || i % 9 == 0 || i % 9 == 8) {
                // Evitar sobrescribir los slots y el botón
                if (i != SLOT_1 && i != SLOT_2 && i != SLOT_3 && i != SPIN_BUTTON) {
                    inventory.setItem(i, border);
                }
            }
        }
    }
    
    private void setSlotItem(Inventory inventory, int slot, Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageUtils.getColoredMessage("&f" + getSlotDisplayName(material)));
        item.setItemMeta(meta);
        inventory.setItem(slot, item);
    }
    
    private void setSpinButton(Inventory inventory) {
        ItemStack spinButton = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta meta = spinButton.getItemMeta();
        meta.setDisplayName(MessageUtils.getColoredMessage("&a&l🎲 GIRAR 🎲"));
        List<String> lore = new ArrayList<>();
        lore.add(MessageUtils.getColoredMessage("&7¡Haz clic para girar la tragamonedas!"));
        lore.add(MessageUtils.getColoredMessage("&7Si los 3 símbolos coinciden, ¡ganas!"));
        lore.add("");
        lore.add(MessageUtils.getColoredMessage("&ePremios:"));
        lore.add(MessageUtils.getColoredMessage("&b💎 Diamante: &f50 XP + 5 Pepitas de Oro"));
        lore.add(MessageUtils.getColoredMessage("&6🥇 Oro: &f30 XP + 3 Pepitas de Oro"));
        lore.add(MessageUtils.getColoredMessage("&a💚 Esmeralda: &f40 XP + 4 Pepitas de Oro"));
        lore.add(MessageUtils.getColoredMessage("&7⚙ Hierro: &f20 XP + 2 Pepitas de Oro"));
        lore.add(MessageUtils.getColoredMessage("&c🔴 Redstone: &f10 XP + 1 Pepita de Oro"));
        meta.setLore(lore);
        spinButton.setItemMeta(meta);
        inventory.setItem(SPIN_BUTTON, spinButton);
    }
    
    public void startSpinAnimation(Player player) {
        Inventory inventory = player.getOpenInventory().getTopInventory();
        if (inventory == null || !inventory.getTitle().contains("Casino")) {
            return;
        }
        
        // Deshabilitar el botón durante la animación
        setSpinButtonDisabled(inventory);
        
        // Reproducir sonido de inicio
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, 0.5f);
        
        new SpinAnimation(player, inventory).runTaskTimer(plugin, 0L, 3L); // Cada 3 ticks (0.15 segundos)
    }
    
    private void setSpinButtonDisabled(Inventory inventory) {
        ItemStack disabledButton = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta meta = disabledButton.getItemMeta();
        meta.setDisplayName(MessageUtils.getColoredMessage("&c&l⏳ GIRANDO... ⏳"));
        List<String> lore = new ArrayList<>();
        lore.add(MessageUtils.getColoredMessage("&7¡La tragamonedas está girando!"));
        lore.add(MessageUtils.getColoredMessage("&7Espera a que termine..."));
        meta.setLore(lore);
        disabledButton.setItemMeta(meta);
        inventory.setItem(SPIN_BUTTON, disabledButton);
    }
    
    private Material getRandomSlotMaterial() {
        return slotMaterials[random.nextInt(slotMaterials.length)];
    }
    
    private String getSlotDisplayName(Material material) {
        switch (material) {
            case DIAMOND:
                return "💎 Diamante";
            case GOLD_INGOT:
                return "🥇 Oro";
            case EMERALD:
                return "💚 Esmeralda";
            case IRON_INGOT:
                return "⚙ Hierro";
            case REDSTONE:
                return "🔴 Redstone";
            default:
                return material.name();
        }
    }
    
    private class SpinAnimation extends BukkitRunnable {
        private final Player player;
        private final Inventory inventory;
        private int tickCount = 0;
        private final int maxTicks = 40; // 2 segundos (40 ticks * 3 = 120 ticks del servidor)
        
        // Materiales finales que se mostrarán
        private final Material finalSlot1;
        private final Material finalSlot2;
        private final Material finalSlot3;
        
        public SpinAnimation(Player player, Inventory inventory) {
            this.player = player;
            this.inventory = inventory;
            this.finalSlot1 = getRandomSlotMaterial();
            this.finalSlot2 = getRandomSlotMaterial();
            this.finalSlot3 = getRandomSlotMaterial();
        }
        
        @Override
        public void run() {
            // Verificar que el jugador sigue teniendo el inventario abierto
            if (!player.isOnline() || !player.getOpenInventory().getTopInventory().equals(inventory)) {
                this.cancel();
                return;
            }
            
            tickCount++;
            
            // Durante la animación, cambiar los slots aleatoriamente
            if (tickCount < maxTicks) {
                // Cambiar los slots con materiales aleatorios
                setSlotItem(inventory, SLOT_1, getRandomSlotMaterial());
                setSlotItem(inventory, SLOT_2, getRandomSlotMaterial());
                setSlotItem(inventory, SLOT_3, getRandomSlotMaterial());
                
                // Reproducir sonido cada ciertos ticks
                if (tickCount % 5 == 0) {
                    player.playSound(player.getLocation(), Sound.CLICK, 0.5f, 1.0f + (tickCount * 0.02f));
                }
            } else {
                // Finalizar animación con los resultados finales
                setSlotItem(inventory, SLOT_1, finalSlot1);
                setSlotItem(inventory, SLOT_2, finalSlot2);
                setSlotItem(inventory, SLOT_3, finalSlot3);
                
                // Restaurar el botón de girar
                setSpinButton(inventory);
                
                // Verificar si ganó
                checkWin(player, finalSlot1, finalSlot2, finalSlot3);
                
                this.cancel();
            }
        }
    }
    
    private void checkWin(Player player, Material slot1, Material slot2, Material slot3) {
        if (slot1 == slot2 && slot2 == slot3) {
            // ¡El jugador ganó!
            RewardManager.giveReward(player, slot1);
            
            // Efectos de victoria
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
            player.sendMessage(MessageUtils.getColoredMessage("&a&l🎉 ¡FELICIDADES! 🎉"));
            player.sendMessage(MessageUtils.getColoredMessage("&e¡Has conseguido 3 " + getSlotDisplayName(slot1) + "&e!"));
            
            // Mensaje a todos los jugadores online
            String winMessage = MessageUtils.getColoredMessage("&6&l🎰 &e" + player.getName() + 
                " &aha ganado en el casino con 3 " + getSlotDisplayName(slot1) + "&a!");
            Bukkit.broadcastMessage(winMessage);
            
        } else {
            // No ganó
            player.playSound(player.getLocation(), Sound.NOTE_BASS, 1.0f, 0.5f);
            player.sendMessage(MessageUtils.getColoredMessage("&c¡No hay suerte esta vez! Inténtalo de nuevo."));
        }
    }
}