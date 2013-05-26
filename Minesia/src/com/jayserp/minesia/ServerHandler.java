package com.jayserp.minesia;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerHandler implements Listener {
	
	private Minesia plugin;
	
	public ServerHandler(Minesia plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private void displayPlayers(ServerListPingEvent evt) {
		evt.setMaxPlayers(Constants.MAX_USERS);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private void checkUser(AsyncPlayerPreLoginEvent evt) {
		if (plugin.getServer().getOnlinePlayers().length >= Constants.MAX_USERS) {
			plugin.getLogger().info("people on server >= " + Constants.MAX_USERS + 
					" retrieving user: " + evt.getName());
			if (evt.getName() != "jayserp" || evt.getName() != "Einshine") {
				evt.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, "Server Full.");
				return;
			}
			/*UsersDataClass user = plugin.getSqlDb().getUser(evt.getName());
			if (user != null) {
				plugin.getLogger().info("rank: " + user.getRank());
				if (user.getRank() > 0) {
					plugin.getLogger().info("allowing: " + evt.getName() + " " + user.getRank());
					return;
				} else {
					plugin.getLogger().info("disallowing: " + evt.getName() + " " + user.getRank());
					evt.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, "Server Full. To ensure a guaranteed slot, " +
							"donate at www.minekoth.com");
					return;
				}
			} else {
				plugin.getLogger().info("disallowing: " + evt.getName());
				evt.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, "Server Full. To ensure a guaranteed slot, " +
						"donate at www.minekoth.com");
			}*/
		}
		
		/*UsersDataClass userData = plugin.getSqlDb().getUser(evt.getName());
		
		if (userData != null) {
			if (userData.getRank() < 10) {
				evt.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, 
				"This server is only open to beta testers. " +
						"To request beta access, contact jay@jayserp.com");
			}
		} else {
			evt.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, 
			"This server is only open to beta testers. " +
					"To request beta access, contact jay@jayserp.com");
		}*/
	}
	
	public Minesia getPlugin() {
		return plugin;
	}
}