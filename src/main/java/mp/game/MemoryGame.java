package mp.game;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Clase principal que maneja el juego de memoria visual
 */
public class MemoryGame {
    private final JavaPlugin plugin;
    private final List<Block> gameBlocks = new ArrayList<>(); // Lista de bloques del juego
    private final List<Material> colors = new ArrayList<>(); // Colores disponibles
    
    // Coordenadas exactas para el juego
    private static final String GAME_WORLD = "world"; // Nombre del mundo donde se jugará
    private static final int PLAYER_X = -190;
    private static final int PLAYER_Y = 4; // Jugador en el suelo base (mundo plano)
    private static final int PLAYER_Z = -1191;
    private static final int BOARD_CENTER_X = -190;
    private static final int BOARD_CENTER_Y = 5; // Base del tablero a 1 bloque sobre el suelo
    private static final int BOARD_CENTER_Z = PLAYER_Z - 4; // 4 bloques adelante del jugador
    private static final int BOARD_WIDTH = 3;
    private static final int BOARD_HEIGHT = 3;
    
    // Espaciado entre bloques
    private static final int BLOCK_SPACING = 1; // 1 bloque de separación entre cada bloque
    
    // Rotación del jugador
    private static final float PLAYER_YAW = 180; // Mirando hacia el sur
    private static final float PLAYER_PITCH = 0; // Mirando horizontalmente
    private final List<Integer> sequence = new ArrayList<>(); // Secuencia de colores a memorizar
    private final List<Integer> playerSequence = new ArrayList<>(); // Secuencia del jugador
    private final Random random = new Random();
    private int currentStep = 0; // Paso actual del juego
    private boolean isPlaying = false; // Estado del juego
    private Player currentPlayer; // Jugador actual
    private int wins = 0; // Contador de victorias
    private int losses = 0; // Contador de derrotas
    private Location gameLocation; // Ubicación del juego

    /**
     * Constructor del juego
     * @param plugin Instancia del plugin
     */
    public MemoryGame(JavaPlugin plugin) {
        this.plugin = plugin;
        // Inicializar colores disponibles
        colors.add(Material.WOOL); // Blanco
        colors.add(Material.WOOL); // Rojo (usando data)
        colors.add(Material.WOOL); // Verde (usando data)
        colors.add(Material.WOOL); // Azul (usando data)
        colors.add(Material.WOOL); // Amarillo (usando data)
    }

    /**
     * Inicia un nuevo juego para un jugador
     * @param player Jugador que inicia el juego
     */
    public void startGame(Player player) {
        if (isPlaying) {
            player.sendMessage("§cYa hay un juego en curso!");
            return;
        }

        currentPlayer = player;
        isPlaying = true;
        currentStep = 0;
        sequence.clear();
        playerSequence.clear();
        
        // Guardar ubicación original del jugador
        originalLocation = player.getLocation();

        // Crear plataforma de piedra 3x3 bajo el jugador en y=63
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                Block platformBlock = plugin.getServer().getWorld(GAME_WORLD)
                    .getBlockAt(PLAYER_X + dx, PLAYER_Y - 1, PLAYER_Z + dz);
                platformBlock.setType(Material.STONE);
            }
        }

        // Teletransportar al jugador exactamente a la posición base, parado en el suelo y mirando al sur
        Location spawnLoc = new Location(
            plugin.getServer().getWorld(GAME_WORLD),
            PLAYER_X,
            PLAYER_Y,
            PLAYER_Z,
            PLAYER_YAW, // Mirando al sur
            PLAYER_PITCH // Mirando horizontal
        );
        player.teleport(spawnLoc);

        // Generar el tablero 3x3 de lana en la posición exacta, recreando la estructura cada vez
        gameLocation = new Location(
            plugin.getServer().getWorld(GAME_WORLD),
            BOARD_CENTER_X,
            BOARD_CENTER_Y,
            BOARD_CENTER_Z
        );
        createGameGrid();

        // Mensaje informativo
        player.sendMessage("§eMira hacia las lanas y memoriza la secuencia");

        // Generar secuencia de 5 pasos
        for (int i = 0; i < 5; i++) {
            sequence.add(random.nextInt(9)); // Número entre 0 y 8 para la posición en la grilla
        }



        // Asegurarse de que el jugador mire hacia las lanas
        player.sendMessage("§eMira hacia las lanas y memoriza la secuencia");

        // Mostrar contador de victorias/derrotas
        player.sendMessage("§eTus estadísticas:");
        player.sendMessage("§aVictorias: " + wins);
        player.sendMessage("§cDerrotas: " + losses);

        // Mostrar secuencia
        showSequence();
    }

    /**
     * Crea la grilla 3x3 de bloques para el juego
     */
    /**
     * Genera el tablero 3x3 de bloques de lana en la posición exacta y orientación correcta.
     * Siempre recrea la estructura, eliminando cualquier modificación previa.
     */
    private void createGameGrid() {
        gameBlocks.clear();
        int baseY = BOARD_CENTER_Y; // 5
        int baseZ = BOARD_CENTER_Z; // 4 bloques adelante del jugador
        // El tablero es una "pared" vertical de 3x3, centrada en X, yendo de abajo hacia arriba
        for (int y = 0; y < BOARD_HEIGHT; y++) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                Block block = plugin.getServer().getWorld(GAME_WORLD)
                    .getBlockAt(BOARD_CENTER_X + (x - 1), baseY + y, baseZ);
                block.setType(Material.WOOL);
                block.setData((byte) 0); // Blanco por defecto
                gameBlocks.add(block);
            }
        }
    }

    /**
     * Muestra la secuencia de colores
     */
    private void showSequence() {
        new BukkitRunnable() {
            int index = 0;

            @Override
            public void run() {
                if (index < sequence.size()) {
                    int position = sequence.get(index);
                    int x = position % 3;
                    int y = position / 3;
                    
                    // Convertir a índice en la lista de bloques
                    int blockIndex = y * 3 + x;
                    Block block = gameBlocks.get(blockIndex);
                    
                    // Cambiar color del bloque
                    // Usar data para los colores (1-15)
                    int colorData = 1 + random.nextInt(15);
                    block.setType(Material.WOOL);
                    block.setData((byte) colorData); // Establecer color usando data
                    
                    index++;
                } else {
                    this.cancel();
                    // Esperar 2 segundos antes de borrar la secuencia
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            hideSequence();
                        }
                    }.runTaskLater(plugin, 40); // 2 segundos
                }
            }
        }.runTaskTimer(plugin, 0, 20); // Cada segundo
    }

    /**
     * Oculta la secuencia, volviendo todos los bloques a blanco
     */
    private void hideSequence() {
        for (Block block : gameBlocks) {
            block.setType(Material.WOOL); // Blanco
        }
    }

    /**
     * Maneja el clic del jugador en un bloque
     * @param block Bloque clickeado
     */
    public void handleBlockClick(Block block) {
        if (!isPlaying || !gameBlocks.contains(block)) {
            return;
        }

        int index = gameBlocks.indexOf(block);
        playerSequence.add(index);

        // Cambiar color temporalmente al bloque clickeado
        block.setType(Material.WOOL);
        block.setData((byte) 3); // Azul claro usando data
        
        // Restaurar color después de 0.5 segundos
        new BukkitRunnable() {
            @Override
            public void run() {
                block.setType(Material.WOOL); // Blanco
            }
        }.runTaskLater(plugin, 10);

        // Verificar si la secuencia está completa
        if (playerSequence.size() == sequence.size()) {
            checkSequence();
        }
    }

    /**
     * Verifica si la secuencia del jugador es correcta
     */
    private Location originalLocation; // Para guardar la ubicación original del jugador

    private void checkSequence() {
        // Verificar si la secuencia es correcta
        boolean isCorrect = sequence.equals(playerSequence);
        
        // Actualizar contador
        if (isCorrect) {
            wins++;
        } else {
            losses++;
        }

        // Mensaje de resultado
        String message = isCorrect ? "§a¡Correcto! Has acertado la secuencia." : "§cIncorrecto. La secuencia no coincide.";
        currentPlayer.sendMessage(message);

        // Mostrar estadísticas actualizadas
        currentPlayer.sendMessage("§eTus estadísticas:");
        currentPlayer.sendMessage("§aVictorias: " + wins);
        currentPlayer.sendMessage("§cDerrotas: " + losses);

        // Feedback visual para la respuesta
        new BukkitRunnable() {
            @Override
            public void run() {
                // Cambiar color de todos los bloques según el resultado
                byte colorData = isCorrect ? (byte) 5 : (byte) 14; // Verde para correcto, Rojo para incorrecto
                for (Block block : gameBlocks) {
                    block.setType(Material.WOOL);
                    block.setData(colorData);
                }
            }
        }.runTask(plugin);

        // Restaurar bloques a blanco después de 0.5 segundos
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Block block : gameBlocks) {
                    block.setType(Material.WOOL);
                    block.setData((byte) 0); // Blanco
                }
            }
        }.runTaskLater(plugin, 10);

        // Limpiar juego después de 1 segundo
        new BukkitRunnable() {
            @Override
            public void run() {
                cleanup();
                
                // Teletransportar al jugador de vuelta a su ubicación original
                if (originalLocation != null) {
                    currentPlayer.teleport(originalLocation);
                }
                
                currentPlayer.sendMessage("§eJuego terminado. Escribe /memorygame para jugar de nuevo.");
            }
        }.runTaskLater(plugin, 20);
    }

    /**
     * Limpia el juego y elimina la grilla
     */
    public void cleanup() {
        if (!gameBlocks.isEmpty()) {
            // Primero hacer que los bloques parpadeen
            for (Block block : gameBlocks) {
                block.setType(Material.WOOL);
                block.setData((byte) 11); // Azul claro
            }
            
            // Esperar un momento y luego eliminar
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Block block : gameBlocks) {
                        block.setType(Material.AIR); // Eliminar bloques
                    }
                    gameBlocks.clear();
                    isPlaying = false;
                }
            }.runTaskLater(plugin, 10);
        } else {
            isPlaying = false;
        }
    }
}
