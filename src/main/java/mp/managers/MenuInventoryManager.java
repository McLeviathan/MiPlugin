package mp.managers;

import mp.plugin1.MiPlugin;
import mp.utils.MessageUtils;
import mp.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MenuInventoryManager {

    private final MiPlugin plugin;
    private final Map<UUID, BukkitRunnable> allyFollowTasks = new HashMap<>();

    public MenuInventoryManager(MiPlugin plugin) {
        this.plugin = plugin;
    }

    public void openAllyEggMenu(Player player) {
        // Crear inventario con 27 slots (3 filas) y un título estilizado
        Inventory inv = Bukkit.createInventory(null, 27, MessageUtils.getColoredMessage("&a&l✦ &fElige tu aliado especial &a&l✦"));

        // Llenar borde con vidrios verdes
        fillBorder(inv, Material.STAINED_GLASS_PANE, (short) 5);

        // 🧱 Gólem de Hierro
        ItemStack golemItem = new ItemStack(Material.IRON_BLOCK);
        ItemMeta golemMeta = golemItem.getItemMeta();
        golemMeta.setDisplayName(MessageUtils.getColoredMessage("&f&l🛡 &7Gólem de Hierro &f&l🛡"));
        List<String> golemLore = new ArrayList<>();
        golemLore.add(MessageUtils.getColoredMessage("&8▪ &7Tanque defensor, absorbe daño"));
        golemLore.add(MessageUtils.getColoredMessage("&8▪ &7Protege a jugadores cercanos"));
        golemLore.add("");
        golemLore.add(MessageUtils.getColoredMessage("&a&l⚡ &fEstadísticas:"));
        golemLore.add(MessageUtils.getColoredMessage("&7• &fVida: &a300 HP"));
        golemLore.add(MessageUtils.getColoredMessage("&7• &fDuración: &e5 minutos"));
        golemLore.add(MessageUtils.getColoredMessage("&7• &fEfectos: &bResistencia II, Regeneración"));
        golemLore.add("");

        int golemsCount = plugin.getWaveManager().getPlayerAllyCount(player, "golem");
        if (golemsCount < 2) {
            golemLore.add(MessageUtils.getColoredMessage("&a&l➤ &eClick para elegir &7(" + (2 - golemsCount) + " restantes)"));
        } else {
            golemLore.add(MessageUtils.getColoredMessage("&c&l✗ &7Máximo alcanzado (2/2)"));
        }
        golemMeta.setLore(golemLore);
        golemItem.setItemMeta(golemMeta);
        inv.setItem(11, golemItem);

        // ☃️ Muñeco de Nieve  
        ItemStack snowmanItem = new ItemStack(Material.SNOW_BALL);
        ItemMeta snowMeta = snowmanItem.getItemMeta();
        snowMeta.setDisplayName(MessageUtils.getColoredMessage("&b&l❄ &fMuñeco de Nieve &b&l❄"));
        List<String> snowLore = new ArrayList<>();
        snowLore.add(MessageUtils.getColoredMessage("&8▪ &7Arquero a distancia, molesta enemigos"));
        snowLore.add(MessageUtils.getColoredMessage("&8▪ &7Ataca con bolas de nieve"));
        snowLore.add("");
        snowLore.add(MessageUtils.getColoredMessage("&b&l⚡ &fEstadísticas:"));
        snowLore.add(MessageUtils.getColoredMessage("&7• &fVida: &a60 HP")); // Aumentada vida
        snowLore.add(MessageUtils.getColoredMessage("&7• &fDuración: &e5 minutos"));
        snowLore.add(MessageUtils.getColoredMessage("&7• &fEfectos: &bVelocidad I, Regeneración leve"));
        snowLore.add("");

        int snowmenCount = plugin.getWaveManager().getPlayerAllyCount(player, "snowman");
        if (snowmenCount < 2) {
            snowLore.add(MessageUtils.getColoredMessage("&a&l➤ &eClick para elegir &7(" + (2 - snowmenCount) + " restantes)"));
        } else {
            snowLore.add(MessageUtils.getColoredMessage("&c&l✗ &7Máximo alcanzado (2/2)"));
        }
        snowMeta.setLore(snowLore);
        snowmanItem.setItemMeta(snowMeta);
        inv.setItem(15, snowmanItem);

        ItemStack info = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = info.getItemMeta();
        infoMeta.setDisplayName(MessageUtils.getColoredMessage("&6&l📋 &eInformación de Aliados"));
        List<String> infoLore = new ArrayList<>();
        infoLore.add(MessageUtils.getColoredMessage("&8▪ &7Puedes elegir/reponer aliados en la &a4ª oleada&7,"));
        infoLore.add(MessageUtils.getColoredMessage("&7  luego cada &a4 oleadas &7(8ª, 12ª, etc.)"));
        infoLore.add(MessageUtils.getColoredMessage("&8▪ &7Máximo &e2 de cada tipo &7por jugador"));
        infoLore.add(MessageUtils.getColoredMessage("&8▪ &7Duran &e5 minutos &7cada uno"));
        infoLore.add(MessageUtils.getColoredMessage("&8▪ &7Te ayudarán y se mantendrán cerca!"));
        infoLore.add("");
        infoLore.add(MessageUtils.getColoredMessage("&a&l✓ &fTus aliados actuales:"));
        infoLore.add(MessageUtils.getColoredMessage("&7• &fGólems: &e" + golemsCount + "/2"));
        infoLore.add(MessageUtils.getColoredMessage("&7• &fMuñecos: &e" + snowmenCount + "/2"));
        infoMeta.setLore(infoLore);
        info.setItemMeta(infoMeta);
        inv.setItem(13, info);

        player.openInventory(inv);
    }

    public void spawnAlly(Player player, String allyType) {
        Location spawnLocation = player.getLocation().add(randomOffset(), 0, randomOffset());
        spawnLocation.setY(player.getWorld().getHighestBlockYAt(spawnLocation));

        LivingEntity spawnedAlly = null;

        switch (allyType.toLowerCase()) {
            case "golem":
                IronGolem golem = (IronGolem) player.getWorld().spawnEntity(spawnLocation, EntityType.IRON_GOLEM);
                golem.setCustomName(MessageUtils.getColoredMessage("&f&l🛡 &7Guardián de " + player.getName()));
                golem.setCustomNameVisible(true);
                golem.setMaxHealth(300.0);
                golem.setHealth(300.0);
                golem.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
                golem.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
                golem.setPlayerCreated(true);
                spawnedAlly = golem;
                player.sendMessage(MessageUtils.getColoredMessage("&a&l➤ &f¡Tu &7Gólem Guardián&f ha aparecido y te protegerá!"));
                player.sendMessage(MessageUtils.getColoredMessage("&7» &fVida: &a300 HP &7| Lealtad: &eMáxima"));
                player.getWorld().playSound(spawnLocation, Sound.IRONGOLEM_HIT, 1.0f, 0.8f);
                break;

            case "snowman":
                Snowman snowman = (Snowman) player.getWorld().spawnEntity(spawnLocation, EntityType.SNOWMAN);
                snowman.setCustomName(MessageUtils.getColoredMessage("&b&l❄ &7Vigilante de " + player.getName()));
                snowman.setCustomNameVisible(true);
                snowman.setMaxHealth(60.0);
                snowman.setHealth(60.0);
                snowman.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                snowman.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
                spawnedAlly = snowman;
                player.sendMessage(MessageUtils.getColoredMessage("&b&l➤ &f¡Tu &fVigilante de Nieve&f ha aparecido y te cubrirá!"));
                player.sendMessage(MessageUtils.getColoredMessage("&7» &fVida: &a60 HP &7| Lealtad: &eMáxima"));
                player.getWorld().playSound(spawnLocation, Sound.STEP_SNOW, 1.0f, 1.2f);
                break;
        }

        if (spawnedAlly != null) {
            final UUID allyUUID = spawnedAlly.getUniqueId();
            final UUID playerUUID = player.getUniqueId();
            final LivingEntity finalAlly = spawnedAlly;

            BukkitRunnable existingTask = allyFollowTasks.get(allyUUID);
            if (existingTask != null) {
                existingTask.cancel();
            }

            BukkitRunnable followTask = new BukkitRunnable() {
                private final double MAX_DISTANCE_SQUARED = 20 * 20;
                private final double TELEPORT_DISTANCE_SQUARED = 3 * 3;

                @Override
                public void run() {
                    Player owner = Bukkit.getPlayer(playerUUID);
                    // Descomentar para depuración masiva:
                    // Bukkit.getLogger().info("[AllyFollow] Tarea para " + finalAlly.getUniqueId() + ". Dueño: " + (owner != null ? owner.getName() : "NULL"));

                    if (owner == null || !owner.isOnline() || finalAlly.isDead() || !finalAlly.isValid()) {
                        Bukkit.getLogger().info("[AllyFollow] Cancelando tarea para " + finalAlly.getCustomName() + ". Dueño online: " + (owner != null && owner.isOnline()) + ", Aliado muerto: " + finalAlly.isDead() + ", Aliado inválido: " + !finalAlly.isValid());
                        this.cancel();
                        allyFollowTasks.remove(allyUUID);
                        return;
                    }

                    Location allyLoc = finalAlly.getLocation();
                    Location ownerLoc = owner.getLocation();

                    if (!allyLoc.getWorld().equals(ownerLoc.getWorld())) {
                        Bukkit.getLogger().info("[AllyFollow] Mundos diferentes! Teletransportando " + finalAlly.getCustomName() + " a " + owner.getName());
                        finalAlly.teleport(ownerLoc.clone().add(randomOffset(), 0, randomOffset()));
                        return;
                    }

                    double distanceSq = allyLoc.distanceSquared(ownerLoc);
                    // Bukkit.getLogger().info("[AllyFollow] " + finalAlly.getCustomName() + " DistSq: " + distanceSq + " (Max: " + MAX_DISTANCE_SQUARED + ")");

                    if (distanceSq > MAX_DISTANCE_SQUARED) {
                        Location teleportTo = ownerLoc.clone().add(randomOffset(), 0, randomOffset());
                        teleportTo.setY(ownerLoc.getWorld().getHighestBlockYAt(teleportTo) + 0.5);

                        // Bukkit.getLogger().info("[AllyFollow] " + finalAlly.getCustomName() + " demasiado lejos. Intentando teleportar a: " + teleportTo);

                        if (finalAlly.getLocation().distanceSquared(teleportTo) > TELEPORT_DISTANCE_SQUARED) {
                            finalAlly.teleport(teleportTo);
                            Bukkit.getLogger().info("[AllyFollow] ¡" + finalAlly.getCustomName() + " teletransportado a " + owner.getName() + "!");
                        } else {
                            // Bukkit.getLogger().info("[AllyFollow] Teleportación cancelada, " + finalAlly.getCustomName() + " ya cerca del punto destino.");
                        }
                    }
                }
            };
            followTask.runTaskTimer(plugin, 40L, 60L); // Revisa cada 3s, empieza tras 2s
            allyFollowTasks.put(allyUUID, followTask);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (finalAlly != null && !finalAlly.isDead() && finalAlly.isValid()) {
                    finalAlly.remove();
                    Player owner = Bukkit.getPlayer(playerUUID);
                    if (owner != null && owner.isOnline()) {
                        String allyTypeName = finalAlly.getCustomName().contains("Guardián") ? "Gólem Guardián" : "Vigilante de Nieve";
                        owner.sendMessage(MessageUtils.getColoredMessage("&6&l⚠ &7Tu " + allyTypeName + " &7ha desaparecido por límite de tiempo."));
                        owner.playSound(owner.getLocation(), Sound.FIZZ, 1.0f, 0.8f);
                    }
                }
                BukkitRunnable task = allyFollowTasks.remove(allyUUID);
                if (task != null) {
                    task.cancel();
                }
            }, 20 * 300);
        }
    }

    private double randomOffset() {
        return (Math.random() * 2.0) - 1.0;
    }

    public BukkitRunnable removeAllyFollowTask(UUID allyUUID) {
        Bukkit.getLogger().info("[AllyFollow] Removiendo tarea de seguimiento para UUID: " + allyUUID);
        return allyFollowTasks.remove(allyUUID);
    }

    public void openMainInventory(Player player) {
        // Menú principal mejorado con sistema de oleadas
        Inventory inv = Bukkit.createInventory(null, 27, MessageUtils.getColoredMessage("&c&lM&6&lE&e&lN&a&lU &b&lP&d&lR&5&lI&4&lN&c&lC&6&lI&e&lP&a&lL"));
        fillBorder(inv, Material.STAINED_GLASS_PANE, (short) 14); // Borde rojo

        // 🗡️ Iniciar Sistema de Oleadas
        ItemStack startWaves = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta startMeta = startWaves.getItemMeta();
        startMeta.setDisplayName(MessageUtils.getColoredMessage("&c⚔ &4&lINICIAR OLEADAS &c⚔"));
        List<String> startLore = new ArrayList<>();
        startLore.add(MessageUtils.getColoredMessage("&8▪ &7¡Comienza la batalla épica!"));
        startLore.add(MessageUtils.getColoredMessage("&8▪ &7Sobrevive oleadas de enemigos"));
        startLore.add("");
        startLore.add(MessageUtils.getColoredMessage("&c&l⚡ &fCaracterísticas:"));
        startLore.add(MessageUtils.getColoredMessage("&7• &fCada oleada dura &c2 minutos"));
        startLore.add(MessageUtils.getColoredMessage("&7• &fDrops épicos garantizados"));
        startLore.add(MessageUtils.getColoredMessage("&7• &fAliados en la &a3ª oleada"));
        startLore.add(MessageUtils.getColoredMessage("&7• &fEnemigos élite desde la &64ª"));
        startLore.add(MessageUtils.getColoredMessage("&7• &fJefe épico en la &65ª oleada"));
        startLore.add("");
        boolean isActive = plugin.getWaveManager().isWaveActive();
        if (!isActive) {
            startLore.add(MessageUtils.getColoredMessage("&a&l➤ &eClick para comenzar"));
        } else {
            startLore.add(MessageUtils.getColoredMessage("&c&l✗ &7Ya hay oleadas activas"));
        }
        startMeta.setLore(startLore);
        startWaves.setItemMeta(startMeta);
        inv.setItem(10, startWaves);

        // ⚔️ Elegir Equipo Nórdico
        ItemStack norseTeam = new ItemStack(Material.GOLD_HELMET);
        ItemMeta norseMeta = norseTeam.getItemMeta();
        norseMeta.setDisplayName(MessageUtils.getColoredMessage("&9⚡ &b&lEQUIPOS NÓRDICOS &9⚡"));
        List<String> norseLore = new ArrayList<>();
        norseLore.add(MessageUtils.getColoredMessage("&8▪ &7Elige el poder de los dioses"));
        norseLore.add(MessageUtils.getColoredMessage("&8▪ &7Cada dios tiene habilidades únicas"));
        norseLore.add("");
        norseLore.add(MessageUtils.getColoredMessage("&b&l⚡ &fDisponibles:"));
        norseLore.add(MessageUtils.getColoredMessage("&7• &fOdín &8- &7Sabiduría y Poder"));
        norseLore.add(MessageUtils.getColoredMessage("&7• &eThor &8- &7Trueno y Valor"));
        norseLore.add(MessageUtils.getColoredMessage("&7• &aLoki &8- &7Astucia y Magia"));
        norseLore.add(MessageUtils.getColoredMessage("&7• &dFreyja &8- &7Vida y Belleza"));
        norseLore.add("");
        norseLore.add(MessageUtils.getColoredMessage("&a&l➤ &eClick para elegir"));
        norseMeta.setLore(norseLore);
        norseTeam.setItemMeta(norseMeta);
        inv.setItem(12, norseTeam);

        // 📊 Estado de Oleadas
        ItemStack waveStatus = new ItemStack(Material.BOOK);
        ItemMeta statusMeta = waveStatus.getItemMeta();
        statusMeta.setDisplayName(MessageUtils.getColoredMessage("&6📊 &e&lESTADO ACTUAL"));
        List<String> statusLore = new ArrayList<>();
        int currentWave = plugin.getWaveManager().getCurrentWave();

        if (isActive) {
            statusLore.add(MessageUtils.getColoredMessage("&a&l✓ &fSistema activo"));
            statusLore.add(MessageUtils.getColoredMessage("&7• &fOleada actual: &c" + currentWave));
            statusLore.add(MessageUtils.getColoredMessage("&7• &fEnemigos restantes: &c" + plugin.getWaveManager().getZombiesAlive()));

            if (currentWave == 2) {
                statusLore.add(MessageUtils.getColoredMessage("&a&l⚠ &fPróxima oleada: &aAliados disponibles"));
            } else if (currentWave == 3) {
                statusLore.add(MessageUtils.getColoredMessage("&6&l⚠ &fPróxima oleada: &6Enemigos élite"));
            } else if (currentWave == 4) {
                statusLore.add(MessageUtils.getColoredMessage("&c&l⚠ &fPróxima oleada: &cJefe épico"));
            }
        } else {
            statusLore.add(MessageUtils.getColoredMessage("&c&l✗ &fSistema inactivo"));
            if (currentWave > 0) {
                statusLore.add(MessageUtils.getColoredMessage("&7• &fÚltima oleada completada: &e" + currentWave));
            }
        }

        statusLore.add("");
        statusLore.add(MessageUtils.getColoredMessage("&6&l⚡ &fProgresión de dificultad:"));
        statusLore.add(MessageUtils.getColoredMessage("&7• &fOleadas 1-3: &aEnemigos básicos"));
        statusLore.add(MessageUtils.getColoredMessage("&7• &fOleada 3: &bAliados disponibles"));
        statusLore.add(MessageUtils.getColoredMessage("&7• &fOleada 4+: &6Enemigos élite"));
        statusLore.add(MessageUtils.getColoredMessage("&7• &fOleada 5: &cJefe épico"));
        statusMeta.setLore(statusLore);
        waveStatus.setItemMeta(statusMeta);
        inv.setItem(14, waveStatus);

        if (isActive) {
            ItemStack stopWaves = new ItemStack(Material.REDSTONE_BLOCK);
            ItemMeta stopMeta = stopWaves.getItemMeta();
            stopMeta.setDisplayName(MessageUtils.getColoredMessage("&c🛑 &4&lDETENER OLEADAS"));
            List<String> stopLore = new ArrayList<>();
            stopLore.add(MessageUtils.getColoredMessage("&8▪ &7Detiene el sistema actual"));
            stopLore.add(MessageUtils.getColoredMessage("&8▪ &cLimpia todos los enemigos"));
            stopLore.add("");
            stopLore.add(MessageUtils.getColoredMessage("&c&l⚠ &fAdvertencia:"));
            stopLore.add(MessageUtils.getColoredMessage("&7• &fSe perderá el progreso actual"));
            stopLore.add(MessageUtils.getColoredMessage("&7• &fTodos los aliados desaparecerán"));
            stopLore.add("");
            stopLore.add(MessageUtils.getColoredMessage("&e&l➤ &cClick para detener"));
            stopMeta.setLore(stopLore);
            stopWaves.setItemMeta(stopMeta);
            inv.setItem(16, stopWaves);
        } else {
            ItemStack allyInfo = new ItemStack(Material.EMERALD);
            ItemMeta allyMeta = allyInfo.getItemMeta();
            allyMeta.setDisplayName(MessageUtils.getColoredMessage("&a&l👥 &f&lTUS ALIADOS"));
            List<String> allyLore = new ArrayList<>();
            allyLore.add(MessageUtils.getColoredMessage("&8▪ &7Estado actual de tus compañeros"));
            allyLore.add("");
            int golems = plugin.getWaveManager().getPlayerAllyCount(player, "golem");
            int snowmen = plugin.getWaveManager().getPlayerAllyCount(player, "snowman");
            allyLore.add(MessageUtils.getColoredMessage("&f&l⚡ &fTus aliados elegidos:"));
            allyLore.add(MessageUtils.getColoredMessage("&7• &fGólems de Hierro: &e" + golems + "/2"));
            allyLore.add(MessageUtils.getColoredMessage("&7• &fMuñecos de Nieve: &e" + snowmen + "/2"));
            allyLore.add("");
            allyLore.add(MessageUtils.getColoredMessage("&a&l✓ &7Los aliados aparecen en la &a3ª oleada"));
            allyMeta.setLore(allyLore);
            allyInfo.setItemMeta(allyMeta);
            inv.setItem(16, allyInfo);
        }
        player.openInventory(inv);
    }

    public void openNorseEpicTeamsMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, MessageUtils.getColoredMessage("&9&lEquipos Nordicos Epicos"));
        fillBorder(inv, Material.ICE, (short) 0);

        ItemStack odin = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
        ItemMeta odinMeta = odin.getItemMeta();
        odinMeta.setDisplayName(MessageUtils.getColoredMessage("&8&l[&fOdín&8&l] &7- &bSabiduría y Poder"));
        List<String> odinLore = new ArrayList<>();
        odinLore.add(MessageUtils.getColoredMessage("&7Rey de Asgard, sabiduría ancestral y poder divino."));
        odinLore.add(MessageUtils.getColoredMessage("&bEfectos: &fFuerza III, Regeneración I, Resistencia II"));
        odinLore.add(MessageUtils.getColoredMessage("&bEspeciales: &fVisión nocturna, Respiración acuática"));
        odinLore.add(MessageUtils.getColoredMessage("&8Click para elegir"));
        odinMeta.setLore(odinLore);
        odin.setItemMeta(odinMeta);
        inv.setItem(10, odin);

        ItemStack thor = new ItemStack(Material.DIAMOND_AXE);
        ItemMeta thorMeta = thor.getItemMeta();
        thorMeta.setDisplayName(MessageUtils.getColoredMessage("&e&l[&6Thor&e&l] &7- &eTrueno y Valor"));
        List<String> thorLore = new ArrayList<>();
        thorLore.add(MessageUtils.getColoredMessage("&7Dios del trueno, el más fuerte de Asgard."));
        thorLore.add(MessageUtils.getColoredMessage("&eEfectos: &fFuerza IV, Resistencia III, Velocidad II"));
        thorLore.add(MessageUtils.getColoredMessage("&eEspeciales: &fResistencia al fuego, Prisa III"));
        thorLore.add(MessageUtils.getColoredMessage("&8Click para elegir"));
        thorMeta.setLore(thorLore);
        thor.setItemMeta(thorMeta);
        inv.setItem(12, thor);

        ItemStack loki = new ItemStack(Material.ENDER_PEARL);
        ItemMeta lokiMeta = loki.getItemMeta();
        lokiMeta.setDisplayName(MessageUtils.getColoredMessage("&a&l[&2Loki&a&l] &7- &aAstucia y Magia"));
        List<String> lokiLore = new ArrayList<>();
        lokiLore.add(MessageUtils.getColoredMessage("&7Maestro del engaño, sombras y transformación."));
        lokiLore.add(MessageUtils.getColoredMessage("&aEfectos: &fVelocidad IV, Salto III, Invisibilidad"));
        lokiLore.add(MessageUtils.getColoredMessage("&aEspeciales: &fSigilo total, Visión nocturna"));
        lokiLore.add(MessageUtils.getColoredMessage("&c⚠ Debilidad por balance"));
        lokiLore.add(MessageUtils.getColoredMessage("&8Click para elegir"));
        lokiMeta.setLore(lokiLore);
        loki.setItemMeta(lokiMeta);
        inv.setItem(14, loki);

        ItemStack freyja = new ItemStack(Material.GOLDEN_APPLE);
        ItemMeta freyjaMeta = freyja.getItemMeta();
        freyjaMeta.setDisplayName(MessageUtils.getColoredMessage("&d&l[&5Freyja&d&l] &7- &dVida y Belleza"));
        List<String> freyjaLore = new ArrayList<>();
        freyjaLore.add(MessageUtils.getColoredMessage("&7Diosa del amor, guerra y supervivencia."));
        freyjaLore.add(MessageUtils.getColoredMessage("&dEfectos: &fRegeneración III, Absorción III"));
        freyjaLore.add(MessageUtils.getColoredMessage("&dEspeciales: &fVida extra V, Resistencia II"));
        freyjaLore.add(MessageUtils.getColoredMessage("&8Click para elegir"));
        freyjaMeta.setLore(freyjaLore);
        freyja.setItemMeta(freyjaMeta);
        inv.setItem(16, freyja);

        player.openInventory(inv);
    }

    public void giveNorseKit(Player player, String team) {
        // Limpiar efectos previos
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }

        // Limpiar equipamiento actual solo
        player.getInventory().setHelmet(null);
        player.getInventory().setChestplate(null);
        player.getInventory().setLeggings(null);
        player.getInventory().setBoots(null);

        switch (team.toLowerCase()) {
            case "odin":
                ItemStack[] odinSet = ItemUtils.generateOdinFullKit();
                player.getInventory().setHelmet(odinSet[1]);
                player.getInventory().setChestplate(odinSet[2]);
                player.getInventory().setLeggings(odinSet[3]);
                player.getInventory().setBoots(odinSet[4]);
                player.getInventory().setItem(0, odinSet[0]);

                // EFECTOS PERMANENTES DE ODÍN - Rey de Asgard
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 2)); // Fuerza III
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0)); // Visión nocturna
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1)); // Resistencia II
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0)); // Regeneración I
                player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, Integer.MAX_VALUE, 0)); // Respiración acuática

                // EFECTOS VISUALES ÉPICOS - Rayos de Asgard
                Location odinLoc = player.getLocation();
                for (int i = 0; i < 8; i++) {
                    final int count = i;
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        double angle = (count * Math.PI * 2) / 8;
                        Location rayLoc = odinLoc.clone().add(Math.cos(angle) * 2, 0, Math.sin(angle) * 2);
                        player.getWorld().strikeLightningEffect(rayLoc);
                    }, i * 3L);
                }
                player.playSound(player.getLocation(), org.bukkit.Sound.ENDERDRAGON_GROWL, 1.5f, 0.3f);
                player.playSound(player.getLocation(), org.bukkit.Sound.WITHER_SPAWN, 1.0f, 0.5f);

                player.sendMessage(MessageUtils.getColoredMessage("&8&l[&f&lOdín&8&l] &b✧ &f¡El poder del Padre de Todos fluye por tus venas!"));
                player.sendMessage(MessageUtils.getColoredMessage("&b&l➤ &7Efectos: &fFuerza III, Regeneración, Resistencia II, Visión nocturna"));
                break;

            case "thor":
                ItemStack[] thorSet = ItemUtils.generateThorFullKit();
                player.getInventory().setHelmet(thorSet[1]);
                player.getInventory().setChestplate(thorSet[2]);
                player.getInventory().setLeggings(thorSet[3]);
                player.getInventory().setBoots(thorSet[4]);
                player.getInventory().setItem(0, thorSet[0]);

                // Items extra para Thor
                player.getInventory().addItem(new ItemStack(Material.POTION, 3, (short) 8201)); // Poción de Fuerza (ej.)
                ItemStack uruFragment = new ItemStack(Material.IRON_INGOT);
                ItemMeta uruMeta = uruFragment.getItemMeta();
                uruMeta.setDisplayName(MessageUtils.getColoredMessage("&7Fragmento de Uru"));
                uruFragment.setItemMeta(uruMeta);
                player.getInventory().addItem(uruFragment);

                // EFECTOS PERMANENTES DE THOR - Dios del Trueno
                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, 3)); // Fuerza IV
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 2)); // Resistencia III
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0)); // Resistencia al fuego
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1)); // Velocidad II
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 2)); // Prisa III

                // EFECTOS VISUALES ÉPICOS - Tormentas de trueno
                for (int i = 0; i < 12; i++) {
                    final int thunderCount = i;
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        Location thunderLoc = player.getLocation().add((Math.random() - 0.5) * 6, 0, (Math.random() - 0.5) * 6);
                        player.getWorld().strikeLightningEffect(thunderLoc);
                        player.playSound(player.getLocation(), org.bukkit.Sound.ENDERDRAGON_GROWL, 1.0f, 0.5f);
                    }, thunderCount * 4L);
                }
                player.playSound(player.getLocation(), org.bukkit.Sound.AMBIENCE_THUNDER, 2.0f, 0.8f);

                player.sendMessage(MessageUtils.getColoredMessage("&e&l[&6&lThor&e&l] &c✯ &e¡El poder del trueno recorre tu cuerpo!"));
                player.sendMessage(MessageUtils.getColoredMessage("&c&l➤ &7Efectos: &fFuerza IV, Resistencia III, Velocidad II, Prisa III"));
                break;

            case "loki":
                ItemStack[] lokiSet = ItemUtils.generateLokiFullKit();
                player.getInventory().setHelmet(lokiSet[1]);
                player.getInventory().setChestplate(lokiSet[2]);
                player.getInventory().setLeggings(lokiSet[3]);
                player.getInventory().setBoots(lokiSet[4]);
                player.getInventory().setItem(0, lokiSet[0]);

                // Items extra para Loki
                player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 8));
                player.getInventory().addItem(new ItemStack(Material.POTION, 2, (short) 16430)); // Poción Invisibilidad Larga (ej. data value)
                player.getInventory().addItem(new ItemStack(Material.POTION, 4, (short) 16426)); // Poción Lentitud Arrojadiza (ej. data value)
                player.getInventory().addItem(new ItemStack(Material.WEB, 8));

                // EFECTOS PERMANENTES DE LOKI - Maestro del Engaño
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 3)); // Velocidad IV
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 2)); // Salto III
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0)); // Visión nocturna
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0)); // Invisibilidad
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 0)); // Debilidad (balance)

                // EFECTOS VISUALES ÉPICOS - Sombras y humo
                for (int i = 0; i < 20; i++) {
                    final int smokeCount = i;
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        for (int j = 0; j < 3; j++) {
                            Location smokeLoc = player.getLocation().add(Math.random() * 4 - 2, Math.random() * 3, Math.random() * 4 - 2);
                            player.getWorld().playEffect(smokeLoc, org.bukkit.Effect.SMOKE, 4);
                            player.getWorld().playEffect(smokeLoc, org.bukkit.Effect.ENDER_SIGNAL, 1);
                        }
                    }, smokeCount * 2L);
                }

                player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0f, 0.5f);
                player.playSound(player.getLocation(), Sound.GHAST_SCREAM, 0.8f, 1.5f);

                player.sendMessage(MessageUtils.getColoredMessage("&2&l[&a&lLoki&2&l] &a❈ &2¡Las sombras te abrazan, maestro del engaño!"));
                player.sendMessage(MessageUtils.getColoredMessage("&a&l➤ &7Efectos: &fVelocidad IV, Salto III, Invisibilidad, Sigilo"));
                break;

            case "freyja":
                ItemStack[] freyjaSet = ItemUtils.generateFreyjaFullKit();
                player.getInventory().setHelmet(freyjaSet[1]);
                player.getInventory().setChestplate(freyjaSet[2]);
                player.getInventory().setLeggings(freyjaSet[3]);
                player.getInventory().setBoots(freyjaSet[4]);
                player.getInventory().setItem(0, freyjaSet[0]);

                // Items extra para Freyja
                player.getInventory().addItem(new ItemStack(Material.ARROW, 64));
                player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 3));
                player.getInventory().addItem(new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1)); // Manzana Notch
                player.getInventory().addItem(new ItemStack(Material.POTION, 2, (short) 16389)); // Poción Regeneración Splash (ej. data value)

                // EFECTOS PERMANENTES DE FREYJA - Diosa del Amor y la Guerra
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 2)); // Regeneración III
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, Integer.MAX_VALUE, 2)); // Absorción III
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1)); // Resistencia II
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0)); // Resistencia al fuego
                player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, Integer.MAX_VALUE, 4)); // Vida extra V

                // EFECTOS VISUALES ÉPICOS - Corazones y luz dorada
                for (int i = 0; i < 15; i++) {
                    final int heartCount = i;
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        for (int j = 0; j < 5; j++) {
                            Location heartLoc = player.getLocation().add(Math.random() * 6 - 3, Math.random() * 3 + 1, Math.random() * 6 - 3);
                            player.getWorld().playEffect(heartLoc, org.bukkit.Effect.HEART, 1);
                            if (j % 2 == 0) {
                                player.getWorld().playEffect(heartLoc, org.bukkit.Effect.FIREWORKS_SPARK, 1);
                            }
                        }
                    }, heartCount * 3L);
                }

                player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.2f);
                player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.5f, 0.8f);

                player.sendMessage(MessageUtils.getColoredMessage("&5&l[&d&lFreyja&5&l] &d❀ &5¡La belleza y el poder de Vanaheim te protegen!"));
                player.sendMessage(MessageUtils.getColoredMessage("&d&l➤ &7Efectos: &fRegeneración III, Absorción III, Vida extra V"));
                break;
        }
    }

    private void fillBorder(Inventory inv, Material material, short data) {
        ItemStack border = new ItemStack(material, 1, data);
        for (int i = 0; i < inv.getSize(); i++) {
            if (i < 9 || i >= 18 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, border);
            }
        }
    }
}
