package mp.miPlugin.commands;

import mp.plugin1.MiPlugin;
import mp.utils.MessageUtils;
import mp.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainCommands implements CommandExecutor {
    private MiPlugin plugin;

    public MainCommands(MiPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        boolean isConsole = !(sender instanceof Player);

        // /miPlugin args [0] args [1] args [2]
        if (args.length >= 1) {
            Player player = null;
            if (args[0].equalsIgnoreCase("Bienvenido")) {
                // /miPlugin Bienvenido
                if (!sender.hasPermission("miPlugin.get")) {
                    sendMessage(sender, MiPlugin.prefix + "&cNo tienes permisos para usar este comando");
                    return true;
                }

                if (isConsole) {
                    sendMessage(sender, MiPlugin.prefix + "&7Bienvenido &aConsola");
                } else {
                    player = (Player) sender;
                    sendMessage(sender, MiPlugin.prefix + "&7Bienvenido &a" + player.getName());
                }
            } else if (args[0].equalsIgnoreCase("Fecha")) {
                // /miPlugin Fecha
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String date = dateFormat.format(new Date());
                sendMessage(sender, MiPlugin.prefix + "&7Fecha y hora actual: &e" + date);
            } else if (args[0].equalsIgnoreCase("Autor")) {
                // /miPlugin Autor
                String authors = String.join(", ", plugin.getDescription().getAuthors());
                sendMessage(sender, MiPlugin.prefix + "&7Los autores del plugin son: &e" + authors);
            } else if (args[0].equalsIgnoreCase("get")) {
                // /miPlugin get <autor/version>
                subCommandGet(sender, args);
            } else if (args[0].equalsIgnoreCase("reload")) {
                // mi plugin reload
                subCommandReload(sender);
            } else if (args[0].equalsIgnoreCase("item")) {
                // /miplugin item (opcional -cantidad-)
                if (isConsole) {
                    sendMessage(sender, MiPlugin.prefix + "&cEste comando solo puede ser usado por jugadores.");
                    return true;
                }
                player = (Player) sender;
                subCommanditem(player, args);
            } else if (args[0].equalsIgnoreCase("sword")) {
                // /miplugin sword
                if (isConsole) {
                    sendMessage(sender, MiPlugin.prefix + "&cEste comando solo puede ser usado por jugadores.");
                    return true;
                }
                player = (Player) sender;
                subCommandSword(player);
            } else if (args[0].equalsIgnoreCase("test")) {
                // /miplugin test - Comando de prueba para verificar funcionamiento
                if (isConsole) {
                    sendMessage(sender, MiPlugin.prefix + "&cEste comando solo puede ser usado por jugadores.");
                    return true;
                }
                player = (Player) sender;
                subCommandTest(player);
            }
        } else {
            // /miPlugin sin argumentos
            help(sender);
        }

        // Solo mostrar mensaje si el comando fue ejecutado correctamente
        if (args.length == 0 || (
                args[0].equalsIgnoreCase("Bienvenido") ||
                args[0].equalsIgnoreCase("Fecha") ||
                args[0].equalsIgnoreCase("Autor") ||
                args[0].equalsIgnoreCase("get") ||
                args[0].equalsIgnoreCase("reload") ||
                args[0].equalsIgnoreCase("item") ||
                        args[0].equalsIgnoreCase("sword") ||
                        args[0].equalsIgnoreCase("test")
            )) {
            sendMessage(sender, MiPlugin.prefix + "&aAcabas de usar el comando &7/miPlugin");
        }
        return true;
    }

    public void help(CommandSender sender) {
        sendMessage(sender, "&f&l----- COMANDOS &c&lMIPLUGIN&c&l2&f&l -----");
        sendMessage(sender, "&7- /miPlugin Bienvenido");
        sendMessage(sender, "&7- /miPlugin Fecha");
        sendMessage(sender, "&7- /miPlugin Autor");
        sendMessage(sender, "&7- /miPlugin get <autor/version>");
        sendMessage(sender, "&7- /miPlugin reload");
        sendMessage(sender, "&7- /miPlugin item [cantidad]");
        sendMessage(sender, "&7- /miPlugin sword");
        sendMessage(sender, "&7- /miPlugin test &8(Debug)");
        sendMessage(sender, "&a&l➤ /menu &7- Menú principal de oleadas");
        sendMessage(sender, "&f&l----- COMANDOS &c&lMIPLUGIN&c&l2&f&l -----");
    }

    public void subCommandGet(CommandSender sender, String[] args) {
        if (!sender.hasPermission("miPlugin.get")) {
            sendMessage(sender, MiPlugin.prefix + "&cNo tienes permisos para usar este comando");
            return;
        }

        if (args.length == 1) {
            // /miPlugin get
            sendMessage(sender, MiPlugin.prefix + "&cDebes usar&7 /miPlugin get <autor/version>");
            return;
        }
        if (args[1].equalsIgnoreCase("autor")) {
            // /miPlugin get autor
            String authors = String.join(", ", plugin.getDescription().getAuthors());
            sendMessage(sender, MiPlugin.prefix + "&7Los autores del plugin son: &e" + authors);
        } else if (args[1].equalsIgnoreCase("version")) {
            // /miPlugin get version
            sendMessage(sender, MiPlugin.prefix + "&7La version del Plugin es: &e" + plugin.getDescription().getVersion());
        } else {
            sendMessage(sender, MiPlugin.prefix + "&cDebes usar&7 /miPlugin get <autor/version>");
        }
    }

    public void subCommandReload(CommandSender sender) {
        if (!sender.hasPermission("miPlugin.commands.reload")) {
            sendMessage(sender, MiPlugin.prefix + "&cNo tienes permisos para usar este comando");
            return;
        }
        plugin.getMainConfigManager().reloadConfig();
        sendMessage(sender, MiPlugin.prefix + "&aEl archivo de configuración ha sido recargado correctamente");
    }

    public void subCommanditem(Player player,String[] args) {
        if (!player.hasPermission("miPlugin.commands.item")) {
            player.sendMessage(MessageUtils.getColoredMessage(MiPlugin.prefix + "&cNo tienes permisos para usar este comando"));
            return;
        }
        int amount = 1;
        if(args.length >= 2){
            try{
                amount = Integer.parseInt(args[1]);
                if(amount <= 0){
                    player.sendMessage(MessageUtils.getColoredMessage(MiPlugin.prefix + "&cUsa una cantidad valida"));
                    return;
                }
            }catch (NumberFormatException e){
                player.sendMessage(MessageUtils.getColoredMessage(MiPlugin.prefix + "&cUsa una cantidad valida"));
                return;
            }
        }
        if(player.getInventory().firstEmpty() == -1){
            player.sendMessage(MessageUtils.getColoredMessage(MiPlugin.prefix + "&cTienes el Inventario lleno"));
            return;
        } else {
            // Corregido: nombre de clase y método correctos
            ItemStack item = ItemUtils.generateEsmeralditem(amount);
            player.getInventory().addItem(item);
            player.sendMessage(MessageUtils.getColoredMessage(MiPlugin.prefix + "&aItem recibido"));
            return;
        }
    }

    public void subCommandSword(Player player) {
        if (!player.hasPermission("miPlugin.commands.sword")) {
            player.sendMessage(MessageUtils.getColoredMessage(MiPlugin.prefix + "&cNo tienes permisos para usar este comando"));
            return;
        }
        if(player.getInventory().firstEmpty() == -1){
            player.sendMessage(MessageUtils.getColoredMessage(MiPlugin.prefix + "&cTienes el Inventario lleno"));
            return;
        } else {
            ItemStack sword = ItemUtils.generateEpicSword();
            player.getInventory().addItem(sword);
            player.sendMessage(MessageUtils.getColoredMessage(MiPlugin.prefix + "&aEspada épica recibida"));
            return;
        }
    }

    public void subCommandTest(Player player) {
        player.sendMessage(MessageUtils.getColoredMessage("&8&l▬▬▬▬▬▬▬ &6&lTEST DEL SISTEMA &8&l▬▬▬▬▬▬▬"));

        // Estado del WaveManager
        boolean isActive = plugin.getWaveManager().isWaveActive();
        int currentWave = plugin.getWaveManager().getCurrentWave();
        int zombiesAlive = plugin.getWaveManager().getZombiesAlive();

        player.sendMessage(MessageUtils.getColoredMessage("&7➤ Sistema de oleadas: " + (isActive ? "&aACTIVO" : "&cINACTIVO")));
        player.sendMessage(MessageUtils.getColoredMessage("&7➤ Oleada actual: &e" + currentWave));
        player.sendMessage(MessageUtils.getColoredMessage("&7➤ Enemigos vivos: &c" + zombiesAlive));

        // Estado de aliados del jugador
        int golems = plugin.getWaveManager().getPlayerAllyCount(player, "golem");
        int snowmen = plugin.getWaveManager().getPlayerAllyCount(player, "snowman");
        player.sendMessage(MessageUtils.getColoredMessage("&7➤ Tus gólems: &e" + golems + "/2"));
        player.sendMessage(MessageUtils.getColoredMessage("&7➤ Tus muñecos: &e" + snowmen + "/2"));

        // Probar abrir menú
        player.sendMessage(MessageUtils.getColoredMessage("&a&l✓ &fAbriendo menú principal para prueba..."));
        plugin.getMenuInventoryManager().openMainInventory(player);

        player.sendMessage(MessageUtils.getColoredMessage("&8&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
    }

    /**
     * Envía un mensaje formateado al remitente, ya sea un jugador o la consola
     * @param sender El remitente del comando
     * @param message El mensaje a enviar
     */
    private void sendMessage(CommandSender sender, String message) {
        if (sender instanceof Player) {
            sender.sendMessage(MessageUtils.getColoredMessage(message));
        } else {
            // Para la consola, usamos el método de consola del servidor
            Bukkit.getServer().getConsoleSender().sendMessage(MessageUtils.getColoredMessage(message));
        }
    }
}
