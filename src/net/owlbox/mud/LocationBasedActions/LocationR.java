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

	public LocationR(final String from, final String to, final Location l) { // Rank
																				// Constructor
		type = "rank";
		rankfrom = from;
		rankto = to;
		x = (int) l.getX();
		y = (int) l.getY();
		z = (int) l.getZ();

	}

	public LocationR(final String from, final int item, final int itemamt,
			final Location l) { // Item
		// Constructor
		type = "item";
		rankfrom = from;
		this.item = item;
		this.itemamt = itemamt;
		recent_items = new ArrayList<String>();
		x = (int) l.getX();
		y = (int) l.getY();
		z = (int) l.getZ();
	}

	public Boolean Equals(final Location rhs) {

		if (x == (int) rhs.getX() && y == (int) rhs.getY()
				&& z == (int) rhs.getZ())
			return true;

		return false;
	}

	public void setX(final int rhsx) {
		x = rhsx;
	}

	public void setY(final int rhsy) {
		y = rhsy;
	}

	public void setZ(final int rhsz) {
		z = rhsz;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}
}
