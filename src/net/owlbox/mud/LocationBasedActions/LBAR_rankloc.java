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

public class LBAR_rankloc implements CommandExecutor {

	private final LocationBasedActions plugin;

	public LBAR_rankloc(final LocationBasedActions myPlugin) {
		plugin = myPlugin;
	}

	public void setrankloc(final Player pl, final String[] args) {
		if (args.length == 3) {
			final String[] targs = { args[0], args[1] };

			final PermissionGroup[] grps = plugin.pex.getGroups();
			// Check for proper group names.
			for (final PermissionGroup grp : grps) {
				for (final String check : targs) {
					if (!grp.getName().equals(check)) {
						plugin.log.fine("Error verifing group: " + check);
					}

				}

			}

			final LocationR locr = new LocationR(args[0], args[1],
					pl.getLocation());
			if (plugin.rank_locs.containsKey(locr.rankfrom)) {
				plugin.rank_locs.get(locr.rankfrom).add(locr);
			} else {
				final List<LocationR> t = new ArrayList<LocationR>();
				t.add(locr);
				plugin.rank_locs.put(locr.rankfrom, t);
			}
			plugin.loc_names.add(args[2]);
			locr.name = args[2];
			plugin.getConfig().set("location_names", plugin.loc_names);

			plugin.getConfig().set("location." + args[2] + ".type", "rank");
			plugin.getConfig().set("location." + args[2] + ".from",
					locr.rankfrom);
			plugin.getConfig().set("location." + args[2] + ".to", locr.rankto);
			plugin.getConfig().set("location." + args[2] + ".x", locr.getX());
			plugin.getConfig().set("location." + args[2] + ".y", locr.getY());
			plugin.getConfig().set("location." + args[2] + ".z", locr.getZ());

			final String newloc = "New location set for AutoRank: X: %x Y: %y Z: %z From: %f To: %t Named: %n";
			String tempstr = newloc
					.replace("%x", Integer.toString(locr.getX()));
			tempstr = tempstr.replace("%y", Integer.toString(locr.getY()));
			tempstr = tempstr.replace("%z", Integer.toString(locr.getZ()));
			tempstr = tempstr.replace("%f", locr.rankfrom);
			tempstr = tempstr.replace("%t", locr.rankto);
			tempstr = tempstr.replace("%n", args[2]);
			plugin.log.info(tempstr);

			plugin.saveConfig();
			pl.sendMessage(ChatColor.GREEN + "Location " + args[2] + " saved!");

			return;
		} else {
			pl.sendMessage("Invalid number of arguments for command. Should be /setrankloc RankFrom Rankto LocationName");
		}

	}

	public void remrankloc(final Player pl, final String[] args) {

		if (args.length != 1) {
			pl.sendMessage("Invalid number of arguments for command. Should be /remrankloc LocationName");
			return;
		}

		plugin.loc_names.remove(args[0]);
		plugin.getConfig().set("location_names", plugin.loc_names);

		final Configuration conf = plugin.getConfig();
		if (conf.contains("location." + args[0])) {
			conf.set("location." + args[0], null);
			plugin.saveConfig();
			pl.sendMessage(ChatColor.GREEN + "Location Removed.");
			plugin.log.info("Location " + args[0] + " removed from AutoRank");

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

	public void listrankloc(final Player pl) {

		String tempstr = "AutoRank Locations:";
		for (final List<LocationR> n : plugin.rank_locs.values()) {
			for (final LocationR x : n) {
				tempstr = tempstr + " " + x.name;
			}
		}
		pl.sendMessage(tempstr);
	}

	@Override
	public boolean onCommand(final CommandSender sender, final Command command,
			final String label, final String[] args) {

		final Player pl = (Player) sender;

		if (command.getName().equalsIgnoreCase("setrankloc")) {
			setrankloc(pl, args);
			return true;
		} else {
			if (command.getName().equalsIgnoreCase("remrankloc")) {
				remrankloc(pl, args);
				return true;
			} else {
				listrankloc(pl);
				return true;
			}
		}
	}
}
