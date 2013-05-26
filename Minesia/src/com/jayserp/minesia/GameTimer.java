package com.jayserp.minesia;

import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

public class GameTimer {

	private Minesia plugin;
	public int id;
	
	public int time;
	
	public GameTimer(final Minesia plugin) {
		this.plugin = plugin;
		
		id = plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {
			public void run() {
				time++;
			
				//Poll gameManager for game management
				//return true if everything is fine
				//pass time to allow capture tracking
				plugin.getGameManager().checkStatus(time);

			}
		}, 20L, 20L);
	}
	
	public int getGameTimerId() {
		return id;
	}
}