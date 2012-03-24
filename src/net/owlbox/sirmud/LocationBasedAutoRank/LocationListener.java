package net.owlbox.sirmud.LocationBasedAutoRank;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;

public class LocationListener implements Listener {

	private LocationBasedAutoRank plugin;
	
	public LocationListener( LocationBasedAutoRank plugin ) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerMove( PlayerMoveEvent event ) {
		Player pl = event.getPlayer();
		PermissionUser pex_pl = plugin.pex.getUser(pl);
		PermissionGroup[] pex_grps = pex_pl.getGroups();
		
		for(PermissionGroup pg : pex_grps) {
			if (plugin.locs.containsKey(pg.getPrefix())) {
				//We have location(s) defined for this group
				Location to = event.getTo();
				for( LocationR locr : plugin.locs.get(pg.getPrefix()) ) {
					if ( locr.loc.equals(to)) {
						//Moving into a location we've specified.
						PermissionGroup group = plugin.pex.getGroup(locr.rankto);
						if (group != null) {
							pex_pl.setGroups(new PermissionGroup[]{group});
							String rankUp = "You have become a " + locr.rankto;
							pl.sendMessage(ChatColor.GREEN + rankUp);
							plugin.log.info(pl.getName() + " became a " + locr.rankto );
						} else {
							pl.sendMessage(ChatColor.RED + "Oops, something went wrong, please contact staff.");
							plugin.log.severe( "ERROR: " + pl.getName() + " was attempting to rank to " + locr.rankto + " but the group didn't exist!");
						}
					}
				}
			}
		}
	}
}
