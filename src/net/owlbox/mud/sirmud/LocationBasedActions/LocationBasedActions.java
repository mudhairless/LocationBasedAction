package net.owlbox.mud.LocationBasedActions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class LocationBasedActions extends JavaPlugin {

	public Logger log;
	private LBAR_rankloc AutoRankexecutor;
	private LBAR_itemloc AutoItemexecutor;
	public PermissionManager pex;
	public HashMap<String, List<LocationR>> rank_locs;
	public HashMap<String, List<LocationR>> item_locs;
	public List<String> loc_names;

	@Override
	public void onEnable() {
		log = this.getLogger();

		loc_names = new ArrayList<String>();
		rank_locs = new HashMap<String, List<LocationR>>();
		item_locs = new HashMap<String, List<LocationR>>();

		if (!getConfig().contains("config")) {
			getConfig().options().copyDefaults(true);
			this.saveDefaultConfig();
		}

		pex = PermissionsEx.getPermissionManager();
		AutoRankexecutor = new LBAR_rankloc(this);
		getCommand("setrankloc").setExecutor(AutoRankexecutor);
		getCommand("remrankloc").setExecutor(AutoRankexecutor);
		getCommand("listrankloc").setExecutor(AutoRankexecutor);

		AutoItemexecutor = new LBAR_itemloc(this);
		getCommand("setitemloc").setExecutor(AutoItemexecutor);
		getCommand("remitemloc").setExecutor(AutoItemexecutor);
		getCommand("listitemloc").setExecutor(AutoItemexecutor);

		LocationListener LocListen = new LocationListener(this);
		getServer().getPluginManager().registerEvents(LocListen, this);

		reloadLocations();

	}

	@Override
	public void onDisable() {
		this.saveConfig();
	}

	public void reloadLocations() {

		loc_names.clear();
		rank_locs.clear();
		item_locs.clear();

		loc_names = getConfig().getStringList("location_names");
		if( loc_names == null )
			return; //No locations to load.

		for (String name : loc_names) {

			log.fine("Loading location: " + name);
			String loc_type = getConfig().getString(
					"location." + name + ".type");
			if (loc_type.equalsIgnoreCase("rank")) { // rank loader
				LocationR tloc = new LocationR(getConfig().getString(
						"location." + name + ".from"), getConfig().getString(
						"location." + name + ".to"), new Location(
						Bukkit.getWorld("world"), 0, 0, 0));
				tloc.loc.setX(getConfig().getInt("location." + name + ".x"));
				tloc.loc.setY(getConfig().getInt("location." + name + ".y"));
				tloc.loc.setZ(getConfig().getInt("location." + name + ".z"));
				tloc.name = name;

				if (rank_locs.containsKey(tloc.rankfrom)) {
					rank_locs.get(tloc.rankfrom).add(tloc);
				} else {
					List<LocationR> t = new ArrayList<LocationR>();
					t.add(tloc);
					rank_locs.put(tloc.rankfrom, t);
				}
			} else {
				if (loc_type.equalsIgnoreCase("item")) {
					// item loader
					LocationR tloc = new LocationR(getConfig().getString(
							"location." + name + ".from"), getConfig().getInt(
							"location." + name + ".item"), getConfig().getInt(
							"location." + name + ".itemamt"), new Location(
							Bukkit.getWorld("world"), 0, 0, 0));
					tloc.loc.setX(getConfig().getInt("location." + name + ".x"));
					tloc.loc.setY(getConfig().getInt("location." + name + ".y"));
					tloc.loc.setZ(getConfig().getInt("location." + name + ".z"));
					tloc.name = name;

					if (item_locs.containsKey(tloc.rankfrom)) {
						item_locs.get(tloc.rankfrom).add(tloc);
					} else {
						List<LocationR> t = new ArrayList<LocationR>();
						t.add(tloc);
						item_locs.put(tloc.rankfrom, t);
					}
				} else {
					log.info("Unknown type of location in " + name);
				}
			}
		}

		log.info("Loaded " + Integer.toString(loc_names.size()) + " locations.");
	}

}
