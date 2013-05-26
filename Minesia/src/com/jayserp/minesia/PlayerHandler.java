package com.jayserp.minesia;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.kitteh.tag.TagAPI;

public class PlayerHandler {
	
	private Minesia plugin;
	
	public PlayerHandler(Minesia plugin) {
		this.plugin = plugin;
	}
	
	public void setClass (String playerName, String playerClass) {
		PlayerDataClass player = findPlayer(playerName);
		player.setType(playerClass);
	}
	
	public void addSpecPlayer (String playerName, int rank) {
		PlayerDataClass player = findPlayer(playerName);
		
		if (player == null) {		
			PlayerDataClass e = new PlayerDataClass();
			e.setName(playerName);
			e.setTeam("spec");
			e.setScore(0);
			e.setType(null);
			e.setRank(rank);
			plugin.getUserList().add(e);
			plugin.getGameManager().teleportToSpawn(plugin.getServer().getPlayer(playerName));
		} else {
			plugin.getUserList().get(plugin.getPlayerHandler().findPlayerId(playerName)).setRank(rank);
			plugin.getUserList().get(plugin.getPlayerHandler().findPlayerId(playerName)).setTeam("spec");
			plugin.getGameManager().teleportToSpawn(plugin.getServer().getPlayer(playerName));
		}
	}
	
	public void addSpecPlayer (String playerName) {
		PlayerDataClass playerData = findPlayer(playerName);
		Player player = plugin.getServer().getPlayer(playerName);
		
		player.setScoreboard(plugin.getScoreboard());
		
		if (plugin.getPlayerTeam().hasPlayer(player) || plugin.getGhostTeam().hasPlayer(player)) {
			plugin.getPlayerTeam().removePlayer(player);
			plugin.getGhostTeam().removePlayer(player);
		}
		
		Player p = plugin.getServer().getPlayer(playerName);
		p.getInventory().setHelmet(null);
		
		p.setHealth(20);
		p.setExhaustion(20);
		p.setFoodLevel(20);

		p.removePotionEffect(PotionEffectType.INVISIBILITY);
		p.removePotionEffect(PotionEffectType.SLOW);
		p.removePotionEffect(PotionEffectType.NIGHT_VISION);
		if (playerData == null) {
			PlayerDataClass e = new PlayerDataClass();
			e.setName(playerName);
			e.setTeam("spec");
			e.setScore(0);
			e.setType(null);
			if (e.getRank() < 0) {
				e.setRank(0);
			}
			plugin.getUserList().add(e);
			plugin.getGameManager().teleportToSpawn(plugin.getServer().getPlayer(playerName));
		} else {
			plugin.getGameManager().teleportToSpawn(plugin.getServer().getPlayer(playerName));
			plugin.getServer().broadcastMessage(ChatColor.YELLOW + playerName + ChatColor.WHITE +
					" is now spectating.");
			plugin.getUserList().get(findPlayerId(playerName)).setTeam("spec");
			//plugin.getUserList().get(findPlayerId(playerName)).setType(null);
		}
	}
	
	public void addGhost(String playerName) {
		//add a ghost to the user list.	
		//PlayerDataClass player = findPlayer(playerName);	
		Player player = plugin.getServer().getPlayer(playerName);
		player.setScoreboard(plugin.getScoreboard());
		plugin.getGhostTeam().addPlayer(player);		
		plugin.getUserList().get(findPlayerId(playerName)).setTeam("ghost");

		//plugin.getUserList().get(findPlayerId(playerName)).setType(type);
				
		plugin.getServer().broadcastMessage(ChatColor.YELLOW + playerName + ChatColor.WHITE +
				" is the ghost!");
		    		
		/*if (plugin.getGameManager().hasGameStarted() == true) {
			spawnGhost(ghost);
		}*/
		//refreshTagsFor(plugin.getServer().getPlayer(playerName));		
	}
	
	public void addPlayer(String playerName) {
		//add a player to the user list.	
		PlayerDataClass playerData = findPlayer(playerName);
		Player player = plugin.getServer().getPlayer(playerName);
		player.setScoreboard(plugin.getScoreboard());
		plugin.getPlayerTeam().addPlayer(player);
		
		plugin.getUserList().get(findPlayerId(playerName)).setTeam("player");
		//plugin.getUserList().get(findPlayerId(playerName)).setType(type);
				
		//plugin.getServer().broadcastMessage(ChatColor.YELLOW + playerName + ChatColor.WHITE +
		//		" joined the human team!");
		    		
		//if (plugin.getGameManager().hasGameStarted() == true) {
			spawnPlayer(playerData);
		//}
		//refreshTagsFor(plugin.getServer().getPlayer(playerName));
	}
		
	public List<PlayerDataClass> getHumanPlayers() {
		List<PlayerDataClass> returnList = new ArrayList<PlayerDataClass>();
		
		for (int i = 0; i < plugin.getUserList().size(); i++) {
			PlayerDataClass player = plugin.getUserList().get(i);
			if (player.getTeam() == "player") {					
				returnList.add(player);
			}		
		}
		return returnList;
	}
	
	public List<PlayerDataClass> getGhostPlayers() {
		List<PlayerDataClass> returnList = new ArrayList<PlayerDataClass>();

		for (int i = 0; i < plugin.getUserList().size(); i++) {
			PlayerDataClass player = plugin.getUserList().get(i);
			if (player.getTeam() == "ghost") {
				returnList.add(player);
			}
		}
		return returnList;
	}
	
	public void removePlayer(String playerName) {		
		for (int i = 0; i < plugin.getUserList().size(); i++) {
			PlayerDataClass temp = plugin.getUserList().get(i);
			
			if (temp.getName() == playerName) {
				plugin.getServer().broadcastMessage(playerName + " has left the game.");
				plugin.getServer().getPlayer(playerName).getInventory().setArmorContents(null);
				plugin.getServer().getPlayer(playerName).getInventory().clear();
				plugin.getUserList().remove(i);
				plugin.getCustomTab().updateTab();
			}
		}
	}
	
	public void clearInventories() {
		for (int i = 0; i < plugin.getServer().getOnlinePlayers().length; i++) {
			Player player = plugin.getServer().getOnlinePlayers()[i];
				player.getInventory().clear();
		}
	}
	
	public void removeAllPlayers() {		
		for (int i = 0; i < plugin.getUserList().size(); i++) {
				plugin.getUserList().remove(i);
		}
	}
	
	public PlayerDataClass findPlayer(String playerName) {
		if (plugin.getUserList() != null) {
    		for (int i = 0; i < plugin.getUserList().size(); i++) {
    			PlayerDataClass temp = plugin.getUserList().get(i);
    			
    			if (temp.getName() == playerName) {
    				return temp;
    			}
    		}
		} 
		return null;
	}
	
	public int findPlayerId(String playerName) {
		if (plugin.getUserList() != null) {
    		for (int i = 0; i < plugin.getUserList().size(); i++) {
    			PlayerDataClass temp = plugin.getUserList().get(i);
    			
    			if (temp.getName() == playerName) {
    				return i;
    			}
    		}
		} 
		return -1;
	}
	
	public void spawnPlayer(PlayerDataClass player) {
		if (player != null) {
			setupPlayer(player.getName());
		}
	}
	
	public void spawnGhost(String player) {
		if (player != null) {
			setupGhost(player);
		}
	}
	
	private void setupGhost(String player) {
		Player p = plugin.getServer().getPlayer(player);
		Location location;
		location = new Location(plugin.getServer().getWorld("world"), -949,7,-107);
		
		p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 10000, 1));
		p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 10000, 1));
		
		PlayerInventory inventory = plugin.getServer().getPlayer(player).getInventory();//call the players inventory
        String headName = "SirCutie"; //set the name of the player who used the command to the String variable playername
        ItemStack head;
        HeadSet HeadSetObject = new HeadSet();
        head = HeadSetObject.headSet(headName);
        inventory.setHelmet(head);//adds the head to their inventory
		p.teleport(location);
		giveGhostItems(player);
	}
	
	public void setupPlayer(String player) {
		PlayerDataClass playerData = findPlayer(player);
		
		Location location;
		location = new Location(plugin.getServer().getWorld("world"), -997,11,128);			
		plugin.getServer().getPlayer(player).teleport(location);

		if (playerData.getClass() != null) {
			givePlayerItems(player, playerData.getType());
		} else {
			givePlayerItems(player, "priest"); 	
		}
	}
	
	public void givePlayerItems(String player, String playerClass) {
		
		List<ItemStack> inv = new ArrayList<ItemStack>();				
		inv.add(new ItemStack(Material.GOLDEN_APPLE, 2));
		inv.add(new ItemStack(Material.EMERALD, 3));

		if (playerClass.equalsIgnoreCase("priest")) {
			inv.add(new ItemStack(Material.BOOK, 1));
		}
		if (playerClass.equalsIgnoreCase("ninja")) {
			inv.add(new ItemStack(Material.IRON_SWORD, 1));
		}
		if (playerClass.equalsIgnoreCase("medic")) {
			inv.add(new ItemStack(Material.PAPER, 1));
		}
		
		ItemStack[] newStack = inv.toArray(new ItemStack[inv.size()]);
		
		plugin.getServer().getPlayer(player).getInventory().clear();
		plugin.getServer()
			.getPlayer(player)
			.getInventory()
			.addItem(newStack);
		plugin.getServer()
		.getPlayer(player)
		.updateInventory();	
	}
	
	public void giveGhostItems(String player) {
		
		List<ItemStack> inv = new ArrayList<ItemStack>();
		ItemStack axe = new ItemStack(Material.STONE_AXE, 1);
		//axe.addEnchantment(Enchantment.KNOCKBACK, 2);
		axe.addEnchantment(Enchantment.DURABILITY, 2);
		inv.add(axe);
		

		ItemStack[] newStack = inv.toArray(new ItemStack[inv.size()]);
		
		plugin.getServer().getPlayer(player).getInventory().clear();
		plugin.getServer()
			.getPlayer(player)
			.getInventory()
			.addItem(newStack);
		plugin.getServer()
		.getPlayer(player)
		.updateInventory();	
	}
	
	public void refreshTagsFor(Player player) {
		for (int i = 0; i < plugin.getServer().getOnlinePlayers().length; i++) {
			Player updatedPlayer = plugin.getServer().getOnlinePlayers()[i];
			if (updatedPlayer != player) {
				TagAPI.refreshPlayer(updatedPlayer, player);
			}
		}
	}
	
}