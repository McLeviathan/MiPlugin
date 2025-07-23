package mp.casino;

import mp.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RewardManager {
    
    public static void giveReward(Player player, Material winningMaterial) {
        int experience = 0;
        int goldNuggets = 0;
        
        // Determinar recompensas según el material ganador
        switch (winningMaterial) {
            case DIAMOND:
                experience = 50;
                goldNuggets = 5;
                break;
            case EMERALD:
                experience = 40;
                goldNuggets = 4;
                break;
            case GOLD_INGOT:
                experience = 30;
                goldNuggets = 3;
                break;
            case IRON_INGOT:
                experience = 20;
                goldNuggets = 2;
                break;
            case REDSTONE:
                experience = 10;
                goldNuggets = 1;
                break;
            default:
                experience = 5;
                goldNuggets = 1;
                break;
        }
        
        // Dar experiencia
        player.giveExp(experience);
        
        // Dar pepitas de oro
        ItemStack goldNuggetStack = new ItemStack(Material.GOLD_NUGGET, goldNuggets);
        
        // Verificar si el inventario tiene espacio
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(goldNuggetStack);
        } else {
            // Si no hay espacio, dropear los items
            player.getWorld().dropItemNaturally(player.getLocation(), goldNuggetStack);
            player.sendMessage(MessageUtils.getColoredMessage("&e⚠ Tu inventario está lleno. Los items se han dropeado en el suelo."));
        }
        
        // Mensaje de recompensa
        player.sendMessage(MessageUtils.getColoredMessage("&a&l💰 RECOMPENSA:"));
        player.sendMessage(MessageUtils.getColoredMessage("&b✦ &f+" + experience + " XP"));
        player.sendMessage(MessageUtils.getColoredMessage("&6✦ &f+" + goldNuggets + " Pepitas de Oro"));
    }
}