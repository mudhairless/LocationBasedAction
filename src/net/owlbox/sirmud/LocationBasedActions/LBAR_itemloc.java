package net.owlbox.sirmud.LocationBasedActions;

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

	private LocationBasedActions plugin;

	public LBAR_itemloc(LocationBasedActions myPlugin) {
		this.plugin = myPlugin;
	}

	public void setitemloc(Player pl, String[] args) {
		if (args.length == 4) {
			String[] targs = { args[0] };

			PermissionGroup[] grps = plugin.pex.getGroups();
			// Check for proper group names.
			for (PermissionGroup grp : grps) {
				for (String check : targs) {
					if (!grp.getName().equals(check)) {
						plugin.log.fine("Error verifing group: " + check);

					}

				}

			}

			LocationR locr = new LocationR(args[0], Integer.parseInt(args[1]),
					Integer.parseInt(args[2]), pl.getLocation());
			if (plugin.item_locs.containsKey(locr.rankfrom)) {
				plugin.item_locs.get(locr.rankfrom).add(locr);
			} else {
				List<LocationR> t = new ArrayList<LocationR>();
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
			plugin.getConfig().set("location." + args[3] + ".x",
					(int) locr.loc.getX());
			plugin.getConfig().set("location." + args[3] + ".y",
					(int) locr.loc.getY());
			plugin.getConfig().set("location." + args[3] + ".z",
					(int) locr.loc.getZ());

			String newloc = "New location set for AutoItem: X: %x Y: %y Z: %z For Group: %f Named: %name Giving %num of %item";
			String tempstr = newloc.replace("%x",
					Double.toString(locr.loc.getX()));
			tempstr = tempstr.replace("%y", Double.toString(locr.loc.getY()));
			tempstr = tempstr.replace("%z", Double.toString(locr.loc.getZ()));
			tempstr = tempstr.replace("%f", locr.rankfrom);
			tempstr = tempstr.replace("%name", args[3]);
			tempstr = tempstr.replace("%num", Integer.toString(locr.itemamt));
			tempstr = tempstr.replace("%item", Integer.toString(locr.item));
			plugin.log.info(tempstr);

			plugin.saveConfig();
			pl.sendMessage(ChatColor.GREEN + "Location " + args[3] + " saved!");

			return;
		} else
			pl.sendMessage("Invalid number of arguments for command. Should be /setitemloc group item item_amount LocationName");

	}

	public void remitemloc(Player pl, String[] args) {

		if (args.length != 1) {
			pl.sendMessage("Invalid number of arguments for command. Should be /remitemloc LocationName");
			return;
		}

		plugin.loc_names.remove(args[0]);
		plugin.getConfig().set("location_names", plugin.loc_names);

		Configuration conf = plugin.getConfig();
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

	public void listitemloc(Player pl) {

		String tempstr = "Autoitem Locations:";
		for (List<LocationR> n : plugin.item_locs.values()) {
			for (LocationR x : n) {
				tempstr = tempstr + " " + x.name;
			}
		}
		pl.sendMessage(tempstr);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {

		Player pl = (Player) sender;

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
