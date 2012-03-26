package net.owlbox.sirmud.LocationBasedActions;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;

public class LocationListener implements Listener {

	private LocationBasedActions plugin;

	public LocationListener(LocationBasedActions plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player pl = event.getPlayer();
		PermissionUser pex_pl = plugin.pex.getUser(pl);
		String[] pgnames = pex_pl.getGroupsNames();

		for (String pg : pgnames) {
			if (plugin.locs.containsKey(pg)) {
				// We have location(s) defined for this group
				Location to = new TinyLocation(event.getTo()).toLocation();

				for (LocationR locr : plugin.locs.get(pg)) {
					if (locr.loc.equals(to)) {
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
	}
}
