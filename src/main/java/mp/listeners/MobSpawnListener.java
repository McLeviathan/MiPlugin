package mp.listeners;

import mp.plugin1.MiPlugin;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class MobSpawnListener implements Listener {

    private final MiPlugin plugin;

    public MobSpawnListener(MiPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        // Permitir siempre spawns de nuestros aliados o mobs de oleada específicos (que deberían tener CUSTOM o PLUGIN como reason, o ser manejados directamente)
        if (event.getSpawnReason() == SpawnReason.CUSTOM || event.getSpawnReason() == SpawnReason.SPAWNER_EGG) {
            // Podríamos añadir una verificación extra si nuestros mobs custom tuvieran un metadata tag
            return;
        }

        // Si las oleadas están activas
        if (plugin.getWaveManager().isWaveActive()) {
            // Si el mob que intenta spawnear NO es uno de nuestros mobs de oleada (basado en nombre o tipo si es necesario)
            // y es un spawn natural/por chunk, etc., lo cancelamos para no interferir.
            // Esta es una lógica simplificada; idealmente, los mobs de oleada se marcarían con metadata.
            // Por ahora, si está activa la oleada, cancelamos spawns naturales de monstruos.
            if (event.getEntity() instanceof Monster &&
                    (event.getSpawnReason() == SpawnReason.NATURAL ||
                            event.getSpawnReason() == SpawnReason.CHUNK_GEN ||
                            event.getSpawnReason() == SpawnReason.JOCKEY ||
                            event.getSpawnReason() == SpawnReason.MOUNT)) {

                // Excepción: No cancelar si el mob tiene un nombre custom que indica que es de una oleada
                // Esto es por si el WaveManager usa spawnEntity que luego dispara este evento con otra SpawnReason
                if (event.getEntity().getCustomName() != null && event.getEntity().getCustomName().contains("Oleada")) {
                    return;
                }
                if (event.getEntity().getCustomName() != null && event.getEntity().getCustomName().contains("JEFE")) {
                    return;
                }

                event.setCancelled(true);
                // Bukkit.getLogger().info("[MiPlugin DEBUG] Oleada activa: Spawn natural de " + event.getEntityType().name() + " cancelado.");
            }
        } else {
            // Si las oleadas NO están activas, cancelar todos los spawns NATURALES de MONSTRUOS.
            if (event.getEntity() instanceof Monster &&
                    (event.getSpawnReason() == SpawnReason.NATURAL ||
                            event.getSpawnReason() == SpawnReason.CHUNK_GEN ||
                            event.getSpawnReason() == SpawnReason.JOCKEY ||
                            event.getSpawnReason() == SpawnReason.MOUNT)) {
                event.setCancelled(true);
                // Bukkit.getLogger().info("[MiPlugin DEBUG] Oleada inactiva: Spawn natural de " + event.getEntityType().name() + " cancelado.");
            }
            // Permitir animales y otros spawns no hostiles (o manejarlos de otra forma si es necesario)
        }
    }
}
