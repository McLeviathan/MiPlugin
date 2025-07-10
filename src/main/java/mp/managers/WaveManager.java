package mp.managers;

import mp.plugin1.MiPlugin;
import mp.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WaveManager {
    private final MiPlugin plugin;
    private int currentWave = 0;
    private int zombiesAlive = 0;
    private boolean isWaveActive = false;
    private BukkitRunnable waveTask;
    private final Random random = new Random();
    // Control de elecciones de aliados por jugador (máx 2 por tipo)
    private final Map<UUID, Map<String, Integer>> allyEggChoices = new HashMap<>();

    public WaveManager(MiPlugin plugin) {
        this.plugin = plugin;
    }

    public void startWaveSystem() {
        if (isWaveActive) {
            Bukkit.broadcastMessage(MessageUtils.getColoredMessage("&c&l⚠ &f¡Ya hay una oleada activa en progreso!"));
            return;
        }

        // Establecer dificultad del mundo principal a NORMAL para las oleadas
        World mainWorld = Bukkit.getWorlds().isEmpty() ? null : Bukkit.getWorlds().get(0);
        if (mainWorld != null) {
            mainWorld.setDifficulty(org.bukkit.Difficulty.NORMAL);
            Bukkit.broadcastMessage(MessageUtils.getColoredMessage("&e&l☣ &6¡La dificultad del mundo ha cambiado a NORMAL para las oleadas!"));
        } else {
            Bukkit.getLogger().severe("[MiPlugin] No se pudo encontrar ningún mundo cargado para ajustar la dificultad.");
            Bukkit.broadcastMessage(MessageUtils.getColoredMessage("&c&lError: &7No se pudo ajustar la dificultad del mundo."));
            return; // No iniciar si no se puede cambiar la dificultad
        }

        // Reset del sistema
        currentWave = 0;
        zombiesAlive = 0;
        isWaveActive = false; // Se activará en startNextWave()

        // Mensajes épicos de inicio
        Bukkit.broadcastMessage(MessageUtils.getColoredMessage("&8&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
        Bukkit.broadcastMessage(MessageUtils.getColoredMessage("&c&l⚔ &6&l¡EL SISTEMA DE OLEADAS HA COMENZADO! &c&l⚔"));
        Bukkit.broadcastMessage(MessageUtils.getColoredMessage("&e&l➤ &f¡Prepárense para la batalla épica!"));
        Bukkit.broadcastMessage(MessageUtils.getColoredMessage("&a&l✦ &7¡Podrás elegir aliados en la &a4ª oleada &7y cada 4 oleadas subsiguientes!&a&l✦"));
        Bukkit.broadcastMessage(MessageUtils.getColoredMessage("&8&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));

        // Dar tiempo para que los jugadores se preparen
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Bukkit.broadcastMessage(MessageUtils.getColoredMessage("&6&l⚠ &f¡La primera oleada comenzará en &c5 segundos&f!"));
        }, 20L); // 1 segundo

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Bukkit.broadcastMessage(MessageUtils.getColoredMessage("&e&l3... &c2... &a1... &6&l¡COMENZAMOS!"));
            startNextWave();
        }, 100L); // 5 segundos
    }

    private void startNextWave() {
        currentWave++;
        isWaveActive = true;
        int mobCount = 5 + (currentWave * 2); // Podrías querer ajustar esto si las oleadas van a ser más largas o frecuentes

        Bukkit.broadcastMessage(MessageUtils.getColoredMessage("&c&lOLEADA " + currentWave + " &8» &7¡Ha comenzado!"));

        // Abrir menú de aliado cada 4 oleadas (4, 8, 12, ...)
        if (currentWave > 0 && currentWave % 4 == 0) {
            Bukkit.broadcastMessage(MessageUtils.getColoredMessage("&a&l✦ &f¡Es momento de elegir o reponer tus aliados especiales! &a&l✦"));
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    plugin.getMenuInventoryManager().openAllyEggMenu(player);
                }
            }, 60L); // 3 segundos de delay
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            spawnWaveMobs(player, mobCount);
        }

        // Finaliza la oleada después de 2 minutos si quedan mobs vivos (nueva duración)
        if (waveTask != null) waveTask.cancel();
        waveTask = new BukkitRunnable() {
            @Override
            public void run() {
                // Limpiar solo enemigos (Monsters) que no sean aliados ni aldeanos
                for (World world : Bukkit.getWorlds()) {
                    for (Entity entity : world.getEntities()) {
                        if (entity instanceof Villager) continue;
                        if (entity instanceof Player) continue;
                        if (entity.getCustomName() != null && entity.getCustomName().contains("Aliado")) continue;
                        if (entity instanceof Monster) {
                            entity.remove();
                        }
                    }
                }
                if (zombiesAlive > 0) {
                    Bukkit.broadcastMessage(MessageUtils.getColoredMessage("&eLa oleada terminó por tiempo. Eliminando mobs restantes..."));
                    for (World world : Bukkit.getWorlds()) {
                        for (Entity entity : world.getEntities()) {
                            if (entity instanceof Monster && entity.getCustomName() != null && entity.getCustomName().contains("Oleada")) {
                                entity.remove();
                            }
                        }
                    }
                    zombiesAlive = 0;
                }
                isWaveActive = false;

                // Mensaje de finalización mejorado
                if (currentWave == 5) {
                    Bukkit.broadcastMessage(MessageUtils.getColoredMessage("&6&l⚡ ¡OLEADA JEFE COMPLETADA! ⚡"));
                    Bukkit.broadcastMessage(MessageUtils.getColoredMessage("&e&l➤ &f¡Has derrotado al jefe de la 5ª oleada!"));
                } else {
                    Bukkit.broadcastMessage(MessageUtils.getColoredMessage("&a&l✓ &f¡Oleada " + currentWave + " completada!"));
                }

                // Iniciar siguiente oleada tras 10 segundos
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        startNextWave();
                    }
                }.runTaskLater(plugin, 20 * 10);
            }
        };
        waveTask.runTaskLater(plugin, 20 * 120); // 2 minutos = 120 segundos (actualizado)
    }

    private void spawnWaveMobs(Player player, int count) {
        World world = player.getWorld();
        Location playerLoc = player.getLocation();

        for (int i = 0; i < count; i++) {
            Location spawnLoc = getSafeSpawnLocation(player, 10, 5); // Intentar 10 veces, radio min 5
            if (spawnLoc == null) {
                // Bukkit.getLogger().warning("[MiPlugin DEBUG] No se pudo encontrar un lugar seguro para spawnear un mob de oleada cerca de " + player.getName());
                continue; // No se pudo encontrar un lugar seguro, saltar este mob
            }

            if (currentWave <= 3) {
                // Oleadas 1-3: Enemigos básicos
                Zombie zombie = (Zombie) world.spawnEntity(spawnLoc, EntityType.ZOMBIE);
                zombie.setCustomName(MessageUtils.getColoredMessage("&cZombie Oleada " + currentWave));
                zombie.setCustomNameVisible(true);
                zombiesAlive++;
                addRandomDrop(zombie);
            } else if (currentWave == 5 && i == 0) {
                // Oleada 5: Jefe épico (solo el primer mob)
                Zombie boss = (Zombie) world.spawnEntity(spawnLoc, EntityType.ZOMBIE);
                boss.setCustomName(MessageUtils.getColoredMessage("&4&l☠ &6JEFE ZOMBI SUPREMO &4&l☠"));
                boss.setCustomNameVisible(true);
                boss.setMaxHealth(120.0); // Más vida para el jefe
                boss.setHealth(120.0);
                boss.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
                boss.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                boss.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
                boss.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                boss.getEquipment().setItemInHand(new ItemStack(Material.DIAMOND_SWORD));
                boss.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 300, 2));
                boss.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 300, 2));
                boss.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 300, 1));
                zombiesAlive++;

                // Drop especial para el jefe
                boss.getEquipment().setItemInHandDropChance(0.5f);
                boss.getEquipment().setHelmetDropChance(0.3f);
                boss.getEquipment().setChestplateDropChance(0.3f);

                Bukkit.broadcastMessage(MessageUtils.getColoredMessage("&4&l⚡ ¡EL JEFE HA APARECIDO! ⚡"));
            } else {
                // Oleadas 4+: Enemigos élite variados
                spawnEliteEnemy(spawnLoc); // Pasar el spawnLoc validado
            }
        }
    }

    // Modificar spawnEliteEnemy para que acepte Location
    private void spawnEliteEnemy(Location spawnLoc) { // Ya no calcula su propia location
        World world = spawnLoc.getWorld(); // Obtener mundo desde la location
        int mobType = random.nextInt(4);
        switch (mobType) {
            case 0: // Zombi élite con armadura
                Zombie zombieElite = (Zombie) world.spawnEntity(spawnLoc, EntityType.ZOMBIE);
                zombieElite.setCustomName(MessageUtils.getColoredMessage("&c&lZombie Élite Oleada " + currentWave));
                zombieElite.setCustomNameVisible(true);
                zombieElite.getEquipment().setHelmet(new ItemStack(Material.IRON_HELMET));
                zombieElite.getEquipment().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                zombieElite.getEquipment().setItemInHand(new ItemStack(Material.IRON_SWORD));
                zombieElite.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 300, 1));
                zombieElite.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 300, 0));
                zombiesAlive++;
                addRandomDrop(zombieElite);
                break;

            case 1: // Esqueleto arquero élite
                Skeleton skeletonElite = (Skeleton) world.spawnEntity(spawnLoc, EntityType.SKELETON);
                skeletonElite.setCustomName(MessageUtils.getColoredMessage("&7&lArquero Élite Oleada " + currentWave));
                skeletonElite.setCustomNameVisible(true);
                skeletonElite.getEquipment().setHelmet(new ItemStack(Material.CHAINMAIL_HELMET));
                skeletonElite.getEquipment().setItemInHand(new ItemStack(Material.BOW));
                skeletonElite.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 300, 1));
                skeletonElite.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 300, 0));
                zombiesAlive++;
                addRandomDrop(skeletonElite);
                break;

            case 2: // Creeper cargado
                Creeper creeperElite = (Creeper) world.spawnEntity(spawnLoc, EntityType.CREEPER);
                creeperElite.setCustomName(MessageUtils.getColoredMessage("&a&lCreeper Cargado Oleada " + currentWave));
                creeperElite.setCustomNameVisible(true);
                creeperElite.setPowered(true); // Creeper cargado = más explosión
                creeperElite.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 300, 0));
                zombiesAlive++;
                addRandomDrop(creeperElite);
                break;

            case 3: // Bruja mejorada
                Witch witchElite = (Witch) world.spawnEntity(spawnLoc, EntityType.WITCH);
                witchElite.setCustomName(MessageUtils.getColoredMessage("&5&lBruja Suprema Oleada " + currentWave));
                witchElite.setCustomNameVisible(true);
                witchElite.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 300, 1));
                witchElite.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 300, 0));
                zombiesAlive++;
                addRandomDrop(witchElite);
                break;
        }
    }

    // Añade drops personalizados a los mobs hostiles
    private void addRandomDrop(LivingEntity entity) {
        int dropType = random.nextInt(100);
        ItemStack drop = null;
        if (dropType < 50) { // 50% comida
            Material[] foods = {Material.BREAD, Material.COOKED_BEEF, Material.COOKED_CHICKEN, Material.GOLDEN_APPLE};
            drop = new ItemStack(foods[random.nextInt(foods.length)]);
        } else if (dropType < 80) { // 30% poción
            drop = new ItemStack(Material.POTION, 1, (short) (8229 + random.nextInt(3))); // Regeneración, fuerza, velocidad
        } else { // 20% manzana dorada
            drop = new ItemStack(Material.GOLDEN_APPLE);
        }
        if (drop != null) {
            entity.getEquipment().setItemInHandDropChance(0.0f);
            entity.getEquipment().setHelmetDropChance(0.0f);
            entity.getEquipment().setChestplateDropChance(0.0f);
            entity.getEquipment().setLeggingsDropChance(0.0f);
            entity.getEquipment().setBootsDropChance(0.0f);
            entity.getEquipment().setItemInHand(null);
            entity.getEquipment().setHelmet(null);
            entity.getEquipment().setChestplate(null);
            entity.getEquipment().setLeggings(null);
            entity.getEquipment().setBoots(null);
            entity.getEquipment().setItemInHand(drop);
            entity.getEquipment().setItemInHandDropChance(1.0f);
        }
    }

    // NUEVO MÉTODO HELPER
    private Location getSafeSpawnLocation(Player player, int maxAttempts, double minRadius) {
        World world = player.getWorld();
        Location playerLoc = player.getLocation();

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            // Aumentar un poco el radio base y el aleatorio para más dispersión
            double radius = minRadius + (random.nextDouble() * 8); // Radio entre minRadius y minRadius+8
            double x = playerLoc.getX() + radius * Math.cos(angle);
            double z = playerLoc.getZ() + radius * Math.sin(angle);

            // Intentar encontrar una Y válida desde un poco más arriba del jugador, hacia abajo
            int startY = world.getHighestBlockYAt((int) x, (int) z) + 2; // Empezar un poco más arriba del bloque más alto
            Location potentialSpawnLoc = null;

            for (int yOffset = 0; yOffset <= 5; yOffset++) { // Buscar hasta 5 bloques hacia abajo desde startY
                Location checkLoc = new Location(world, x, startY - yOffset, z);
                Material blockAt = checkLoc.getBlock().getType();
                Material blockBelow = checkLoc.clone().subtract(0, 1, 0).getBlock().getType();
                Material blockAbove = checkLoc.clone().add(0, 1, 0).getBlock().getType();

                // Condición de spawn seguro:
                // - El bloque en los pies es transitable (AIRE, HIERBA_ALTA, etc.)
                // - El bloque en la cabeza es transitable (AIRE)
                // - El bloque DEBAJO de los pies es SÓLIDO y NO es LÍQUIDO o PELIGROSO
                if (isPassable(blockAt) && isPassable(blockAbove) &&
                        blockBelow.isSolid() && !isLiquidOrHazard(blockBelow)) {
                    potentialSpawnLoc = checkLoc;
                    break; // Encontrado un buen Y
                }
            }

            if (potentialSpawnLoc != null) {
                // Asegurarse de que no está dentro de un bloque o muy cerca de una pared
                // Esto es una verificación simple, se puede mejorar
                if (world.getNearbyEntities(potentialSpawnLoc, 0.5, 1, 0.5).stream().noneMatch(e -> e instanceof Player || e instanceof Villager)) {
                    // Verificar que no haya obstrucciones directas en un pequeño radio
                    boolean obstructed = false;
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dz = -1; dz <= 1; dz++) {
                            if (dx == 0 && dz == 0) continue;
                            if (potentialSpawnLoc.clone().add(dx, 0, dz).getBlock().getType().isSolid()) {
                                obstructed = true;
                                break;
                            }
                        }
                        if (obstructed) break;
                    }
                    if (!obstructed) {
                        return potentialSpawnLoc; // Ubicación segura encontrada
                    }
                }
            }
        }
        return null; // No se encontró ubicación segura después de varios intentos
    }

    private boolean isPassable(Material material) {
        return material == Material.AIR ||
                material == Material.LONG_GRASS ||
                material == Material.YELLOW_FLOWER ||
                material == Material.RED_ROSE ||
                material == Material.DEAD_BUSH ||
                material == Material.SNOW;
    }

    private boolean isLiquidOrHazard(Material material) {
        return material == Material.LAVA || material == Material.STATIONARY_LAVA ||
                material == Material.WATER || material == Material.STATIONARY_WATER ||
                material == Material.CACTUS || material == Material.FIRE;
    }

    public void decrementZombiesAlive() {
        zombiesAlive--;
        if (zombiesAlive <= 0 && isWaveActive) {
            isWaveActive = false;
            Bukkit.broadcastMessage(MessageUtils.getColoredMessage("&a¡Oleada " + currentWave + " completada!"));
            if (waveTask != null) waveTask.cancel();
            // Iniciar siguiente oleada tras 10 segundos
            new BukkitRunnable() {
                @Override
                public void run() {
                    startNextWave();
                }
            }.runTaskLater(plugin, 20 * 10);
        }
    }

    public void stopWaveSystem() {
        if (waveTask != null) waveTask.cancel();

        // Limpiar todos los enemigos de oleada activos
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof Monster && entity.getCustomName() != null &&
                        (entity.getCustomName().contains("Oleada") || entity.getCustomName().contains("JEFE"))) {
                    entity.remove();
                }
            }
        }

        // Reset completo del sistema
        isWaveActive = false;
        zombiesAlive = 0;
        int completedWaves = currentWave;
        currentWave = 0;

        // Mensajes épicos de finalización
        Bukkit.broadcastMessage(MessageUtils.getColoredMessage("&8&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
        Bukkit.broadcastMessage(MessageUtils.getColoredMessage("&c&l🛑 &4&l¡SISTEMA DE OLEADAS DETENIDO! &c&l🛑"));
        if (completedWaves > 0) {
            Bukkit.broadcastMessage(MessageUtils.getColoredMessage("&e&l➤ &fOleadas completadas: &a" + completedWaves));
        }
        Bukkit.broadcastMessage(MessageUtils.getColoredMessage("&7&l➤ &fTodos los enemigos han sido eliminados"));
        Bukkit.broadcastMessage(MessageUtils.getColoredMessage("&a&l✓ &f¡El sistema está listo para reiniciar!"));
        Bukkit.broadcastMessage(MessageUtils.getColoredMessage("&8&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬"));
    }

    // Métodos getter públicos para acceso desde otras clases
    public int getCurrentWave() {
        return currentWave;
    }

    public boolean isWaveActive() {
        return isWaveActive;
    }

    public int getZombiesAlive() {
        return zombiesAlive;
    }

    // Métodos para controlar elecciones de huevos aliados
    public boolean canChooseAllyEgg(Player player, String mobType) {
        UUID uuid = player.getUniqueId();
        allyEggChoices.putIfAbsent(uuid, new HashMap<>());
        Map<String, Integer> choices = allyEggChoices.get(uuid);
        return choices.getOrDefault(mobType, 0) < 2;
    }

    public void registerAllyEggChoice(Player player, String mobType) {
        UUID uuid = player.getUniqueId();
        allyEggChoices.putIfAbsent(uuid, new HashMap<>());
        Map<String, Integer> choices = allyEggChoices.get(uuid);
        choices.put(mobType, choices.getOrDefault(mobType, 0) + 1);
    }

    public int getPlayerAllyCount(Player player, String mobType) {
        UUID uuid = player.getUniqueId();
        allyEggChoices.putIfAbsent(uuid, new HashMap<>());
        Map<String, Integer> choices = allyEggChoices.get(uuid);
        return choices.getOrDefault(mobType, 0);
    }
}
