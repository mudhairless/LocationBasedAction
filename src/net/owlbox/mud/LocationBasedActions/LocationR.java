package net.owlbox.mud.LocationBasedActions;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

public class LocationR {

	public String type;
	public String rankfrom;
	public String rankto;
	public String name;
	public int item;
	public int itemamt;
	public List<String> recent_items;
	private int x;
	private int y;
	private int z;

	public LocationR(String from, String to, Location l) { // Rank Constructor
		this.type = "rank";
		this.rankfrom = from;
		this.rankto = to;
		this.x = (int) l.getX();
		this.y = (int) l.getY();
		this.z = (int) l.getZ();

	}

	public LocationR(String from, int item, int itemamt, Location l) { // Item
																		// Constructor
		this.type = "item";
		this.rankfrom = from;
		this.item = item;
		this.itemamt = itemamt;
		this.recent_items = new ArrayList<String>();
		this.x = (int) l.getX();
		this.y = (int) l.getY();
		this.z = (int) l.getZ();
	}

	public Boolean Equals(Location rhs) {

		if (x == (int) rhs.getX() && y == (int) rhs.getY()
				&& z == (int) rhs.getZ())
			return true;

		return false;
	}

}
