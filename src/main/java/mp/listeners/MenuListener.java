package mp.listeners;

import mp.plugin1.MiPlugin;
import mp.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class MenuListener implements Listener {
    private final MiPlugin plugin;
    // Predefined colored titles for plugin menus
    private static final String TITLE_MAIN = MessageUtils.getColoredMessage("&c&lM&6&lE&e&lN&a&lU &b&lP&d&lR&5&lI&4&lN&c&lC&6&lI&e&lP&a&lL");
    private static final String TITLE_NORDIC = MessageUtils.getColoredMessage("&9&lEquipos Nordicos Epicos");
    private static final String TITLE_ALLY = MessageUtils.getColoredMessage("&a&l✦ &fElige tu aliado especial &a&l✦");

    public MenuListener(MiPlugin plugin) {
        this.plugin = plugin;
        // DEBUG: Confirmar que el listener se registra
        Bukkit.getLogger().info("[MiPlugin DEBUG] MenuListener registrado.");
    }

    // Helper para verificar si es un menú del plugin (más robusto)
    private boolean isPluginMenu(InventoryView view) {
        if (view == null || view.getTitle() == null) return false;
        String title = view.getTitle();
        // DEBUG: Mostrar título detectado
        Bukkit.getLogger().info("[MiPlugin DEBUG] isPluginMenu check: '" + title + "'");
        boolean result = TITLE_MAIN.equals(title) || TITLE_NORDIC.equals(title) || TITLE_ALLY.equals(title);
        Bukkit.getLogger().info("[MiPlugin DEBUG] isPluginMenu result: " + result + " for title: '" + title + "'");
        return result;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        InventoryView view = event.getView();

        // DEBUG: Registrar cada clic en cualquier inventario
        Bukkit.getLogger().info("[MiPlugin DEBUG] Click en inventario: '" + (view.getTitle() == null ? "NULL_TITLE" : view.getTitle()) + "' por " + player.getName() + ". Tipo de clic: " + event.getClick().name());

        if (!isPluginMenu(view)) {
            Bukkit.getLogger().info("[MiPlugin DEBUG] No es un menú del plugin (Título: '" + view.getTitle() + "'). IGNORANDO.");
            return;
        }

        Bukkit.getLogger().info("[MiPlugin DEBUG] Es un menú del plugin: '" + view.getTitle() + "'. Cancelando evento.");
        event.setCancelled(true);

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR || !clickedItem.hasItemMeta() || !clickedItem.getItemMeta().hasDisplayName()) {
            Bukkit.getLogger().info("[MiPlugin DEBUG] Click en slot vacío o ítem inválido.");
            return;
        }

        String clickedItemName = clickedItem.getItemMeta().getDisplayName();
        String plainName = org.bukkit.ChatColor.stripColor(clickedItemName).trim();
        String inventoryTitle = view.getTitle();

        Bukkit.getLogger().info("[MiPlugin DEBUG] Título: '" + inventoryTitle + "', Ítem: '" + clickedItemName + "' (Plain: '" + plainName + "')");

        // --- LÓGICA DEL MENÚ PRINCIPAL ---
        if (TITLE_MAIN.equals(inventoryTitle)) {
            Bukkit.getLogger().info("[MiPlugin DEBUG] Entrando a lógica de Menú Principal.");
            if (clickedItemName.contains("INICIAR OLEADAS")) {
                if (!plugin.getWaveManager().isWaveActive()) {
                    player.sendMessage(MessageUtils.getColoredMessage("&a&l⚡ &f¡Iniciando sistema de oleadas...!"));
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.2f);
                    plugin.getWaveManager().startWaveSystem();
                    // Reabrir menú para actualizar estado (después de un pequeño delay para que el mensaje de inicio se muestre)
                    Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getMenuInventoryManager().openMainInventory(player), 20L);
                } else {
                    player.sendMessage(MessageUtils.getColoredMessage("&c&l✗ &f¡Ya hay oleadas activas en este momento!"));
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 1.0f, 0.5f);
                }
            } else if (clickedItemName.contains("EQUIPOS NÓRDICOS")) {
                player.sendMessage(MessageUtils.getColoredMessage("&9&l⚡ &f¡Abriendo selección de dioses nórdicos...!"));
                player.playSound(player.getLocation(), Sound.CLICK, 1.0f, 1.0f);
                plugin.getMenuInventoryManager().openNorseEpicTeamsMenu(player);
            } else if (clickedItemName.contains("DETENER OLEADAS")) {
                if (plugin.getWaveManager().isWaveActive()) {
                    player.sendMessage(MessageUtils.getColoredMessage("&c&l🛑 &f¡Deteniendo sistema de oleadas...!"));
                    player.playSound(player.getLocation(), Sound.ANVIL_LAND, 1.0f, 0.8f);
                    plugin.getWaveManager().stopWaveSystem();
                    Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getMenuInventoryManager().openMainInventory(player), 20L);
                } else {
                    player.sendMessage(MessageUtils.getColoredMessage("&e&l⚠ &f¡No hay oleadas activas para detener!"));
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, 0.8f);
                }
            }
            // --- LÓGICA DEL MENÚ DE EQUIPOS NÓRDICOS ---
        } else if (TITLE_NORDIC.equals(inventoryTitle)) {
            Bukkit.getLogger().info("[MiPlugin DEBUG] Entrando a lógica de Equipos Nórdicos.");
            String kitName = null;
            if (plainName.contains("Odín")) kitName = "odin";
            else if (plainName.contains("Thor")) kitName = "thor";
            else if (plainName.contains("Loki")) kitName = "loki";
            else if (plainName.contains("Freyja")) kitName = "freyja";

            Bukkit.getLogger().info("[MiPlugin DEBUG] KitName detectado: " + kitName);

            if (kitName != null) {
                String diosNombre = clickedItemName.replaceAll("§.", "").replaceAll("\\[|\\]", "").trim().split(" ")[0];
                player.sendMessage(MessageUtils.getColoredMessage(String.format("&%s&l⚡ &f¡Has elegido el poder de &l%s&f!", getKitColor(kitName), diosNombre)));
                Bukkit.getLogger().info("[MiPlugin DEBUG] Llamando a giveNorseKit para " + player.getName() + " con kit " + kitName);
                plugin.getMenuInventoryManager().giveNorseKit(player, kitName);
                player.closeInventory();
                Bukkit.getLogger().info("[MiPlugin DEBUG] Inventario cerrado después de dar kit.");
            } else {
                Bukkit.getLogger().info("[MiPlugin DEBUG] KitName es null, no se dio kit.");
            }
            // --- LÓGICA DEL MENÚ DE ALIADOS ---
        } else if (TITLE_ALLY.equals(inventoryTitle)) {
            Bukkit.getLogger().info("[MiPlugin DEBUG] Entrando a lógica de Menú de Aliados.");
            String allyType = null;
            if (plainName.contains("Gólem de Hierro")) allyType = "golem";
            else if (plainName.contains("Muñeco de Nieve")) allyType = "snowman";

            if (allyType != null) {
                if (plugin.getWaveManager().canChooseAllyEgg(player, allyType)) {
                    plugin.getMenuInventoryManager().spawnAlly(player, allyType);
                    plugin.getWaveManager().registerAllyEggChoice(player, allyType);
                    player.sendMessage(MessageUtils.getColoredMessage(String.format("&%s&l✓ &f¡Has elegido un %s &fcomo aliado!", allyType.equals("golem") ? "a" : "b", clickedItemName.replaceAll("§.", "").trim())));
                    player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 1.0f, 1.5f);
                    plugin.getMenuInventoryManager().openAllyEggMenu(player); // Reabrir para actualizar contador
                } else {
                    player.sendMessage(MessageUtils.getColoredMessage("&c&l✗ &7Ya tienes el máximo de este aliado (2/2)"));
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 1.0f, 0.5f);
                }
            }
        }
    }

    // Helper para color de mensaje de kit
    private String getKitColor(String kitName) {
        switch (kitName) {
            case "odin":
                return "8"; // Gris oscuro
            case "thor":
                return "e"; // Amarillo
            case "loki":
                return "a"; // Verde claro
            case "freyja":
                return "d"; // Rosa claro
            default:
                return "f"; // Blanco
        }
    }

    // --- OTRAS PROTECCIONES DE INVENTARIO (Drag, Move, Shift-Click, Double-Click, Drop, Close) ---
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (isPluginMenu(event.getView())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        boolean sourceIsPluginMenu = false;
        boolean destIsPluginMenu = false;

        if (event.getSource().getHolder() instanceof Player) {
            Player sourcePlayer = (Player) event.getSource().getHolder();
            sourceIsPluginMenu = isPluginMenu(sourcePlayer.getOpenInventory());
        }

        if (event.getDestination().getHolder() instanceof Player) {
            Player destPlayer = (Player) event.getDestination().getHolder();
            destIsPluginMenu = isPluginMenu(destPlayer.getOpenInventory());
        }

        if (sourceIsPluginMenu || destIsPluginMenu) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryShiftClick(InventoryClickEvent event) {
        if (isPluginMenu(event.getView()) && event.isShiftClick()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryDoubleClick(InventoryClickEvent event) {
        if (isPluginMenu(event.getView()) && event.getClick().name().contains("DOUBLE")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryDrop(InventoryClickEvent event) {
        if (isPluginMenu(event.getView()) && (event.getClick() == ClickType.DROP || event.getClick() == ClickType.CONTROL_DROP)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        if (isPluginMenu(event.getView())) {
            ItemStack cursorItem = player.getItemOnCursor();
            if (cursorItem != null && cursorItem.getType() != Material.AIR) {
                player.getInventory().addItem(cursorItem);
                player.setItemOnCursor(null);
                player.sendMessage(MessageUtils.getColoredMessage("&e&l⚠ &7Item devuelto a tu inventario."));
            }
        }
    }
}
