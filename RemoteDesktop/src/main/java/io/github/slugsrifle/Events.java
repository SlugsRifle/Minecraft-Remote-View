package io.github.slugsrifle;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import static io.github.slugsrifle.Main.*;

import java.util.Collection;
import java.util.UUID;

public class Events implements Listener {

	private final Main plugin;

	public Events(Main instance) {
		plugin = instance;
	}

	public long getDistance(Location l) {
		long x = l.getBlockX(), y = l.getBlockY(), z = l.getBlockZ();
		return x * x + y * y + z * z;
	}

	public void removeRender(MapView mv) {
		for (MapRenderer mr : mv.getRenderers()) {
			mv.removeRenderer(mr);
		}
	}

	@EventHandler
	public void onMapInitializeEvent(MapInitializeEvent e) {
		MapView mv = e.getMap();
		plugin.getLogger().info("Map ID: " + mv.getId() + " :: Init");
		if (mv.getId() < getSize()) {
			mapView.put(mv.getId(), mv);
			plugin.getLogger().info("Map ID: " + mv.getId() + " :: Init");
			removeRender(mv);
		}
	}

	/*
	 * public void onTouch(Player p, Action a) { ItemStack is =
	 * p.getInventory().getItemInMainHand(); if (is.getType() == Material.STICK) {
	 * ItemMeta im = is.getItemMeta(); List<String> lore = im.getLore(); if
	 * (lore.size() > 0) { if (lore.get(0).equalsIgnoreCase("Touch Stick")) { World
	 * w = p.getWorld(); Snowball sb = (Snowball) w.spawnEntity(p.getEyeLocation(),
	 * EntityType.SNOWBALL); sb.setGravity(false);
	 * sb.setVelocity(p.getEyeLocation().getDirection()); // 좌클릭시 if (a ==
	 * Action.LEFT_CLICK_AIR || a == Action.LEFT_CLICK_BLOCK) {
	 * sb.setCustomName("Left"); } else if (a == Action.RIGHT_CLICK_AIR || a ==
	 * Action.RIGHT_CLICK_BLOCK) { sb.setCustomName("Right"); }
	 * logger.info("touch"); } } } }
	 */

	/*
	 * @EventHandler public void onProjectileHit(ProjectileHitEvent e) { if
	 * (e.getEntityType() == EntityType.SNOWBALL) { Snowball sb = (Snowball)
	 * e.getEntity(); if (sb.getCustomName().equals("Left")) { logger.info("Left " +
	 * sb.getLocation().subtract(100, 24, 240).toString()); } else if
	 * (sb.getCustomName().equals("Right")) { logger.info("Right " +
	 * sb.getLocation().subtract(100, 24, 240).toString()); } // logger.info("hit");
	 * } }
	 */

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
		if (e.getDamager().getType() == EntityType.PLAYER) {
			Player p = (Player) e.getDamager();
			if (e.getEntity().getType() == EntityType.ITEM_FRAME) {
				ItemFrame itemFrame = (ItemFrame) e.getEntity();
				if (itemFrame.getItem().getType() == Material.FILLED_MAP) {
					// onTouch(p, Action.LEFT_CLICK_AIR);
					if (p.isOp() && p.getInventory().getItemInMainHand().getType() == Material.STICK) {
						return;
					}
					e.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		// Player p = e.getPlayer();
		Block b = e.getBlock();
		World w = b.getWorld();
		// logger.info(b.getType() + " ");
		Collection<ItemFrame> l = w.getEntitiesByClass(ItemFrame.class);
		long count = l.parallelStream().filter(itemFrame -> {
			BlockFace bf = itemFrame.getFacing();
			Location loc = itemFrame.getLocation().getBlock().getLocation();
			double distance = 0;
			switch (bf) {
			case EAST:
				loc.add(-1, 0, 0);
				break;
			case WEST:
				loc.add(1, 0, 0);				
				break;
			case NORTH:
				loc.add(0, 0, 1);				
				break;
			case SOUTH:
				loc.add(0, 0, -1);				
				break;
			default:
				break;
			}
			distance = loc.distance(b.getLocation());
			//e.getPlayer().sendMessage(prefix + bf + " " + distance);
			return distance == 0;
		}).count();
		if (count > 0) {
			e.setCancelled(true);
		}
		// logger.info(c + " ");
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		if (e.getRightClicked().getType() == EntityType.ITEM_FRAME) {
			ItemFrame itemFrame = (ItemFrame) e.getRightClicked();
			ItemStack is = itemFrame.getItem();
			Material m = is.getType();
			// logger.info(m + " ");
			if (m == Material.MAP || m == Material.FILLED_MAP) {
				// onTouch(e.getPlayer(), Action.RIGHT_CLICK_AIR);
				e.setCancelled(true);
			}
		}
		// logger.info(e.getRightClicked().getType() + " ");
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		UUID u = p.getUniqueId();
		Location l;
		if (p.isOp() && p.getInventory().getItemInMainHand().getType() == Material.STICK) {
			switch (e.getAction()) {
			case LEFT_CLICK_BLOCK:
				l = e.getClickedBlock().getLocation();
				pos1.put(u, l);
				p.sendMessage(prefix + "Pos1 Selected :: " + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ());
				break;
			case RIGHT_CLICK_BLOCK:
				l = e.getClickedBlock().getLocation();
				pos2.put(u, l);
				if (e.getHand() == EquipmentSlot.HAND) { // If the event is fired by HAND (main hand)
					p.sendMessage(
							prefix + "Pos2 Selected :: " + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ());
				}
				break;
			default:
				break;
			}
			e.setCancelled(true);
		}
	}

	/*
	 * @EventHandler public void
	 * onPlayerInteractAtEntity(PlayerInteractAtEntityEvent e) {
	 * logger.info(e.getRightClicked().getType() + " AT"); }
	 */

}
