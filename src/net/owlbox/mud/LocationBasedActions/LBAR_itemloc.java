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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionGroup;

public class LBAR_itemloc implements CommandExecutor {

	private final LocationBasedActions plugin;

	public LBAR_itemloc(final LocationBasedActions myPlugin) {
		plugin = myPlugin;
	}

	public void setitemloc(final Player pl, final String[] args) {
		if (args.length == 4) {
			final String[] targs = { args[0] };

			final PermissionGroup[] grps = plugin.pex.getGroups();
			// Check for proper group names.
			for (final PermissionGroup grp : grps) {
				for (final String check : targs) {
					if (!grp.getName().equals(check)) {
						plugin.log.fine("Error verifing group: " + check);

					}

				}

			}

			final LocationR locr = new LocationR(args[0],
					Integer.parseInt(args[1]), Integer.parseInt(args[2]),
					pl.getLocation());
			if (plugin.item_locs.containsKey(locr.rankfrom)) {
				plugin.item_locs.get(locr.rankfrom).add(locr);
			} else {
				final List<LocationR> t = new ArrayList<LocationR>();
				t.add(locr);
				plugin.item_locs.put(locr.rankfrom, t);
			}
			plugin.loc_names.add(args[3]);
			locr.name = args[3];

			plugin.getConfig().set("location_names", plugin.loc_names);

			plugin.getConfig().set("location." + args[3] + ".type", "item");
			plugin.getConfig().set("location." + args[3] + ".from",
					locr.rankfrom);
			plugin.getConfig().set("location." + args[3] + ".item", locr.item);
			plugin.getConfig().set("location." + args[3] + ".itemamt",
					locr.itemamt);
			plugin.getConfig().set("location." + args[3] + ".x", locr.getX());
			plugin.getConfig().set("location." + args[3] + ".y", locr.getY());
			plugin.getConfig().set("location." + args[3] + ".z", locr.getZ());

			final String newloc = "New location set for AutoItem: X: %x Y: %y Z: %z For Group: %f Named: %name Giving %num of %item";
			String tempstr = newloc.replace("%x", Double.toString(locr.getX()));
			tempstr = tempstr.replace("%y", Integer.toString(locr.getY()));
			tempstr = tempstr.replace("%z", Integer.toString(locr.getZ()));
			tempstr = tempstr.replace("%f", locr.rankfrom);
			tempstr = tempstr.replace("%name", args[3]);
			tempstr = tempstr.replace("%num", Integer.toString(locr.itemamt));
			tempstr = tempstr.replace("%item", Integer.toString(locr.item));
			plugin.log.info(tempstr);

			plugin.saveConfig();
			pl.sendMessage(ChatColor.GREEN + "Location " + args[3] + " saved!");

			return;
		} else {
			pl.sendMessage("Invalid number of arguments for command. Should be /setitemloc group item item_amount LocationName");
		}

	}

	public void remitemloc(final Player pl, final String[] args) {

		if (args.length != 1) {
			pl.sendMessage("Invalid number of arguments for command. Should be /remitemloc LocationName");
			return;
		}

		plugin.loc_names.remove(args[0]);
		plugin.getConfig().set("location_names", plugin.loc_names);

		final Configuration conf = plugin.getConfig();
		if (conf.contains("location." + args[0])) {
			conf.set("location." + args[0], null);
			plugin.saveConfig();
			pl.sendMessage(ChatColor.GREEN + "Location Removed.");
			plugin.log.info("Location " + args[0] + " removed from Autoitem");
			plugin.reloadConfig();
			plugin.reloadLocations();

			return;
		} else {
			plugin.log.log(Level.FINE, pl.getName()
					+ " attempted to delete location: " + args[0]
					+ " but that location does not exist.");
			pl.sendMessage(ChatColor.RED
					+ "The specified location does not exist.");
			return;
		}
	}

	public void listitemloc(final Player pl) {

		final StringBuilder tempstr = new StringBuilder("Autoitem Locations:");
		for (final List<LocationR> n : plugin.item_locs.values()) {
			for (final LocationR x : n) {
				tempstr.append(" " + x.name);
			}
		}
		pl.sendMessage(tempstr.toString());
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command,
			final String label, final String[] args) {

		final Player pl = (Player) sender;

		if (command.getName().equalsIgnoreCase("setitemloc")) {
			setitemloc(pl, args);
			return true;
		} else {
			if (command.getName().equalsIgnoreCase("remitemloc")) {
				remitemloc(pl, args);
				return true;
			} else {
				listitemloc(pl);
				return true;
			}
		}
	}
}
