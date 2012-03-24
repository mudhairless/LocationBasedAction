package net.owlbox.sirmud.LocationBasedAutoRank;

import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import ru.tehkode.permissions.PermissionGroup;

public class LBAR_rankloc implements CommandExecutor {

	private LocationBasedAutoRank plugin;
	
	public LBAR_rankloc( LocationBasedAutoRank myPlugin ) {
		this.plugin = myPlugin;
	}
	
	public void setrankloc( Player pl, String[] args) {
		if (args.length == 3) {
			
			PermissionGroup[] grps = plugin.pex.getGroups();
			//Check for proper group names.
			for( PermissionGroup grp : grps ) {
				for( String check : args ) {
					if (!grp.getPrefix().equals(check)) {
						plugin.log.log(Level.FINE, pl.getName() + " attempted to create a LocationBasedAutoRank for nonexistant group: " + check);
						pl.sendMessage(ChatColor.RED + "The specified group: " + check + ", does not exist.");
					}

				}
			}

			LocationR locr = new LocationR(args[0],args[1],pl.getLocation());
			if(plugin.locs.containsKey(locr.rankfrom)) {
				LocationR[] loc = plugin.locs.get(locr.rankfrom);
				loc[loc.length] = locr;
					
				
			} else {
				LocationR[] tvalue = { locr };
				
				plugin.locs.put(locr.rankfrom, tvalue);
			}
			plugin.loc_names.add(args[2]);
			plugin.getConfig().set("locations", plugin.loc_names);
			plugin.getConfig().set( args[2], locr);
			
			String newloc = "New location set for AutoRank: X: %x Y: %y Z: %z From: %f To: %t Named: %n";
			String tempstr = newloc.replace("%x", Double.toString(locr.loc.getX()));
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

	public void remrankloc( Player pl, String[] args ) {
		
		if (args.length != 1) {
			pl.sendMessage("Invalid number of arguments for command. Should be /remrankloc LocationName");
			return;
		}

		Configuration conf = plugin.getConfig();
		if (conf.contains(args[0])) {
			conf.set( args[0], null);
			plugin.saveConfig();
			pl.sendMessage(ChatColor.GREEN + "Location Removed.");
			plugin.log.info("Location " + args[0] + " removed from AutoRank");
			return;
		} else {
			plugin.log.log(Level.FINE, pl.getName() + " attempted to delete location: " + args[0] + " but that location does not exist.");
			pl.sendMessage(ChatColor.RED + "The specified location does not exist.");
			return;
		}
	}
	
	public void listrankloc( Player pl ){

		String tempstr = "AutoRank Locations: ";
		for( String n : plugin.loc_names ) {
			tempstr = tempstr + " " + n;
		}
		pl.sendMessage(tempstr);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		
		Player pl = (Player)sender;
		
		if(command.getName().equalsIgnoreCase("setrankloc")) {
			setrankloc(pl,args);
			return true;
		} else {
			if(command.getName().equalsIgnoreCase("remrankloc")) {
				remrankloc(pl,args);
			} else {
				listrankloc(pl);
				return true;
			}
		}

		return false;
	}
}
