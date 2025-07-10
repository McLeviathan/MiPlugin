package mp.listeners;

import mp.plugin1.MiPlugin;
import mp.utils.MessageUtils;
import mp.utils.ItemUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

import java.util.Random;

public class ZombieListener implements Listener {
    private final MiPlugin plugin;
    private final Random random = new Random();

    public ZombieListener(MiPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        String name = entity.getCustomName();
        if (name != null && name.toLowerCase().contains("oleada")) {
            plugin.getWaveManager().decrementZombiesAlive();
        }

        // Solo procesar mobs hostiles con nombres personalizados (de oleadas)
        if (!(entity instanceof Monster)) return;
        if (entity.getCustomName() == null) return;
        if (!entity.getCustomName().contains("Oleada")) return;

        // Limpiar drops por defecto y agregar drops épicos
        event.getDrops().clear();

        // Dar XP extra por matar enemigos de oleada
        event.setDroppedExp(event.getDroppedExp() * 2);

        // Drops épicos garantizados según el tipo de oleada
        int currentWave = plugin.getWaveManager().getCurrentWave();

        if (currentWave >= 5) {
            // Oleada 5+: Drops de jefe épicos
            addEpicBossDrops(event, entity);
        } else if (currentWave >= 4) {
            // Oleada 4+: Drops élite
            addEliteDrops(event, entity);
        } else {
            // Oleadas 1-3: Drops básicos mejorados
            addBasicDrops(event, entity);
        }
    }

    @EventHandler
    public void onVillagerDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Villager) {
            // Cancelar cualquier daño a aldeanos, sin importar el atacante
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVillagerTarget(EntityTargetLivingEntityEvent event) {
        if (event.getTarget() instanceof Villager) {
            // No cancelar el evento, así los aldeanos huyen y se asustan
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityExplode(EntityExplodeEvent event) {
        Entity exploder = event.getEntity();

        // PROTECCIÓN GENERAL DE ALDEA - Cancelar daño a bloques de TODAS las explosiones
        if (!event.blockList().isEmpty()) {
            // Limpiar lista de bloques para prevenir daño estructural
            event.blockList().clear();

            // Mensaje informativo específico según el tipo de explosión
            String message = "&a&l✓ &7Aldea protegida contra explosión";

            if (exploder.getCustomName() != null && exploder.getCustomName().contains("Oleada")) {
                message += " de &c" + exploder.getCustomName().replace("§", "&");
            } else if (exploder instanceof Creeper) {
                message += " de &aCreeper";
            } else {
                message += " &7(tipo: &e" + exploder.getType().name() + "&7)";
            }

            // Notificar a jugadores cercanos
            for (Player player : exploder.getWorld().getPlayers()) {
                if (player.getLocation().distance(exploder.getLocation()) <= 25) {
                    player.sendMessage(MessageUtils.getColoredMessage(message));
                }
            }
        }

        // PROTECCIÓN ESPECIAL para explosiones cerca de aliados
        if (exploder instanceof Creeper) {
            // Verificar si hay aliados cerca (protección adicional)
            for (Entity entity : exploder.getNearbyEntities(15, 15, 15)) {
                if (entity.getCustomName() != null && entity.getCustomName().contains("Aliado")) {
                    // Informar sobre protección especial de zona de aliado
                    for (Player player : exploder.getWorld().getPlayers()) {
                        if (player.getLocation().distance(exploder.getLocation()) <= 20) {
                            player.sendMessage(MessageUtils.getColoredMessage("&b&l✓ &7Zona de aliado protegida contra explosión"));
                        }
                    }
                    break;
                }
            }
        }

        // PROTECCIÓN ADICIONAL - Reducir daño de explosión si es muy alta
        if (event.getYield() > 3.0f) {
            event.setYield(1.0f); // Reducir potencia de explosión para evitar daño masivo a entidades
        }
    }

    private void addEpicBossDrops(EntityDeathEvent event, Entity entity) {
        // DROPS ÉPICOS DEL JEFE - Reducidos

        // Pociones épicas (1 de cada, 100% chance)
        event.getDrops().add(new ItemStack(Material.POTION, 1, (short) 8233)); // Fuerza II
        event.getDrops().add(new ItemStack(Material.POTION, 1, (short) 8201)); // Regeneración II
        event.getDrops().add(new ItemStack(Material.POTION, 1, (short) 8229)); // Velocidad II

        // Manzanas doradas encantadas (50% chance para 1)
        if (random.nextInt(100) < 50) {
            event.getDrops().add(new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1)); // Notch Apple
        }

        // Recursos valiosos (cantidades reducidas)
        event.getDrops().add(new ItemStack(Material.GOLDEN_APPLE, 2 + random.nextInt(2))); // Manzanas doradas normales (2-3)
        event.getDrops().add(new ItemStack(Material.DIAMOND, 1 + random.nextInt(2)));    // Diamantes (1-2)
        event.getDrops().add(new ItemStack(Material.EMERALD, 4 + random.nextInt(4)));    // Esmeraldas (4-7)
        event.getDrops().add(new ItemStack(Material.ARROW, 16 + random.nextInt(17)));   // Flechas (16-32)

        // Comida de calidad (cantidades reducidas)
        event.getDrops().add(new ItemStack(Material.COOKED_BEEF, 8 + random.nextInt(9))); // (8-16)
        event.getDrops().add(new ItemStack(Material.BREAD, 6 + random.nextInt(7)));     // (6-12)

        // Esmeraldas épicas personalizadas (cantidad reducida)
        event.getDrops().add(ItemUtils.generateEsmeralditem(3 + random.nextInt(3))); // (3-5)
    }

    private void addEliteDrops(EntityDeathEvent event, Entity entity) {
        // DROPS ÉLITE - Reducidos

        // Pociones útiles (40% chance para 1)
        if (random.nextInt(100) < 40) {
            short[] potionTypes = {8201, 8233, 8229}; // Regeneración, Fuerza, Velocidad (Nivel I o II simples)
            short chosenType = potionTypes[random.nextInt(potionTypes.length)];
            event.getDrops().add(new ItemStack(Material.POTION, 1, chosenType));
        }

        // Recursos valiosos garantizados (cantidades reducidas)
        event.getDrops().add(new ItemStack(Material.GOLDEN_APPLE, 1 + random.nextInt(2))); // (1-2)
        event.getDrops().add(new ItemStack(Material.COOKED_BEEF, 2 + random.nextInt(3))); // (2-4)
        event.getDrops().add(new ItemStack(Material.ARROW, 8 + random.nextInt(9)));    // (8-16)

        // 25% chance de recursos premium (cantidades reducidas)
        if (random.nextInt(100) < 25) {
            if (random.nextBoolean()) {
                event.getDrops().add(new ItemStack(Material.DIAMOND, 1));
            } else {
                event.getDrops().add(ItemUtils.generateEsmeralditem(1 + random.nextInt(2))); // (1-2)
            }
        }
    }

    private void addBasicDrops(EntityDeathEvent event, Entity entity) {
        // DROPS BÁSICOS - Reducidos

        // Comida garantizada (cantidad reducida)
        Material[] foods = {Material.BREAD, Material.COOKED_BEEF, Material.COOKED_CHICKEN, Material.GRILLED_PORK};
        event.getDrops().add(new ItemStack(foods[random.nextInt(foods.length)], 1 + random.nextInt(2))); // (1-2)

        // Flechas (30% chance para 2-6)
        if (random.nextInt(100) < 30) {
            event.getDrops().add(new ItemStack(Material.ARROW, 2 + random.nextInt(5)));
        }

        // Pociones básicas (15% chance para 1)
        if (random.nextInt(100) < 15) {
            short[] basicPotions = {8193, 8201, 8225}; // Curación I, Regeneración I, Velocidad I
            short chosenPotion = basicPotions[random.nextInt(basicPotions.length)];
            event.getDrops().add(new ItemStack(Material.POTION, 1, chosenPotion));
        }

        // Recursos ocasionales (10% chance, cantidad reducida)
        if (random.nextInt(100) < 10) {
            Material[] resources = {Material.IRON_INGOT, Material.GOLD_INGOT, Material.COAL};
            event.getDrops().add(new ItemStack(resources[random.nextInt(resources.length)], 1 + random.nextInt(2))); // (1-2)
        }

        // Manzana dorada ocasional (5% chance para 1)
        if (random.nextInt(100) < 5) {
            event.getDrops().add(new ItemStack(Material.GOLDEN_APPLE, 1));
        }
    }
}
