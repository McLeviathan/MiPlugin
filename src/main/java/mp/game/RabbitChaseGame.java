package mp.game;

import mp.plugin1.MiPlugin;
import mp.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class RabbitChaseGame {
    
    private final MiPlugin plugin;
    private final Map<UUID, GameSession> activeSessions;
    private final Random random;
    
    // Configuración del juego
    private static final int GAME_AREA_SIZE = 10; // 10x10 bloques
    private static final int GAME_DURATION = 10; // 10 segundos
    private static final int RABBIT_TELEPORT_INTERVAL = 20; // 1 segundo (20 ticks)
    private static final int INITIAL_PROTECTION_TIME = 40; // 2 segundos de protección inicial (40 ticks)
    
    public RabbitChaseGame(MiPlugin plugin) {
        this.plugin = plugin;
        this.activeSessions = new HashMap<>();
        this.random = new Random();
    }
    
    /**
     * Inicia un nuevo juego para el jugador
     */
    public boolean startGame(Player player) {
        // Verificar si el jugador ya tiene un juego activo
        if (activeSessions.containsKey(player.getUniqueId())) {
            player.sendMessage(MessageUtils.getColoredMessage("&c¡Ya tienes un juego de conejo activo!"));
            return false;
        }
        
        // Encontrar una posición segura para el juego
        Location gameCenter = findSafeGameLocation(player);
        if (gameCenter == null) {
            player.sendMessage(MessageUtils.getColoredMessage("&cNo se pudo encontrar un área segura para el juego cerca de tu posición."));
            return false;
        }
        
        // Teletransportar al jugador al centro del área de juego
        player.teleport(gameCenter);
        
        // Encontrar una posición inicial para el conejo (lejos del jugador)
        Location rabbitSpawnLocation = findInitialRabbitLocation(gameCenter);
        
        // Crear y configurar el conejo
        Rabbit rabbit = (Rabbit) gameCenter.getWorld().spawnEntity(rabbitSpawnLocation, EntityType.RABBIT);
        rabbit.setCustomName(MessageUtils.getColoredMessage("&a&l¡Atrápame!"));
        rabbit.setCustomNameVisible(true);
        rabbit.setRemoveWhenFarAway(false); // Evitar que desaparezca por distancia
        
        // Crear sesión de juego
        GameSession session = new GameSession(player, rabbit, gameCenter);
        activeSessions.put(player.getUniqueId(), session);
        
        // Mensajes de inicio
        player.sendMessage(MessageUtils.getColoredMessage("&6&l🐰 ¡MINIJUEGO DEL CONEJO! 🐰"));
        player.sendMessage(MessageUtils.getColoredMessage("&e¡Tienes &c" + GAME_DURATION + " segundos &epara golpear al conejo!"));
        player.sendMessage(MessageUtils.getColoredMessage("&7El conejo se moverá cada segundo..."));
        player.sendMessage(MessageUtils.getColoredMessage("&c⏳ Preparándose... &7¡Espera 2 segundos!"));
        
        // Efectos de sonido
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, 1.5f);
        
        // Iniciar la lógica del juego
        session.startGameLogic();
        
        return true;
    }
    
    /**
     * Maneja cuando un jugador golpea al conejo
     */
    public boolean handleRabbitHit(Player player, Rabbit rabbit) {
        GameSession session = activeSessions.get(player.getUniqueId());
        if (session == null || !session.rabbit.equals(rabbit)) {
            return false; // No es el conejo de este jugador o no tiene juego activo
        }
        
        // Verificar si la protección inicial está activa
        if (session.protectionActive) {
            player.sendMessage(MessageUtils.getColoredMessage("&c¡Espera un momento! El conejo aún está preparándose..."));
            return true; // Cancelar el golpe pero confirmar que es el conejo del juego
        }
        
        // El jugador ganó
        session.endGame(true);
        return true;
    }
    
    /**
     * Termina el juego de un jugador (usado cuando se desconecta o por otros motivos)
     */
    public void endPlayerGame(Player player) {
        GameSession session = activeSessions.get(player.getUniqueId());
        if (session != null) {
            session.endGame(false);
        }
    }
    
    /**
     * Encuentra una ubicación segura para el juego cerca del jugador
     */
    private Location findSafeGameLocation(Player player) {
        Location playerLoc = player.getLocation();
        World world = playerLoc.getWorld();
        
        // Intentar encontrar una posición segura en un radio de 10 bloques
        for (int attempts = 0; attempts < 20; attempts++) {
            double offsetX = (random.nextDouble() - 0.5) * 20; // -10 a +10
            double offsetZ = (random.nextDouble() - 0.5) * 20; // -10 a +10
            
            int x = (int) (playerLoc.getX() + offsetX);
            int z = (int) (playerLoc.getZ() + offsetZ);
            
            // Obtener la altura más alta en esa posición
            int y = world.getHighestBlockYAt(x, z);
            Location testLoc = new Location(world, x + 0.5, y + 1, z + 0.5);
            
            // Verificar que sea una posición segura
            if (isSafeLocation(testLoc)) {
                return testLoc;
            }
        }
        
        // Si no se encuentra una posición segura, usar la posición actual del jugador
        Location fallback = playerLoc.clone();
        fallback.setY(world.getHighestBlockYAt(fallback) + 1);
        return fallback;
    }
    
    /**
     * Verifica si una ubicación es segura para el juego
     */
    private boolean isSafeLocation(Location loc) {
        World world = loc.getWorld();
        
        // Verificar que el bloque debajo sea sólido
        Material blockBelow = world.getBlockAt(loc.clone().subtract(0, 1, 0)).getType();
        if (!blockBelow.isSolid()) {
            return false;
        }
        
        // Verificar que no haya bloques peligrosos cerca
        Material blockAt = world.getBlockAt(loc).getType();
        Material blockAbove = world.getBlockAt(loc.clone().add(0, 1, 0)).getType();
        
        return blockAt == Material.AIR && blockAbove == Material.AIR;
    }
    
    /**
     * Encuentra una posición inicial para el conejo, asegurando que esté lejos del jugador
     */
    private Location findInitialRabbitLocation(Location gameCenter) {
        // Intentar encontrar una posición que esté al menos a 3 bloques del centro (jugador)
        for (int attempts = 0; attempts < 15; attempts++) {
            Location testLoc = getRandomLocationInArea(gameCenter);
            
            // Verificar que esté al menos a 3 bloques de distancia del centro
            if (testLoc.distance(gameCenter) >= 3.0) {
                return testLoc;
            }
        }
        
        // Si no se encuentra una posición lejana, forzar una en el borde del área
        double angle = random.nextDouble() * 2 * Math.PI; // Ángulo aleatorio
        double distance = GAME_AREA_SIZE / 2.0 - 1; // Cerca del borde pero dentro del área
        
        double offsetX = Math.cos(angle) * distance;
        double offsetZ = Math.sin(angle) * distance;
        
        int x = (int) (gameCenter.getX() + offsetX);
        int z = (int) (gameCenter.getZ() + offsetZ);
        int y = gameCenter.getWorld().getHighestBlockYAt(x, z);
        
        return new Location(gameCenter.getWorld(), x + 0.5, y + 1, z + 0.5);
    }
    
    /**
     * Obtiene una posición aleatoria dentro del área de juego
     */
    private Location getRandomLocationInArea(Location center) {
        double offsetX = (random.nextDouble() - 0.5) * GAME_AREA_SIZE;
        double offsetZ = (random.nextDouble() - 0.5) * GAME_AREA_SIZE;
        
        int x = (int) (center.getX() + offsetX);
        int z = (int) (center.getZ() + offsetZ);
        int y = center.getWorld().getHighestBlockYAt(x, z);
        
        return new Location(center.getWorld(), x + 0.5, y + 1, z + 0.5);
    }
    
    /**
     * Clase interna para manejar una sesión de juego individual
     */
    private class GameSession {
        private final Player player;
        private final Rabbit rabbit;
        private final Location gameCenter;
        private BukkitRunnable gameTask;
        private BukkitRunnable teleportTask;
        private int secondsRemaining;
        private boolean protectionActive;
        
        public GameSession(Player player, Rabbit rabbit, Location gameCenter) {
            this.player = player;
            this.rabbit = rabbit;
            this.gameCenter = gameCenter;
            this.secondsRemaining = GAME_DURATION;
            this.protectionActive = true; // Iniciar con protección activa
        }
        
        public void startGameLogic() {
            // Desactivar protección inicial después de 2 segundos
            new BukkitRunnable() {
                @Override
                public void run() {
                    protectionActive = false;
                    if (player.isOnline()) {
                        player.sendMessage(MessageUtils.getColoredMessage("&a&l¡COMIENZA LA CAZA! &7¡Atrapa al conejo!"));
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, 2.0f);
                    }
                }
            }.runTaskLater(plugin, INITIAL_PROTECTION_TIME);
            
            // Tarea principal del juego (cuenta regresiva)
            gameTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!player.isOnline() || rabbit.isDead()) {
                        endGame(false);
                        return;
                    }
                    
                    secondsRemaining--;
                    
                    // Mostrar tiempo restante
                    if (secondsRemaining <= 5 && secondsRemaining > 0) {
                        player.sendMessage(MessageUtils.getColoredMessage("&c&l" + secondsRemaining + "..."));
                        player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1.0f, 1.0f);
                    }
                    
                    if (secondsRemaining <= 0) {
                        // Tiempo agotado - el jugador perdió
                        endGame(false);
                    }
                }
            };
            gameTask.runTaskTimer(plugin, 20L, 20L); // Cada segundo
            
            // Tarea de teletransporte del conejo
            teleportTask = new BukkitRunnable() {
                @Override
                public void run() {
                    if (!player.isOnline() || rabbit.isDead()) {
                        this.cancel();
                        return;
                    }
                    
                    // Teletransportar el conejo a una nueva posición
                    Location newLoc = getRandomLocationInArea(gameCenter);
                    rabbit.teleport(newLoc);
                    
                    // Efecto de sonido
                    player.playSound(newLoc, Sound.ENDERMAN_TELEPORT, 0.5f, 1.5f);
                }
            };
            teleportTask.runTaskTimer(plugin, RABBIT_TELEPORT_INTERVAL, RABBIT_TELEPORT_INTERVAL);
        }
        
        public void endGame(boolean playerWon) {
            // Cancelar tareas
            if (gameTask != null) {
                gameTask.cancel();
            }
            if (teleportTask != null) {
                teleportTask.cancel();
            }
            
            // Eliminar conejo
            if (rabbit != null && !rabbit.isDead()) {
                rabbit.remove();
            }
            
            // Remover sesión
            activeSessions.remove(player.getUniqueId());
            
            if (!player.isOnline()) {
                return; // No enviar mensajes si el jugador no está online
            }
            
            if (playerWon) {
                // Victoria
                player.sendMessage(MessageUtils.getColoredMessage("&a&l🎉 ¡FELICIDADES! 🎉"));
                player.sendMessage(MessageUtils.getColoredMessage("&e¡Has atrapado al conejo!"));
                
                // Recompensas
                player.giveExp(25);
                player.sendMessage(MessageUtils.getColoredMessage("&b&l+ &f25 XP de recompensa"));
                
                // Efectos de victoria
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
                
                // Mensaje broadcast
                String winMessage = MessageUtils.getColoredMessage("&6&l🐰 &e" + player.getName() + 
                    " &aha atrapado al conejo en el minijuego!");
                Bukkit.broadcastMessage(winMessage);
                
            } else {
                // Derrota
                player.sendMessage(MessageUtils.getColoredMessage("&c&l😅 ¡El conejo escapó!"));
                player.sendMessage(MessageUtils.getColoredMessage("&7¡Inténtalo de nuevo la próxima vez!"));
                player.playSound(player.getLocation(), Sound.NOTE_BASS, 1.0f, 0.5f);
            }
        }
    }
    
    /**
     * Limpia todas las sesiones activas (usado al deshabilitar el plugin)
     */
    public void cleanup() {
        for (GameSession session : activeSessions.values()) {
            session.endGame(false);
        }
        activeSessions.clear();
    }
}