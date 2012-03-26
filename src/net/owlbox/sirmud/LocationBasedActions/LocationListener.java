package net.owlbox.sirmud.LocationBasedActions;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;

public class LocationListener implements Listener {

	private LocationBasedActions plugin;

	public LocationListener(LocationBasedActions plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		if (processRank(event))
			return;

		removeRecent(""); // ensure the lists are always given a chance to flush

		if (processItem(event))
			return;
	}

	private Boolean processItem(PlayerMoveEvent event) {
		Boolean ret = false;
		Player pl = event.getPlayer();
		PermissionUser pex_pl = plugin.pex.getUser(pl);
		String[] pgnames = pex_pl.getGroupsNames();

		for (String pg : pgnames) {
			if (plugin.item_locs.containsKey(pg)) {
				// We have location(s) defined for this group
				Location to = new TinyLocation(event.getTo()).toLocation();

				for (LocationR locr : plugin.item_locs.get(pg)) {
					if (locr.loc.equals(to)) {
						ret = true;
						// Moving into a location we've specified.
						if (!locr.recent_items.contains(pl.getName())) {

							String itemGiveconf = plugin.getConfig().getString(
									"strings.itemgive",
									"You have been given %item, nice!");

							String itemGive = itemGiveconf.replace("%item",
									new ItemStack(locr.item, locr.itemamt)
											.toString());
							pl.sendMessage(ChatColor.GREEN + itemGive);
							plugin.log.info(pl.getName() + " was given "
									+ Integer.toString(locr.itemamt) + " of "
									+ Integer.toString(locr.item));
							pl.getInventory().addItem(
									new ItemStack(locr.item, locr.itemamt));
							locr.recent_items.add(pl.getName());
						}

					}
				}

			}
		}
		return ret;
	}

	private void removeRecent(String pname) {
		for (List<LocationR> n : plugin.item_locs.values()) {
			for (LocationR m : n) {
				if (m.recent_items.contains(pname))
					m.recent_items.remove(pname);
				if (m.recent_items.size() > 10)
					m.recent_items.remove(0);
			}
		}

	}

	private Boolean processRank(PlayerMoveEvent event) {
		Boolean ret = false;
		Player pl = event.getPlayer();
		PermissionUser pex_pl = plugin.pex.getUser(pl);
		String[] pgnames = pex_pl.getGroupsNames();

		for (String pg : pgnames) {
			if (plugin.rank_locs.containsKey(pg)) {
				// We have location(s) defined for this group
				Location to = new TinyLocation(event.getTo()).toLocation();

				for (LocationR locr : plugin.rank_locs.get(pg)) {
					if (locr.loc.equals(to)) {
						ret = true;
						// Moving into a location we've specified.
						PermissionGroup group = plugin.pex
								.getGroup(locr.rankto);
						if (group != null) {
							String rankUpconf = plugin
									.getConfig()
									.getString("strings.rankchange",
											"You have become a %rank! Congratulations!");
							pex_pl.setGroups(new PermissionGroup[] { group });
							String rankUp = rankUpconf.replace("%rank",
									locr.rankto);
							pl.sendMessage(ChatColor.GREEN + rankUp);
							plugin.log.info(pl.getName() + " became a "
									+ locr.rankto);
							// When rank changes player name
							// is removed from recent item
							// recipients list.
							removeRecent(pl.getName());

							if (plugin.getConfig().getBoolean(
									"config.autorank.announce-change", false) == true) {
								String announcestr = plugin
										.getConfig()
										.getString("strings.rankchange-server",
												"Everyone welcome our newest %rank, %player!");
								announcestr = announcestr.replace("%player",
										ChatColor.BLUE + pl.getDisplayName()
												+ ChatColor.WHITE);
								announcestr = announcestr.replace("%rank",
										ChatColor.GOLD + locr.rankto
												+ ChatColor.WHITE);
								Bukkit.broadcastMessage(announcestr);
							}
						} else {
							pl.sendMessage(ChatColor.RED
									+ "Oops, something went wrong, please contact staff.");
							plugin.log.severe("ERROR: " + pl.getName()
									+ " was attempting to rank to "
									+ locr.rankto
									+ " but the group didn't exist!");
						}
					}
				}

			}
		}
		return ret;
	}
}
