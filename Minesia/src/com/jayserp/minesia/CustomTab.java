package com.jayserp.minesia;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.mcsg.double0negative.tabapi.TabAPI;

public class CustomTab {
	
	private Minesia plugin;
	
	public CustomTab(Minesia plugin) {
		this.plugin = plugin;
	}

	public void updateTab(){ //update the tab for a player
		
		for(Player p : plugin.getServer().getOnlinePlayers()) {
					
			TabAPI.setPriority(plugin, p, 0);
			
			TabAPI.setTabString(plugin, p, 0, 0, ChatColor.RED + "GHOST");
			TabAPI.setTabString(plugin, p, 0, 1, ChatColor.BLUE + "HUMAN"); 
			TabAPI.setTabString(plugin, p, 0, 2, ChatColor.GRAY + "SPEC"); 
			
			int specList = 1;			
			int redList = 1;
			int blueList = 1;
					
			if (plugin.getUserList() != null) {
	    		for (int i = 0; i < plugin.getUserList().size(); i++) {
	    			
	    			PlayerDataClass temp = plugin.getUserList().get(i);
	    			
	    			if (temp.getTeam() == "ghost") {
	    				TabAPI.setTabString(plugin, p, redList, 0, temp.getName());
	    				redList++;
	    			}

	    			if (temp.getTeam() == "player") {
	    				TabAPI.setTabString(plugin, p, blueList, 1, temp.getName());
	    				blueList++;
	    			}
	    			
	    			if (temp.getTeam() == "spec") {
	    				TabAPI.setTabString(plugin, p, specList, 2, temp.getName());
	    				specList++;
	    			}
	    		}
			} 
			
			TabAPI.updatePlayer(p);
		}
	}	
}