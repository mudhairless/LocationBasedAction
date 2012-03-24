package net.owlbox.sirmud.LocationBasedAutoRank;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

public class LocationR implements ConfigurationSerializable {

	
	
	public String rankfrom;
	public String rankto;
	public Location loc;
	
	public LocationR( String from, String to, Location l ) {
		this.rankfrom = from;
		this.rankto = to;
		TinyLocation temp = new TinyLocation(l);
		this.loc = temp.toLocation();
	}
	
	static {
		ConfigurationSerialization.registerClass(LocationR.class);
	}
	
	@SuppressWarnings("unchecked")
	public LocationR( Map<String,Object> in ) {
		HashMap<String, Object> x = (HashMap<String,Object>)in;
		rankfrom = (String) x.get("from");
		rankto = (String) x.get("to");
		loc = (TinyLocation.deserialize((Map<String,Object>)x.get("loc"))).toLocation();
		
	}

	@Override
	public Map<String, Object> serialize() {
		
		HashMap<String, Object> outmap = new HashMap<String, Object>();
		
		outmap.put("from", rankfrom);
		outmap.put("to", rankto);
		outmap.put("loc", (new TinyLocation(loc)).serialize());

				
		return outmap;
	}
	
}
