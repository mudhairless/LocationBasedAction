package net.owlbox.sirmud.LocationBasedActions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

public class LocationR implements ConfigurationSerializable {

	public String type;
	public String rankfrom;
	public String rankto;
	public String name;
	public int item;
	public int itemamt;
	public Location loc;
	public List<String> recent_items;

	public LocationR(String from, String to, Location l) { // Rank Constructor
		this.type = "rank";
		this.rankfrom = from;
		this.rankto = to;
		TinyLocation temp = new TinyLocation(l);
		this.loc = temp.toLocation();
	}

	public LocationR(String from, int item, int itemamt, Location l) { // Item
																		// Constructor
		this.type = "item";
		this.rankfrom = from;
		this.item = item;
		this.itemamt = itemamt;
		TinyLocation temp = new TinyLocation(l);
		this.loc = temp.toLocation();
		this.recent_items = new ArrayList<String>();
	}

	static {
		ConfigurationSerialization.registerClass(LocationR.class);
	}

	public LocationR(Map<String, Object> in) {
		HashMap<String, Object> x = (HashMap<String, Object>) in;
		rankfrom = (String) x.get("from");
		rankto = (String) x.get("to");
		Map<String, Object> map = (Map<String, Object>) x.get("loc");
		loc = (TinyLocation.deserialize(map)).toLocation();

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
