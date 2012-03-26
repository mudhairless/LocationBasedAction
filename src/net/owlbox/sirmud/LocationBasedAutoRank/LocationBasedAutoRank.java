package net.owlbox.sirmud.LocationBasedAutoRank;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class LocationBasedAutoRank extends JavaPlugin {

	public Logger log;
	private LBAR_rankloc SRLexecutor;
	public PermissionManager pex;
	public HashMap<String,LocationR[]> locs;
	public List<String> loc_names;
	
	
	
	@Override
	public void onEnable() {
		log = this.getLogger();
		locs = new HashMap<String,LocationR[]>();
		loc_names = getConfig().getStringList("locations");
		
		pex = PermissionsEx.getPermissionManager();
		SRLexecutor = new LBAR_rankloc(this);
		LocationListener LocListen = new LocationListener(this);
		getCommand("setrankloc").setExecutor(SRLexecutor);
		getCommand("remrankloc").setExecutor(SRLexecutor);
		getCommand("listrankloc").setExecutor(SRLexecutor);
		log.log(Level.FINE, "LocationBasedAutoRank Command Executor registered.");
		
		getServer().getPluginManager().registerEvents(LocListen, this);
		log.log(Level.FINE, "LocationBasedAutoRank Listener event registered.");
		
		for(String name : loc_names) {
			
			log.fine("Loading location: " + name);
			LocationR tloc = new LocationR(getConfig().getString(name+".from"), getConfig().getString(name+".to"), new Location(Bukkit.getWorld("world"),0,0,0));
			tloc.loc.setX(getConfig().getInt(name+".x"));
			tloc.loc.setY(getConfig().getInt(name+".y"));
			tloc.loc.setZ(getConfig().getInt(name+".z"));
			
			if(locs.containsKey(tloc.rankfrom)) {
				LocationR[] t = locs.get(tloc.rankfrom);
				t[t.length] = tloc;
			} else {
				LocationR[] t = { tloc };
				locs.put(tloc.rankfrom, t);
			}
		}
		
		log.info("LocationBasedAutoRank Enabled. Loaded " + Integer.toString(loc_names.size()) + " locations.");
	}
	
	@Override
	public void onDisable() {
		this.saveConfig();
		log.info("LocationBasedAutoRank Disabled.");
	}
	

}
