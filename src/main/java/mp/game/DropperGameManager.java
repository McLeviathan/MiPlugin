package mp.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Gestiona el minijuego Dropper: generación de bloques trampa y estado del jugador.
 * Compatible con Spigot 1.8.8 y Java 8.
 */
public class DropperGameManager {
    // Coordenadas y área de caída
    private static final String GAME_WORLD = "world";
    private static final int START_X = -156;
    private static final int START_Y = 31;
    private static final int START_Z = -1220;
    private static final int MIN_X = -157;
    private static final int MAX_X = -155;
    private static final int MIN_Z = -1221;
    private static final int MAX_Z = -1219;
    private static final int MIN_Y = 13;
    private static final int MAX_Y = 30;
    private static final int SLIME_Y = 10;
    private static final int TRAP_MIN = 3;
    private static final int TRAP_MAX = 7;
    private static final int SAFE_BLOCK_X = START_X;
    private static final int SAFE_BLOCK_Z = START_Z;
    private static final Material SAFE_BLOCK = Material.SLIME_BLOCK;

    // Estado del jugador
    private UUID playingPlayer = null;
    private Location playerStartLocation = null;
    private final List<Block> trapBlocks = new ArrayList<>();

    /**
     * Returns the list of trap blocks in the game.
     * @return List of trap blocks
     */
    public List<Block> getTrapBlocks() {
        return Collections.unmodifiableList(trapBlocks);
    }

    /**
     * Adds a new trap block to the game.
     * @param block The trap block to add
     */
    public void addTrapBlock(Block block) {
        trapBlocks.add(block);
    }

    /**
     * Clears all trap blocks from the game.
     */
    public void clearTrapBlocks() {
        trapBlocks.clear();
    }
    // Puntaje por jugador
    private final Map<UUID, Integer> playerPoints = new HashMap<>();

    /**
     * Returns the current points for a player.
     * @param player The player to check
     * @return Current points
     */
    public int getPlayerPoints(Player player) {
        return playerPoints.getOrDefault(player.getUniqueId(), 0);
    }

    /**
     * Adds points to a player's score.
     * @param player The player
     * @param points Points to add
     */
    public void addPlayerPoints(Player player, int points) {
        UUID playerId = player.getUniqueId();
        int currentPoints = playerPoints.getOrDefault(playerId, 0);
        playerPoints.put(playerId, currentPoints + points);
    }

    /**
     * Resets a player's score.
     * @param player The player
     */
    public void resetPlayerPoints(Player player) {
        playerPoints.remove(player.getUniqueId());
    }

    /**
     * Inicia el minijuego para el jugador dado.
     * @param player Jugador que ejecuta el comando
     */
    public void startForPlayer(Player player) {
        if (playingPlayer != null) {
            player.sendMessage("§c¡Ya hay un jugador jugando Dropper!");
            return;
        }
        World world = Bukkit.getWorld(GAME_WORLD);
        if (world == null) {
            player.sendMessage("§cNo se encontró el mundo '" + GAME_WORLD + "'.");
            return;
        }
        // Guardar ubicación original
        playerStartLocation = player.getLocation();
        playingPlayer = player.getUniqueId();

        // Crear plataforma 3x3 de piedra FUERA del túnel, por ejemplo en z = MIN_Z - 1 (norte exterior)
        int platformZ = MIN_Z - 1;
        for (int x = MIN_X; x <= MAX_X; x++) {
            Block platformBlock = world.getBlockAt(x, START_Y, platformZ);
            platformBlock.setType(Material.STONE);
        }
        // Crear plataforma de agua en la base del área de caída (evita rebotes)
        for (int x = MIN_X; x <= MAX_X; x++) {
            for (int z = MIN_Z; z <= MAX_Z; z++) {
                Block waterBlock = world.getBlockAt(x, SLIME_Y, z);
                waterBlock.setType(Material.WATER);
            }
        }
        // Teletransportar al jugador al centro de la plataforma, mirando al sur hacia el túnel
        Location startLoc = new Location(world, START_X + 0.5, START_Y + 1, platformZ + 0.5, 180, 0);
        player.teleport(startLoc);
        player.sendMessage("§e¡Dropper iniciado! Esquiva los bloques de colores y aterriza en el agua.");

        // Generar bloques trampa
        spawnTrapBlocks(world);
    }

    /**
     * Genera entre 3 y 7 bloques trampa de lana de color aleatorio en posiciones aleatorias dentro del área de caída.
     * @param world Mundo donde se generan los bloques
     */
    private void spawnTrapBlocks(World world) {
        // Limpiar bloques trampa anteriores
        for (Block b : trapBlocks) {
            b.setType(Material.AIR);
        }
        trapBlocks.clear();
        Random random = new Random();
        int trapCount = TRAP_MIN + random.nextInt(TRAP_MAX - TRAP_MIN + 1);
        for (int i = 0; i < trapCount; i++) {
            int x = MIN_X + random.nextInt(MAX_X - MIN_X + 1);
            int y = MIN_Y + random.nextInt(MAX_Y - MIN_Y + 1);
            int z = MIN_Z + random.nextInt(MAX_Z - MIN_Z + 1);
            Block block = world.getBlockAt(x, y, z);
            block.setType(Material.WOOL);
            block.setData((byte) random.nextInt(16)); // Color aleatorio (0-15)
            trapBlocks.add(block);
        }
    }

    /**
     * Devuelve si el jugador está jugando actualmente.
     */
    public boolean isPlaying(Player player) {
        return playingPlayer != null && playingPlayer.equals(player.getUniqueId());
    }

    /**
     * Devuelve la ubicación de inicio del jugador (para reinicio o retorno).
     */
    public Location getPlayerStartLocation() {
        return playerStartLocation;
    }

    /**
     * Marca el fin de la partida (por perder o ganar).
     */
    public void endGame() {
        playingPlayer = null;
        playerStartLocation = null;
        // Limpiar bloques trampa
        for (Block b : trapBlocks) {
            b.setType(Material.AIR);
        }
        trapBlocks.clear();
    }

    /**
     * Suma un punto al jugador y devuelve el puntaje actual.
     */
    public int addPoint(Player player) {
        UUID uuid = player.getUniqueId();
        int points = playerPoints.containsKey(uuid) ? playerPoints.get(uuid) : 0;
        points++;
        playerPoints.put(uuid, points);
        return points;
    }

    /**
     * Devuelve el puntaje actual del jugador.
     */
    public int getPoints(Player player) {
        UUID uuid = player.getUniqueId();
        return playerPoints.containsKey(uuid) ? playerPoints.get(uuid) : 0;
    }

    /**
     * Devuelve la lista de bloques trampa activos.
     */
    // Método duplicado eliminado para evitar conflicto de compilación.

    /**
     * Returns the location of the safe block (slime block).
     * @return Location of the safe block
     */
    public Location getSafeBlockLocation() {
        World world = Bukkit.getWorld(GAME_WORLD);
        if (world == null) {
            return null;
        }
        return new Location(world, SAFE_BLOCK_X, SLIME_Y, SAFE_BLOCK_Z);
    }

    /**
     * Handles a player's win in the game.
     * @param player The winning player
     */
    public void handleWin(Player player) {
        player.sendMessage("§a¡Has ganado! Aterrizaste en el slime.");
        player.sendMessage("§ePuntos totales: " + getPlayerPoints(player));
        endGame();
    }

    /**
     * Handles a player's loss in the game.
     * @param player The losing player
     */
    public void handleLose(Player player) {
        player.sendMessage("§c¡Has perdido! ¡Mejor suerte la próxima vez!");
        player.sendMessage("§ePuntos obtenidos: " + getPlayerPoints(player));
        endGame();
    }
}
