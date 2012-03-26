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

public class LBAR_rankloc implements CommandExecutor {

	private LocationBasedActions plugin;

	public LBAR_rankloc(LocationBasedActions myPlugin) {
		this.plugin = myPlugin;
	}

	public void setrankloc(Player pl, String[] args) {
		if (args.length == 3) {
			String[] targs = { args[0], args[1] };

			PermissionGroup[] grps = plugin.pex.getGroups();
			// Check for proper group names.
			for (PermissionGroup grp : grps) {
				for (String check : targs) {
					if (!grp.getName().equals(check)) {
						plugin.log.fine("Error verifing group: " + check);
					}

				}

			}

			LocationR locr = new LocationR(args[0], args[1], pl.getLocation());
			if (plugin.rank_locs.containsKey(locr.rankfrom)) {
				plugin.rank_locs.get(locr.rankfrom).add(locr);
			} else {
				List<LocationR> t = new ArrayList<LocationR>();
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
			plugin.getConfig().set("location." + args[2] + ".x",
					(int) locr.loc.getX());
			plugin.getConfig().set("location." + args[2] + ".y",
					(int) locr.loc.getY());
			plugin.getConfig().set("location." + args[2] + ".z",
					(int) locr.loc.getZ());

			String newloc = "New location set for AutoRank: X: %x Y: %y Z: %z From: %f To: %t Named: %n";
			String tempstr = newloc.replace("%x",
					Double.toString(locr.loc.getX()));
			tempstr = tempstr.replace("%y", Double.toString(locr.loc.getY()));
			tempstr = tempstr.replace("%z", Double.toString(locr.loc.getZ()));
			tempstr = tempstr.replace("%f", locr.rankfrom);
			tempstr = tempstr.replace("%t", locr.rankto);
			tempstr = tempstr.replace("%n", args[2]);
			plugin.log.info(tempstr);

			plugin.saveConfig();
			pl.sendMessage(ChatColor.GREEN + "Location " + args[2] + " saved!");

			return;
		} else
			pl.sendMessage("Invalid number of arguments for command. Should be /setrankloc RankFrom Rankto LocationName");

	}

	public void remrankloc(Player pl, String[] args) {

		if (args.length != 1) {
			pl.sendMessage("Invalid number of arguments for command. Should be /remrankloc LocationName");
			return;
		}

		plugin.loc_names.remove(args[0]);
		plugin.getConfig().set("location_names", plugin.loc_names);

		Configuration conf = plugin.getConfig();
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

	public void listrankloc(Player pl) {

		String tempstr = "AutoRank Locations:";
		for (List<LocationR> n : plugin.rank_locs.values()) {
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