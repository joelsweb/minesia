package com.jayserp.minesia;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.kitteh.tag.TagAPI;

public class PlayerListeners implements Listener {
	
	private Minesia plugin;
	
	public PlayerListeners(Minesia plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private void preventArmor(InventoryClickEvent evt) {
		//plugin.getLogger().info(evt.getView().getType().getDefaultTitle());
		if (evt.getView().getType() == InventoryType.CRAFTING) {
			//plugin.getLogger().info("inventory click " + evt.getSlot());
			if (evt.getSlot() == 39 || evt.getSlot() == 38 ||
				evt.getSlot() == 37 || evt.getSlot() == 36) {
				evt.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private void preventHunger(FoodLevelChangeEvent evt) {
		evt.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private void preventHealthRegen(EntityRegainHealthEvent evt) {
		if (evt.getRegainReason() == RegainReason.EATING) {
			return;
		} else {
			evt.setCancelled(true);
		}
	}	

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerJoin(PlayerJoinEvent evt) {
		
		final Player p = evt.getPlayer();
		/*
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				
				p.sendMessage("Welcome to MINESIA. Game by EINSHINE, " + 
						" plugin by " + ChatColor.GRAY  + "JAYSERP.");
				
				UsersDataClass userData = plugin.getSqlDb().getUser(p.getDisplayName());
				if (userData != null) {
					plugin.getPlayerHandler().addSpecPlayer(p.getDisplayName(), userData.getRank());
					if (userData.getRank() > 90) {
						p.sendMessage("Welcome, admin.");
					}
				} else {
					plugin.getPlayerHandler().addSpecPlayer(p.getDisplayName());
				}
								
				plugin.getCustomTab().updateTab();
			}
		}, 3);*/
		
		p.sendMessage("Welcome to MINESIA. Game by " + ChatColor.RED + "Einshine" + ChatColor.WHITE + ", " + 
				" plugin by " + ChatColor.GRAY  + "jayserp");
		
		plugin.getPlayerHandler().addSpecPlayer(p.getDisplayName());
		plugin.getCustomTab().updateTab();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerQuit(PlayerQuitEvent evt) {			
		if (plugin.getPlayerHandler().findPlayer(evt.getPlayer().getDisplayName()) != null) {
			plugin.getPlayerHandler().removePlayer(evt.getPlayer().getDisplayName());
			plugin.getLogger().info("removed " + evt.getPlayer().getDisplayName() + " from the game.");
			if (plugin.getGameManager().getGhost() == evt.getPlayer().getDisplayName()) {
				plugin.getGameManager().setGhostLeft(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerKick(PlayerKickEvent evt) {
		if (plugin.getPlayerHandler().findPlayer(evt.getPlayer().getDisplayName()) != null) {
			plugin.getPlayerHandler().removePlayer(evt.getPlayer().getDisplayName());
			plugin.getLogger().info("removed " + evt.getPlayer().getDisplayName() + " from the game.");
			if (plugin.getGameManager().getGhost() == evt.getPlayer().getDisplayName()) {
				plugin.getGameManager().setGhostLeft(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private void onInteract(PlayerInteractEvent event) {
	    final Player player = event.getPlayer();
	    PlayerDataClass playerData = plugin.getPlayerHandler().findPlayer(player.getDisplayName());
	    
	    if (playerData.getType() != null) {		
	    	if (playerHasEmerald(player) == true) {
			    if (player.getItemInHand().getType() == Material.BOOK && 
			    		(event.getAction() == Action.RIGHT_CLICK_AIR || 
			    		event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
			    		playerData.getType().equalsIgnoreCase("priest")) {
			    	decrementEmerald(player);
			    	plugin.getServer().broadcastMessage("Priest " + player.getDisplayName() + " has slowed the ghost!");
			    	plugin.getServer().getPlayer(plugin.getGameManager().getGhost()).addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 250, 4));
			    }
			    
			    if (player.getItemInHand().getType() == Material.IRON_SWORD && 
			    		(event.getAction() == Action.RIGHT_CLICK_AIR || 
			    		event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
			    		playerData.getType().equalsIgnoreCase("ninja")) {
			    	decrementEmerald(player);
			    	plugin.getServer().broadcastMessage("Ninja " + player.getDisplayName() + " has temporarily vanished!");
					player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 250, 1));
			    }
			    
			    if (player.getItemInHand().getType() == Material.PAPER &&
			    		playerData.getType().equalsIgnoreCase("medic")) {		
			    	if (event.getAction() == Action.RIGHT_CLICK_AIR ||
			    		event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			    		decrementEmerald(player);
			    		player.setHealth(20);
			    		plugin.getServer().broadcastMessage("Medic " + player.getDisplayName() + " has fully healed!");
			    	}
			    }
	    	}
	    } else {
	    	return;
	    }
	}
	
	public boolean playerHasEmerald(Player player) {
		if (player.getInventory().contains(Material.EMERALD)) {
			return true;
		} else {
			return false;
		}
	}
	
	public void decrementEmerald(Player player) {
		if (playerHasEmerald(player)) {
			player.getInventory().removeItem(new ItemStack (Material.EMERALD, 1));
			player.updateInventory();
		}
	}
	
/*	@EventHandler(priority = EventPriority.HIGHEST)
	private void onNameTag(PlayerReceiveNameTagEvent evt) {
		String target = evt.getNamedPlayer().getName();	
		String user = evt.getPlayer().getDisplayName();
		PlayerDataClass targetData = plugin.getPlayerHandler().findPlayer(target);
		PlayerDataClass userData = plugin.getPlayerHandler().findPlayer(user);
		
		if (targetData != null && userData != null) {
			if (targetData.getTeam() == userData.getTeam()) {
				if (userData.getTeam() == "red") {
					evt.setTag(ChatColor.RED + target);
				}
				if (userData.getTeam() == "blue") {
					evt.setTag(ChatColor.BLUE + target);
				}			
			}	
		} else {
			evt.setTag(target);
		}
	}*/
	
	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerDamage(EntityDamageByEntityEvent evt) {
		if (evt.getEntity() instanceof Player && evt.getDamager() instanceof Player) {
			
			Player player = (Player) evt.getEntity();
			PlayerDataClass playerData = plugin.getPlayerHandler()
											   .findPlayer(player.getDisplayName());
			Player attacker = (Player) evt.getDamager();
			PlayerDataClass attackerData = plugin.getPlayerHandler()
												 .findPlayer(attacker.getDisplayName());			
			
			if (attackerData != null && playerData != null) {
				/*plugin.getLogger().info("AttackerData: " + attackerData.getTeam() + 
						" | " + "playerData: " + playerData.getTeam());*/
				if (attackerData.getTeam() == "ghost" && playerData.getTeam() == "player") {
					evt.setCancelled(false);
				}
				if (attackerData.getTeam() == "player" && playerData.getTeam() == "ghost") {
					evt.setCancelled(true);
				}
				if (attackerData.getTeam() == "spec" && playerData.getTeam() == "spec") {
					evt.setCancelled(true);
				}
				if (attackerData.getTeam() == "player" && playerData.getTeam() == "player") {
					if (attacker.getItemInHand().getType() == Material.PAPER &&
							attackerData.getType().equalsIgnoreCase("medic")) {
						//plugin.getLogger().info("healing player");
						if (player.getHealth() >= 19) {
							player.setHealth(20);						
						} else {
							player.setHealth(player.getHealth() + 1);
						}
						//decrementEmerald(attacker);
					}
					evt.setCancelled(true);
				}
			}
		}
	}
	
/*	@EventHandler(priority = EventPriority.HIGHEST)
	private void onPlayerRespawn(final PlayerRespawnEvent evt) {		
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){			 
            public void run(){   
        		PlayerDataClass playerData = plugin.getPlayerHandler().findPlayer(evt.getPlayer().getDisplayName());
        		Player player = evt.getPlayer();
        		if (playerData != null) {
        			plugin.getLogger().info("Respawning: " + playerData.getName());
        			plugin.getPlayerHandler().spawnPlayer(playerData);
        		}
            }
        }, 3);
	}*/

	@EventHandler(priority = EventPriority.HIGHEST)
	private void onDeath(PlayerDeathEvent evt) {
		evt.getDrops().clear();
		
		final Player player = evt.getEntity().getPlayer();
		final PlayerDataClass playerData = plugin.getPlayerHandler().findPlayer(player.getDisplayName());
		
		if (playerData != null) {	
			
			if (evt.getEntity().getKiller() != null) {	
				Player killer = evt.getEntity().getKiller();
				PlayerDataClass killerData = plugin.getPlayerHandler()
												   .findPlayer(killer.getDisplayName());
		
				playerData.setDeaths(playerData.getDeaths() + 1);		
				if (killer != null) {
					killerData.setKills(killerData.getKills() + 1);
				}
			}
			
			playerData.setTeam("spec");

			plugin.getGameManager().teleportToSpawn(player);
			TagAPI.refreshPlayer(player);
		}	
		
	} 
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		
		PlayerDataClass playerData = plugin.getPlayerHandler().findPlayer(e.getPlayer().getDisplayName());
		
		if (playerData == null) {
			e.setCancelled(true);
		} else {
			if (playerData.getTeam().equals("ghost") || playerData.getTeam().equals("player")) {
				e.setCancelled(true);
				return;
			}
			if (playerData.getTeam().equals("spec") && (e.getPlayer().isOp() ||
					playerData.getRank() > 90)) {
				plugin.getLogger().info("admin breaking");
				return;
			}
			e.setCancelled(true);
		}
	}
}
