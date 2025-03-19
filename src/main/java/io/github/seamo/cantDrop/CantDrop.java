package io.github.seamo.cantDrop;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class CantDrop extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerBrokenItem(PlayerItemBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack stick = new ItemStack(org.bukkit.Material.STICK);
        ItemMeta meta = stick.getItemMeta();
        meta.setUnbreakable(true);
        meta.setItemName("부서진 아이템" + System.currentTimeMillis());
        stick.setItemMeta(meta);
        getServer().getScheduler().runTaskLater(this, () -> player.getInventory().addItem(stick), 1L);
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        if (event.getRecipe().getResult().getType() == org.bukkit.Material.CHEST || event.getRecipe().getResult().getType() == org.bukkit.Material.BARREL || event.getRecipe().getResult().getType() == Material.ENDER_CHEST) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            player.getWorld().strikeLightning(player.getLocation());
            for (int i = 0; i < 50; i++) {
                player.getWorld().spawnEntity(player.getLocation(), org.bukkit.entity.EntityType.values()[(int) (Math.random() * org.bukkit.entity.EntityType.values().length)]);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(org.bukkit.event.entity.PlayerDeathEvent event) {
        event.setKeepInventory(true);
        Player player = event.getPlayer();
        event.getDrops().clear();
    }

    @EventHandler
    public void ChestPick(PlayerPickupItemEvent event) {
        ItemStack item = event.getItem().getItemStack();
        if (item.getType() == org.bukkit.Material.CHEST) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory topInventory = event.getView().getTopInventory(); // 열린 상단 인벤토리 (상자 등)
        InventoryType topType = topInventory.getType(); // 열린 인벤토리 유형

        // 상자가 열려 있는 상태에서 플레이어 인벤토리 조작 금지
        if (topType == InventoryType.CHEST) {
            if (event.getClickedInventory() != null && event.getClickedInventory().getType() == InventoryType.PLAYER) {
                event.setCancelled(true);
            }
        }
    }
}