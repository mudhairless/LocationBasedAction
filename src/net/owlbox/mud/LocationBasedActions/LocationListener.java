/*This file is part of LocationBasedActions.

    LocationBasedActions is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    LocationBasedActions is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with LocationBasedActions.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.owlbox.mud.LocationBasedActions;

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

	private final LocationBasedActions plugin;

	public LocationListener(final LocationBasedActions plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerMove(final PlayerMoveEvent event) {
		if (processRank(event))
			return;

		removeRecent(""); // ensure the lists are always given a chance to flush

		if (processItem(event))
			return;
	}

	private Boolean processItem(final PlayerMoveEvent event) {
		Boolean ret = false;
		final Player pl = event.getPlayer();
		final PermissionUser pex_pl = plugin.pex.getUser(pl);
		final String[] pgnames = pex_pl.getGroupsNames();

		for (final String pg : pgnames) {
			if (plugin.item_locs.containsKey(pg)) {
				// We have location(s) defined for this group
				final Location to = event.getTo();

				for (final LocationR locr : plugin.item_locs.get(pg)) {
					if (locr.Equals(to)) {
						ret = true;
						// Moving into a location we've specified.
						if (!locr.recent_items.contains(pl.getName())) {

							final String itemGiveconf = plugin.getConfig()
									.getString("strings.itemgive",
											"You have been given %item, nice!");
							final ItemStack newitem = new ItemStack(locr.item,
									locr.itemamt);
							pl.sendMessage(ChatColor.GREEN
									+ itemGiveconf.replace("%item",
											newitem.toString()));
							plugin.log.info(pl.getName() + " was given "
									+ newitem.toString());
							pl.getInventory().addItem(newitem);
							locr.recent_items.add(pl.getName());
						}

					}
				}

			}
		}
		return ret;
	}

	private void removeRecent(final String pname) {
		for (final List<LocationR> n : plugin.item_locs.values()) {
			for (final LocationR m : n) {
				if (m.recent_items.contains(pname)) {
					m.recent_items.remove(pname);
				}
				if (m.recent_items.size() > 10) {
					m.recent_items.remove(0);
				}
			}
		}

	}

	private Boolean processRank(final PlayerMoveEvent event) {
		Boolean ret = false;
		final Player pl = event.getPlayer();
		final PermissionUser pex_pl = plugin.pex.getUser(pl);
		final String[] pgnames = pex_pl.getGroupsNames();

		for (final String pg : pgnames) {
			if (plugin.rank_locs.containsKey(pg)) {
				// We have location(s) defined for this group
				final Location to = event.getTo();

				for (final LocationR locr : plugin.rank_locs.get(pg)) {
					if (locr.Equals(to)) {
						ret = true;
						// Moving into a location we've specified.
						final PermissionGroup group = plugin.pex
								.getGroup(locr.rankto);
						if (group != null) {
							final String rankUpconf = plugin
									.getConfig()
									.getString("strings.rankchange",
											"You have become a %rank! Congratulations!");
							pex_pl.setGroups(new PermissionGroup[] { group });
							pl.sendMessage(ChatColor.GREEN
									+ rankUpconf.replace("%rank", locr.rankto));
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
