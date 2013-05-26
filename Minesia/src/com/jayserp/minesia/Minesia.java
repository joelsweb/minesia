package com.jayserp.minesia;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import org.kitteh.tag.TagAPI;

import com.jayserp.minesia.CustomTab;
import com.jayserp.minesia.PlayerDataClass;
import com.jayserp.minesia.GameManager;

public class Minesia extends JavaPlugin {
	
	private Minesia plugin = this;
	private GameManager gameManager;
	private PlayerHandler playerHandler;
	private CustomTab customTab;
	//private Database database;
	private PlayerListeners playerListeners;
	private GameTimer gameTimer;
	private ServerHandler serverHandler;
	private ScoreboardManager scoreboardManager;
	private Scoreboard scoreboard;
	private Team playerTeam;
	private Team ghostTeam;
	
	private List<PlayerDataClass> userList; //user list
	
	@Override
	public void onEnable() {
		getLogger().info("Minesia ENABLED");	
		
    	//setup gameManager
		gameManager = new GameManager(this);
		playerHandler = new PlayerHandler(this);
		customTab = new CustomTab(this);
		//database = new Database(this);
		playerListeners = new PlayerListeners(this);
		gameTimer = new GameTimer(this);
		serverHandler = new ServerHandler(this);
		
		//scoreboard manager
		scoreboardManager = plugin.getServer().getScoreboardManager();
		scoreboard = scoreboardManager.getNewScoreboard();
		playerTeam = scoreboard.registerNewTeam("human");
		ghostTeam = scoreboard.registerNewTeam("ghost");
		playerTeam.setCanSeeFriendlyInvisibles(true);
		
		Objective objective = scoreboard.registerNewObjective("test", "dummy");
	    //Setting where to display the scoreboard/objective (either SIDEBAR, PLAYER_LIST or BELOW_NAME)
	    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
	     
	    //Setting the display name of the scoreboard/objective
	    objective.setDisplayName("Display Name");
		
    	//setup user list
    	userList = new ArrayList<PlayerDataClass>();
		
    	getServer().getPluginManager().registerEvents(playerListeners, this);
    	getServer().getPluginManager().registerEvents(serverHandler, this);
    	
		for (int i = 0; i < this.getServer().getOnlinePlayers().length; i++) {
			Player player = this.getServer().getOnlinePlayers()[i];
			getPlayerHandler().addSpecPlayer(player.getDisplayName());
			getLogger().info(player.getDisplayName());
		}
		
		playerHandler.clearInventories();
		gameManager.startGame();
	}
	
	@Override
	public void onDisable() {
		getLogger().info("Minesia DISABLED");	
		//database.closeConnection();
	}
	
    //setup commands
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	
    	String[] split = args;
  
    	if(cmd.getName().equalsIgnoreCase("class")) {
    		if(sender instanceof Player == true) {
    			if (split.length > 1) {
    				sender.sendMessage("Too many arguments!");
    				return true;
    		    } 
    			if (split.length < 1) {
 		           sender.sendMessage("Select a class: /class <class>");
 		           sender.sendMessage("Classes available: priest, ninja, medic");
 		           return true;
 		        } 
    			if (split[0].equalsIgnoreCase("priest") ||
    				split[0].equalsIgnoreCase("ninja") ||
    				split[0].equalsIgnoreCase("medic")) {
	       			getLogger().info(sender.getName() + " set to " + split[0]);
	       			sender.sendMessage("Class set to " + split[0]);
	        		playerHandler.setClass(sender.getName(), split[0]);
	        		plugin.getCustomTab().updateTab();
	        		TagAPI.refreshPlayer((Player) sender);
    			} else {
    				sender.sendMessage("Classes available: priest, ninja, medic");
    			}
			} else {
				sender.sendMessage("Sorry but this command is only for players");
			}
    		return true;
    	}
    	
    	if(cmd.getName().equalsIgnoreCase("leave")) { 
    		if(sender instanceof Player == true) {
    			playerHandler.addSpecPlayer(sender.getName());
    			//playerHandler.removePlayer(sender.getName());
    			//gameManager.teleportToSpawn((Player) sender);
    			plugin.getCustomTab().updateTab();
    		} else {
    			sender.sendMessage("Sorry but this command is only for players");
    		}
    		return true;
    	}
    	
    	if(cmd.getName().equalsIgnoreCase("start")) { 
    		gameManager.startGame();
    		return true;
    	}
    	
    	if(cmd.getName().equalsIgnoreCase("reset")) {
    		gameManager.resetGame();
    		return true;
    	}
    	
    	if(cmd.getName().equalsIgnoreCase("js")) {
    		//UsersDataClass data = database.getUser("jayserps");
			//plugin.getLogger().info(String.valueOf(data.getId()));
    		//database.closeConnection();	
			return true;
    	}
    	
    	if(cmd.getName().equalsIgnoreCase("lp")) { 
    		String ghostList = null;
    		String playerList = null;
    		String specList = null;
    		for (int i = 0; i < userList.size(); i++) {
    			PlayerDataClass e = userList.get(i);
    			if (e != null) {
    				if (e.getTeam() == "ghost") {
    					if (ghostList == null) {
    						ghostList = "[" + e.getType() + "] " + e.getName()  + "(" + e.getRank() + ")";
    					} else {
    						ghostList = ghostList + ", [" + e.getType() + "] " + e.getName() + "(" + e.getRank() + ")";
    					}
    				}
    				if (e.getTeam() == "player") {
    					if (playerList == null) {
    						playerList = "[" + e.getType() + "] " + e.getName() + "(" + e.getRank() + ")";
    					} else {
    						playerList = playerList + ", [" + e.getType() + "] " + e.getName() + "(" + e.getRank() + ")";
    					}
    				}
    				if (e.getTeam() == "spec") {
    					if (specList == null) {
    						specList = "[" + e.getType() + "] " +  e.getName() + "(" + e.getRank() + ")";
    					} else {
    						specList = specList + ", [" + e.getType() + "] " + e.getName() + "(" + e.getRank() + ")";
    					}
    				}
    			}
    		}
			plugin.getServer().broadcastMessage(ChatColor.RED + "Ghost Team : " + ghostList);
			plugin.getServer().broadcastMessage(ChatColor.BLUE + "Player Team: " + playerList);
			plugin.getServer().broadcastMessage(ChatColor.GRAY + "Spectators: " + specList);
    		return true;
    	}
    	return false; 
    }
	
	public Minesia getPlugin() {
		return plugin;
	}
	
	public GameManager getGameManager() {
		return gameManager;
	}
	
    public List<PlayerDataClass> getUserList() {
    	return userList;
    }
    
    public PlayerHandler getPlayerHandler() {
    	return playerHandler;
    }
    
    public CustomTab getCustomTab() {
    	return customTab;
    }
    
    public Team getPlayerTeam() {
    	return playerTeam;
    }
    
    public Team getGhostTeam() {
    	return ghostTeam;
    }
    
    public Scoreboard getScoreboard() {
    	return scoreboard;
    }
    
    /*public Database getSqlDb() {
    	return database;
    }*/
}