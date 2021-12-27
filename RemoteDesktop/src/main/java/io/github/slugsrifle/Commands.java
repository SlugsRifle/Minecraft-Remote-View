package io.github.slugsrifle;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class Commands implements CommandExecutor {

	public Commands() {
	}

	public int getBlockDistance(Location l1, Location l2) {
		int x = l1.getBlockX() - l2.getBlockX();
		int y = l1.getBlockY() - l2.getBlockY();
		int z = l1.getBlockZ() - l2.getBlockZ();
		return x * x + y * y + z * z;
	}

	public boolean isItemFrameExist(Location l) {
		long c = l.getWorld().getEntitiesByClass(ItemFrame.class).parallelStream().filter(i -> {
			int distance = getBlockDistance(i.getLocation(), l);
			// p.sendMessage(prefix + distance);
			return distance == 0;
		}).count();
		return c == 0;
	}

	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
		if (sender instanceof Player p) {
			UUID u = p.getUniqueId();
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("wand")) {
					ItemStack item = new ItemStack(Material.STICK);
					p.getInventory().addItem(item);
				} else if (args[0].equalsIgnoreCase("build")) {
					if (Main.pos1.containsKey(u) || Main.pos2.containsKey(u)) {
						Location lp = p.getLocation();
						Location l1 = Main.pos1.get(u);
						Location l2 = Main.pos2.get(u);
						Location lc = l1.clone().subtract(l2);

						float yaw = -lp.getYaw();

						int xl = Math.min(l1.getBlockX(), l2.getBlockX());
						int xh = l1.getBlockX() + l2.getBlockX() - xl;
						int yl = Math.min(l1.getBlockY(), l2.getBlockY());
						int yh = l1.getBlockY() + l2.getBlockY() - yl;
						int zl = Math.min(l1.getBlockZ(), l2.getBlockZ());
						int zh = l1.getBlockZ() + l2.getBlockZ() - zl;

						if (lc.getBlockX() == 0 && lc.getBlockZ() == 0) {
							for (int y = 0; y < yh - yl + 1; ++y) {
								Location l = new Location(lp.getWorld(), xl, yl + y, zl);
								l.getBlock().setType(Material.SPRUCE_PLANKS);
								//p.sendMessage(prefix.concat("Yaw :: ") + yaw);
								if (315 < yaw || yaw < 45) {
									l.add(0, 0, -1);
								} else if (135 < yaw && yaw < 225) {
									l.add(0, 0, 1);
								} else if (225 < yaw && yaw < 315) {
									l.add(1, 0, 0);
								} else if (45 < yaw && yaw < 135) {
									l.add(-1, 0, 0);
								}

								if (isItemFrameExist(l) && l.getBlock().getType() == Material.AIR) {
									ItemFrame itemFrame = l.getWorld().spawn(l, ItemFrame.class);
									ItemStack item = itemFrame.getItem();
									item.setType(Material.FILLED_MAP);
									MapMeta mapMeta = (MapMeta) item.getItemMeta();
									mapMeta.setMapView(Main.mapView.get((zh - zl - y)));
									item.setItemMeta(mapMeta);
									itemFrame.setItem(item);
								}
							}
						} else if (lc.getBlockX() == 0) {
							for (int y = 0; y < yh - yl + 1; ++y) {
								for (int z = 0; z < zh - zl + 1; ++z) {
									boolean reverse = false;
									Location l = new Location(lp.getWorld(), xl, yl + y, zl + z);
									l.getBlock().setType(Material.SPRUCE_PLANKS);
									//p.sendMessage(prefix.concat("Yaw :: ") + yaw);
									if (0 < yaw && yaw < 180) {
										l.add(-1, 0, 0);
									} else if (180 < yaw && yaw < 360) {
										reverse = true;
										l.add(1, 0, 0);
									}
									if (isItemFrameExist(l) && l.getBlock().getType() == Material.AIR) {
										ItemFrame itemFrame = l.getWorld().spawn(l, ItemFrame.class);
										ItemStack item = itemFrame.getItem();
										item.setType(Material.FILLED_MAP);
										MapMeta mapMeta = (MapMeta) item.getItemMeta();
										if (!reverse) {
											mapMeta.setMapView(Main.mapView.get((yh - yl - y) * (zh - zl + 1) + z));
										} else {
											mapMeta.setMapView(Main.mapView.get((yh - yl - y) * (zh - zl + 1) + zh - zl - z));
										}
										item.setItemMeta(mapMeta);
										itemFrame.setItem(item);
									}
								}
							}
						} else if (lc.getBlockZ() == 0) {
							for (int y = 0; y < yh - yl + 1; ++y) {
								for (int x = 0; x < xh - xl + 1; ++x) {
									Location l = new Location(lp.getWorld(), xl + x, yl + y, zl);
									boolean reverse = false;
									l.getBlock().setType(Material.SPRUCE_PLANKS);
									//p.sendMessage(prefix.concat("Yaw :: ") + yaw);
									if (270 < yaw || yaw < 90) {
										reverse = true;
										l.add(0, 0, -1);
									} else if (90 < yaw && yaw < 270) {
										l.add(0, 0, 1);
									}
									if (isItemFrameExist(l) && l.getBlock().getType() == Material.AIR) {
										ItemFrame itemFrame = l.getWorld().spawn(l, ItemFrame.class);
										ItemStack item = itemFrame.getItem();
										item.setType(Material.FILLED_MAP);
										MapMeta mapMeta = (MapMeta) item.getItemMeta();
										if (!reverse) {
											mapMeta.setMapView(Main.mapView.get((yh - yl - y) * (xh - xl + 1) + x));
										} else {
											mapMeta.setMapView(Main.mapView.get((yh - yl - y) * (xh - xl + 1) + xh - xl - x));
										}
										item.setItemMeta(mapMeta);
										itemFrame.setItem(item);
									}
								}
							}
						}
					} else {
						p.sendMessage(Main.prefix.concat("Pos1 Or Pos2 Not Selected"));
					}
				}
			}
		}
		return true;
	}

}
