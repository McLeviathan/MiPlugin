package mp.utils;

import com.google.common.collect.Lists;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ItemUtils {

    public static ItemStack generateEsmeralditem(int amount) {
        ItemStack item = new ItemStack(Material.EMERALD, amount);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(MessageUtils.getColoredMessage("&aEsmeralda Epíca"));

        List<String> lore = new ArrayList<>();
        lore.add(MessageUtils.getColoredMessage("&7Esta Esmeralda hace cositas..."));
        lore.add(MessageUtils.getColoredMessage("&7Cositas Epicas..."));
        lore.add("");
        lore.add(MessageUtils.getColoredMessage("&fNivel: &a73"));
        meta.setLore(lore);
        item.setItemMeta(meta);

        meta.addEnchant(Enchantment.DAMAGE_ALL,10,true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack generateEpicSword() {
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD, 1);
        ItemMeta meta = sword.getItemMeta();
        meta.setDisplayName(MessageUtils.getColoredMessage("&bEspada Épica"));
        List<String> lore = new ArrayList<>();
        lore.add(MessageUtils.getColoredMessage("&7Espada legendaria con poder descomunal"));
        lore.add(MessageUtils.getColoredMessage("&7Filo X & Aspecto Ígneo X"));
        meta.setLore(lore);
        meta.addEnchant(Enchantment.DAMAGE_ALL, 10, true); // Filo X
        meta.addEnchant(Enchantment.FIRE_ASPECT, 10, true); // Aspecto Ígneo X
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        sword.setItemMeta(meta);
        return sword;
    }

    public static ItemStack generateRandomEpicArmor() {
        Random random = new Random();
        Material[] armorTypes = {
            Material.DIAMOND_HELMET,
            Material.DIAMOND_CHESTPLATE,
            Material.DIAMOND_LEGGINGS,
            Material.DIAMOND_BOOTS
        };

        Material selectedType = armorTypes[random.nextInt(armorTypes.length)];
        ItemStack armor = new ItemStack(selectedType);
        ItemMeta meta = armor.getItemMeta();

        String pieceName = selectedType.name().replace("DIAMOND_", "").toLowerCase();
        meta.setDisplayName(MessageUtils.getColoredMessage("&b✧ &3Armadura Épica &7(" + pieceName + ") &b✧"));

        List<String> lore = new ArrayList<>();
        lore.add(MessageUtils.getColoredMessage("&8▪ &7Forjada en las profundidades"));
        lore.add(MessageUtils.getColoredMessage("&8▪ &7Protección ancestral"));
        lore.add("");
        lore.add(MessageUtils.getColoredMessage("&d❈ &fEncantamientos especiales:"));

        // Añadir encantamientos aleatorios
        if (random.nextBoolean()) {
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
            lore.add(MessageUtils.getColoredMessage("&7• Protección IV"));
        }
        if (random.nextBoolean()) {
            meta.addEnchant(Enchantment.DURABILITY, 3, true);
            lore.add(MessageUtils.getColoredMessage("&7• Irrompible III"));
        }
        if (random.nextBoolean()) {
            meta.addEnchant(Enchantment.THORNS, 3, true);
            lore.add(MessageUtils.getColoredMessage("&7• Espinas III"));
        }

        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        armor.setItemMeta(meta);
        return armor;
    }

    public static ItemStack generateSpecialItem(String type) {
        switch(type.toLowerCase()) {
            case "bow":
                return generateEpicBow();
            case "axe":
                return generateEpicAxe();
            case "boots":
                return generateSpeedBoots();
            default:
                return null;
        }
    }

    private static ItemStack generateEpicBow() {
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta meta = bow.getItemMeta();
        meta.setDisplayName(MessageUtils.getColoredMessage("&d☆ &5Arco del Poder &d☆"));
        List<String> lore = new ArrayList<>();
        lore.add(MessageUtils.getColoredMessage("&8▪ &7Forjado con magia antigua"));
        lore.add(MessageUtils.getColoredMessage("&8▪ &7Poder devastador"));
        lore.add("");
        lore.add(MessageUtils.getColoredMessage("&d❈ &fEncantamientos:"));
        lore.add(MessageUtils.getColoredMessage("&7• Poder V"));
        lore.add(MessageUtils.getColoredMessage("&7• Llama II"));
        lore.add(MessageUtils.getColoredMessage("&7• Infinidad I"));

        meta.addEnchant(Enchantment.ARROW_DAMAGE, 5, true);
        meta.addEnchant(Enchantment.ARROW_FIRE, 2, true);
        meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        bow.setItemMeta(meta);
        return bow;
    }

    public static ItemStack generateEpicAxe() {
        ItemStack axe = new ItemStack(Material.DIAMOND_AXE);
        ItemMeta meta = axe.getItemMeta();
        meta.setDisplayName(MessageUtils.getColoredMessage("&c⚔ &4Hacha del Berserker &c⚔"));
        List<String> lore = new ArrayList<>();
        lore.add(MessageUtils.getColoredMessage("&8▪ &7Imbuida con furia ancestral"));
        lore.add("");
        lore.add(MessageUtils.getColoredMessage("&d❈ &fEncantamientos:"));
        lore.add(MessageUtils.getColoredMessage("&7• Filo V"));
        lore.add(MessageUtils.getColoredMessage("&7• Irrompible III"));
        lore.add(MessageUtils.getColoredMessage("&7• Toque de fuego II"));

        meta.addEnchant(Enchantment.DAMAGE_ALL, 5, true);
        meta.addEnchant(Enchantment.DURABILITY, 3, true);
        meta.addEnchant(Enchantment.FIRE_ASPECT, 2, true);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        axe.setItemMeta(meta);
        return axe;
    }

    private static ItemStack generateSpeedBoots() {
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta meta = (LeatherArmorMeta) boots.getItemMeta();
        meta.setColor(Color.AQUA);
        meta.setDisplayName(MessageUtils.getColoredMessage("&b❋ &3Botas del Viento &b❋"));

        List<String> lore = new ArrayList<>();
        lore.add(MessageUtils.getColoredMessage("&8▪ &7Ligeras como el viento"));
        lore.add(MessageUtils.getColoredMessage("&8▪ &7Velocidad aumentada"));
        lore.add("");
        lore.add(MessageUtils.getColoredMessage("&d❈ &fEncantamientos:"));
        lore.add(MessageUtils.getColoredMessage("&7• Protección III"));
        lore.add(MessageUtils.getColoredMessage("&7• Agilidad III"));
        lore.add(MessageUtils.getColoredMessage("&7• Pluma III"));

        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3, true);
        meta.addEnchant(Enchantment.PROTECTION_FALL, 3, true);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        boots.setItemMeta(meta);
        return boots;
    }

    public static ItemStack generateGungnir() {
        ItemStack spear = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = spear.getItemMeta();
        meta.setDisplayName(MessageUtils.getColoredMessage("&8☄ &7Lanza Gungnir &8☄"));
        List<String> lore = new ArrayList<>();
        lore.add(MessageUtils.getColoredMessage("&7La lanza infalible del Padre de Todo."));
        lore.add(MessageUtils.getColoredMessage("&8▪ &fForjada por los enanos maestros."));
        lore.add(MessageUtils.getColoredMessage("&8▪ &fSiempre alcanza su objetivo."));
        lore.add("");
        lore.add(MessageUtils.getColoredMessage("&d❈ &fEncantamientos de Odín:"));
        lore.add(MessageUtils.getColoredMessage("&7• Filo V"));
        lore.add(MessageUtils.getColoredMessage("&7• Irrompible III"));
        lore.add(MessageUtils.getColoredMessage("&7• Empuje I"));
        meta.addEnchant(Enchantment.DAMAGE_ALL, 5, true);
        meta.addEnchant(Enchantment.DURABILITY, 3, true);
        meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        spear.setItemMeta(meta);
        return spear;
    }

    public static ItemStack[] generateOdinFullKit() {
        ItemStack[] kit = new ItemStack[5]; // Arma, Casco, Pechera, Pantalones, Botas + items extra
        kit[0] = generateGungnir(); // Arma principal

        // Armadura Majestuosa de Diamante
        ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
        ItemMeta helmetMeta = helmet.getItemMeta();
        helmetMeta.setDisplayName(MessageUtils.getColoredMessage("&b⚡ &fYelmo del Padre de Todo &b⚡"));
        helmetMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
        helmetMeta.addEnchant(Enchantment.DURABILITY, 3, true);
        helmet.setItemMeta(helmetMeta);
        kit[1] = helmet;

        ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemMeta chestMeta = chestplate.getItemMeta();
        chestMeta.setDisplayName(MessageUtils.getColoredMessage("&b⚡ &fCoraza del Rey de Asgard &b⚡"));
        chestMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
        chestMeta.addEnchant(Enchantment.DURABILITY, 3, true);
        chestplate.setItemMeta(chestMeta);
        kit[2] = chestplate;

        ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
        ItemMeta legMeta = leggings.getItemMeta();
        legMeta.setDisplayName(MessageUtils.getColoredMessage("&b⚡ &fGrebas de la Sabiduría &b⚡"));
        legMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
        legMeta.addEnchant(Enchantment.DURABILITY, 3, true);
        leggings.setItemMeta(legMeta);
        kit[3] = leggings;

        ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
        ItemMeta bootsMeta = boots.getItemMeta();
        bootsMeta.setDisplayName(MessageUtils.getColoredMessage("&b⚡ &fBotas del Viajero Interdimensional &b⚡"));
        bootsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
        bootsMeta.addEnchant(Enchantment.PROTECTION_FALL, 4, true);
        bootsMeta.addEnchant(Enchantment.DEPTH_STRIDER, 3, true); // Para respiración acuática
        bootsMeta.addEnchant(Enchantment.DURABILITY, 3, true);
        boots.setItemMeta(bootsMeta);
        kit[4] = boots;

        // Items extra se darán en MenuInventoryManager
        return kit;
    }

    public static ItemStack generateMjolnir() {
        ItemStack hammer = new ItemStack(Material.DIAMOND_AXE); // Hacha como martillo
        ItemMeta meta = hammer.getItemMeta();
        meta.setDisplayName(MessageUtils.getColoredMessage("&e⚡ &6Martillo Mjolnir &e⚡"));
        List<String> lore = new ArrayList<>();
        lore.add(MessageUtils.getColoredMessage("&7El legendario martillo del Dios del Trueno."));
        lore.add(MessageUtils.getColoredMessage("&8▪ &fSolo los dignos pueden empuñarlo."));
        lore.add(MessageUtils.getColoredMessage("&8▪ &fInvoca el poder de la tormenta."));
        lore.add("");
        lore.add(MessageUtils.getColoredMessage("&d❈ &fEncantamientos de Thor:"));
        lore.add(MessageUtils.getColoredMessage("&7• Filo VI"));
        lore.add(MessageUtils.getColoredMessage("&7• Irrompible III"));
        lore.add(MessageUtils.getColoredMessage("&7• Fortuna III (Impacto poderoso)"));
        meta.addEnchant(Enchantment.DAMAGE_ALL, 6, true);
        meta.addEnchant(Enchantment.DURABILITY, 3, true);
        meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 3, true); // Simula poder
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        hammer.setItemMeta(meta);
        return hammer;
    }

    public static ItemStack[] generateThorFullKit() {
        ItemStack[] kit = new ItemStack[5];
        kit[0] = generateMjolnir();

        // Armadura de Guerrero de Hierro
        ItemStack helmet = new ItemStack(Material.IRON_HELMET);
        ItemMeta helmetMeta = helmet.getItemMeta();
        helmetMeta.setDisplayName(MessageUtils.getColoredMessage("&e⚡ &cYelmo de Batalla de Thor &e⚡"));
        helmetMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
        helmetMeta.addEnchant(Enchantment.DURABILITY, 3, true);
        helmet.setItemMeta(helmetMeta);
        kit[1] = helmet;

        ItemStack chestplate = new ItemStack(Material.IRON_CHESTPLATE);
        ItemMeta chestMeta = chestplate.getItemMeta();
        chestMeta.setDisplayName(MessageUtils.getColoredMessage("&e⚡ &cCoraza del Trueno &e⚡"));
        chestMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
        chestMeta.addEnchant(Enchantment.THORNS, 2, true);
        chestMeta.addEnchant(Enchantment.DURABILITY, 3, true);
        chestplate.setItemMeta(chestMeta);
        kit[2] = chestplate;

        ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS);
        ItemMeta legMeta = leggings.getItemMeta();
        legMeta.setDisplayName(MessageUtils.getColoredMessage("&e⚡ &cMusleras del Campeón &e⚡"));
        legMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
        legMeta.addEnchant(Enchantment.DURABILITY, 3, true);
        leggings.setItemMeta(legMeta);
        kit[3] = leggings;

        ItemStack boots = new ItemStack(Material.IRON_BOOTS);
        ItemMeta bootsMeta = boots.getItemMeta();
        bootsMeta.setDisplayName(MessageUtils.getColoredMessage("&e⚡ &cBotas de Guerra Asgardianas &e⚡"));
        bootsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
        bootsMeta.addEnchant(Enchantment.PROTECTION_FALL, 4, true);
        bootsMeta.addEnchant(Enchantment.DURABILITY, 3, true);
        boots.setItemMeta(bootsMeta);
        kit[4] = boots;
        return kit;
    }

    public static ItemStack generateLokisDagger() {
        ItemStack dagger = new ItemStack(Material.GOLD_SWORD);
        ItemMeta meta = dagger.getItemMeta();
        meta.setDisplayName(MessageUtils.getColoredMessage("&a🗡 &2Daga de las Sombras &a🗡"));
        List<String> lore = new ArrayList<>();
        lore.add(MessageUtils.getColoredMessage("&7Susurra mentiras y confunde a tus enemigos."));
        lore.add(MessageUtils.getColoredMessage("&8▪ &fImbuida con magia ilusoria."));
        lore.add(MessageUtils.getColoredMessage("&8▪ &fPerfecta para ataques furtivos."));
        lore.add("");
        lore.add(MessageUtils.getColoredMessage("&d❈ &fEncantamientos de Loki:"));
        lore.add(MessageUtils.getColoredMessage("&7• Filo IV"));
        lore.add(MessageUtils.getColoredMessage("&7• Empuje II"));
        lore.add(MessageUtils.getColoredMessage("&7• Irrompible III"));
        meta.addEnchant(Enchantment.DAMAGE_ALL, 4, true);
        meta.addEnchant(Enchantment.KNOCKBACK, 2, true);
        meta.addEnchant(Enchantment.DURABILITY, 3, true);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        dagger.setItemMeta(meta);
        return dagger;
    }

    public static ItemStack[] generateLokiFullKit() {
        ItemStack[] kit = new ItemStack[5];
        kit[0] = generateLokisDagger();

        // Armadura de Cuero Teñido Verde Oscuro o Cota de Malla
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
        helmetMeta.setColor(Color.fromRGB(0, 100, 0)); // Verde oscuro
        helmetMeta.setDisplayName(MessageUtils.getColoredMessage("&a🌿 &2Capucha del Embaucador &a🌿"));
        helmetMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true);
        helmetMeta.addEnchant(Enchantment.DURABILITY, 3, true);
        helmet.setItemMeta(helmetMeta);
        kit[1] = helmet;

        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta chestMeta = (LeatherArmorMeta) chestplate.getItemMeta();
        chestMeta.setColor(Color.fromRGB(0, 100, 0));
        chestMeta.setDisplayName(MessageUtils.getColoredMessage("&a🌿 &2Túnica de Secretos &a🌿"));
        chestMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true);
        chestMeta.addEnchant(Enchantment.DURABILITY, 3, true);
        chestplate.setItemMeta(chestMeta);
        kit[2] = chestplate;

        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta legMeta = (LeatherArmorMeta) leggings.getItemMeta();
        legMeta.setColor(Color.fromRGB(0, 100, 0));
        legMeta.setDisplayName(MessageUtils.getColoredMessage("&a🌿 &2Pantalones del Sigilo &a🌿"));
        legMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true);
        legMeta.addEnchant(Enchantment.DURABILITY, 3, true);
        leggings.setItemMeta(legMeta);
        kit[3] = leggings;

        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
        bootsMeta.setColor(Color.fromRGB(0, 100, 0));
        bootsMeta.setDisplayName(MessageUtils.getColoredMessage("&a🌿 &2Botas Ligeras del Engaño &a🌿"));
        bootsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true);
        bootsMeta.addEnchant(Enchantment.PROTECTION_FALL, 4, true);
        bootsMeta.addEnchant(Enchantment.DURABILITY, 3, true);
        boots.setItemMeta(bootsMeta);
        kit[4] = boots;
        return kit;
    }

    public static ItemStack generateFreyjasBow() {
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta meta = bow.getItemMeta();
        meta.setDisplayName(MessageUtils.getColoredMessage("&d✿ &5Arco Floral de Freyja &d✿"));
        List<String> lore = new ArrayList<>();
        lore.add(MessageUtils.getColoredMessage("&7Imbuido con la magia Seiðr de la Diosa."));
        lore.add(MessageUtils.getColoredMessage("&8▪ &fSus flechas cantan con poder vital y protector."));
        lore.add("");
        lore.add(MessageUtils.getColoredMessage("&d❈ &fEncantamientos divinos:"));
        lore.add(MessageUtils.getColoredMessage("&7• Poder V"));
        lore.add(MessageUtils.getColoredMessage("&7• Llama II"));
        lore.add(MessageUtils.getColoredMessage("&7• Infinidad I"));
        meta.addEnchant(Enchantment.ARROW_DAMAGE, 5, true);
        meta.addEnchant(Enchantment.ARROW_FIRE, 2, true);
        meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        bow.setItemMeta(meta);
        return bow;
    }

    public static ItemStack[] generateFreyjaFullKit() {
        ItemStack[] kit = new ItemStack[5];
        kit[0] = generateFreyjasBow();

        // Armadura Dorada Elegante
        ItemStack helmet = new ItemStack(Material.GOLD_HELMET);
        ItemMeta helmetMeta = helmet.getItemMeta();
        helmetMeta.setDisplayName(MessageUtils.getColoredMessage("&d✿ &eDiadema de la Valquiria &d✿"));
        helmetMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3, true);
        helmetMeta.addEnchant(Enchantment.DURABILITY, 4, true);
        helmet.setItemMeta(helmetMeta);
        kit[1] = helmet;

        ItemStack chestplate = new ItemStack(Material.GOLD_CHESTPLATE);
        ItemMeta chestMeta = chestplate.getItemMeta();
        chestMeta.setDisplayName(MessageUtils.getColoredMessage("&d✿ &eCoraza de Plumas Doradas &d✿"));
        chestMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3, true);
        chestMeta.addEnchant(Enchantment.DURABILITY, 4, true);
        chestplate.setItemMeta(chestMeta);
        kit[2] = chestplate;

        ItemStack leggings = new ItemStack(Material.GOLD_LEGGINGS);
        ItemMeta legMeta = leggings.getItemMeta();
        legMeta.setDisplayName(MessageUtils.getColoredMessage("&d✿ &eFalda de Batalla de Vanaheim &d✿"));
        legMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3, true);
        legMeta.addEnchant(Enchantment.DURABILITY, 4, true);
        leggings.setItemMeta(legMeta);
        kit[3] = leggings;

        ItemStack boots = new ItemStack(Material.GOLD_BOOTS);
        ItemMeta bootsMeta = boots.getItemMeta();
        bootsMeta.setDisplayName(MessageUtils.getColoredMessage("&d✿ &eSandalias Bendecidas &d✿"));
        bootsMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3, true);
        bootsMeta.addEnchant(Enchantment.PROTECTION_FALL, 3, true);
        bootsMeta.addEnchant(Enchantment.DURABILITY, 4, true);
        boots.setItemMeta(bootsMeta);
        kit[4] = boots;
        return kit;
    }
}
